package com.example.glowmusic.data.local.scanner

import android.content.Context
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import kotlin.math.min

class Id3LyricsExtractor(
    private val context: Context
) {

    fun extractFromContentUri(contentUri: String): String? {
        val uri = runCatching { Uri.parse(contentUri) }.getOrNull() ?: return null

        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                extractFromStream(input)
            }
        }.getOrNull()
    }

    private fun extractFromStream(input: InputStream): String? {
        val header = ByteArray(10)
        if (input.read(header) != 10) return null
        if (!header.copyOfRange(0, 3)
                .contentEquals(byteArrayOf('I'.code.toByte(), 'D'.code.toByte(), '3'.code.toByte()))
        ) {
            return null
        }

        val version = header[3].toInt() and 0xFF
        val flags = header[5].toInt() and 0xFF
        val tagSize = readSyncSafeInt(header, 6)
        if (tagSize <= 0) return null

        val tagBytes = readFully(input, tagSize)
        if (tagBytes.isEmpty()) return null

        val framesStart = detectFramesStart(version, flags, tagBytes)

        var firstUslt: String? = null

        var offset = framesStart
        while (offset < tagBytes.size) {
            val frameHeaderSize = if (version == 2) 6 else 10
            if (offset + frameHeaderSize > tagBytes.size) break

            val frameId: String
            val frameSize: Int

            if (version == 2) {
                frameId = decodeAscii(tagBytes, offset, 3)
                frameSize = ((tagBytes[offset + 3].toInt() and 0xFF) shl 16) or
                        ((tagBytes[offset + 4].toInt() and 0xFF) shl 8) or
                        (tagBytes[offset + 5].toInt() and 0xFF)
            } else {
                frameId = decodeAscii(tagBytes, offset, 4)
                frameSize = if (version == 4) {
                    readSyncSafeInt(tagBytes, offset + 4)
                } else {
                    readInt32(tagBytes, offset + 4)
                }
            }

            if (frameId.isBlank() || frameId.all { it == '\u0000' }) break
            if (frameSize <= 0) break

            val dataStart = offset + frameHeaderSize
            val dataEnd = dataStart + frameSize
            if (dataEnd > tagBytes.size) break

            val frameData = tagBytes.copyOfRange(dataStart, dataEnd)

            when (frameId) {
                "USLT", "ULT" -> if (firstUslt == null) {
                    firstUslt = parseUslt(frameData)
                }

                "SYLT", "SLT" -> {
                    val sylt = parseSylt(frameData)
                    if (!sylt.isNullOrBlank()) {
                        return sylt
                    }
                }
            }

            offset = dataEnd
        }

        return firstUslt
    }

    private fun parseUslt(frameData: ByteArray): String? {
        if (frameData.size < 5) return null

        val encoding = frameData[0].toInt() and 0xFF
        val textStart = skipDescription(frameData, 4, encoding)
        if (textStart >= frameData.size) return null

        return decodeText(frameData.copyOfRange(textStart, frameData.size), encoding)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }

    private fun parseSylt(frameData: ByteArray): String? {
        if (frameData.size < 7) return null

        val encoding = frameData[0].toInt() and 0xFF
        val timestampFormat = frameData[4].toInt() and 0xFF
        if (timestampFormat != 1) {
            return null
        }

        var pointer = skipDescription(frameData, 6, encoding)
        if (pointer >= frameData.size) return null

        data class Entry(val text: String, val timeMs: Long)

        val entries = mutableListOf<Entry>()

        while (pointer < frameData.size) {
            val textEnd = findTextTerminator(frameData, pointer, encoding)
            if (textEnd < 0) break

            val token = decodeText(frameData.copyOfRange(pointer, textEnd), encoding)
                ?.trim()
                .orEmpty()

            val termSize = terminatorSizeForEncoding(encoding)
            val tsStart = textEnd + termSize
            if (tsStart + 4 > frameData.size) break

            val timestamp = readInt32(frameData, tsStart).toLong().coerceAtLeast(0L)

            if (token.isNotBlank()) {
                entries += Entry(token, timestamp)
            }

            pointer = tsStart + 4
        }

        if (entries.isEmpty()) return null

        val chunked = entries.chunked(8)
        val lines = chunked.map { chunk ->
            val lineStart = chunk.first().timeMs
            val words = chunk.joinToString(" ") { entry ->
                "<${formatLrcTimestamp(entry.timeMs)}>${entry.text}"
            }
            "[${formatLrcTimestamp(lineStart)}]$words"
        }

        return lines.joinToString(separator = "\n").trim().takeIf { it.isNotBlank() }
    }

    private fun skipDescription(frameData: ByteArray, start: Int, encoding: Int): Int {
        val end = findTextTerminator(frameData, start, encoding)
        if (end < 0) return frameData.size
        return min(end + terminatorSizeForEncoding(encoding), frameData.size)
    }

    private fun findTextTerminator(bytes: ByteArray, start: Int, encoding: Int): Int {
        if (start >= bytes.size) return -1

        return if (encoding == 1 || encoding == 2) {
            var i = start
            while (i + 1 < bytes.size) {
                if (bytes[i].toInt() == 0 && bytes[i + 1].toInt() == 0) return i
                i += 2
            }
            -1
        } else {
            var i = start
            while (i < bytes.size) {
                if (bytes[i].toInt() == 0) return i
                i++
            }
            -1
        }
    }

    private fun terminatorSizeForEncoding(encoding: Int): Int {
        return if (encoding == 1 || encoding == 2) 2 else 1
    }

    private fun decodeText(bytes: ByteArray, encoding: Int): String? {
        if (bytes.isEmpty()) return null
        val charset = when (encoding) {
            0 -> Charsets.ISO_8859_1
            1 -> detectUtf16Charset(bytes)
            2 -> Charset.forName("UTF-16BE")
            3 -> Charsets.UTF_8
            else -> Charsets.ISO_8859_1
        }

        return runCatching {
            var textBytes = bytes
            if (encoding == 1 && textBytes.size >= 2) {
                val b0 = textBytes[0].toInt() and 0xFF
                val b1 = textBytes[1].toInt() and 0xFF
                if ((b0 == 0xFF && b1 == 0xFE) || (b0 == 0xFE && b1 == 0xFF)) {
                    textBytes = textBytes.copyOfRange(2, textBytes.size)
                }
            }
            String(textBytes, charset)
                .replace("\u0000", "")
                .trim()
        }.getOrNull()
    }

    private fun detectUtf16Charset(bytes: ByteArray): Charset {
        if (bytes.size >= 2) {
            val b0 = bytes[0].toInt() and 0xFF
            val b1 = bytes[1].toInt() and 0xFF
            if (b0 == 0xFF && b1 == 0xFE) return Charset.forName("UTF-16LE")
            if (b0 == 0xFE && b1 == 0xFF) return Charset.forName("UTF-16BE")
        }
        return Charsets.UTF_16
    }

    private fun readFully(input: InputStream, size: Int): ByteArray {
        val safeSize = size.coerceIn(1, 2 * 1024 * 1024)
        val out = ByteArrayOutputStream(safeSize)
        val buffer = ByteArray(8192)
        var remaining = safeSize

        while (remaining > 0) {
            val read = input.read(buffer, 0, min(buffer.size, remaining))
            if (read <= 0) break
            out.write(buffer, 0, read)
            remaining -= read
        }

        return out.toByteArray()
    }

    private fun readSyncSafeInt(bytes: ByteArray, offset: Int): Int {
        if (offset + 3 >= bytes.size) return 0
        return ((bytes[offset].toInt() and 0x7F) shl 21) or
                ((bytes[offset + 1].toInt() and 0x7F) shl 14) or
                ((bytes[offset + 2].toInt() and 0x7F) shl 7) or
                (bytes[offset + 3].toInt() and 0x7F)
    }

    private fun readInt32(bytes: ByteArray, offset: Int): Int {
        if (offset + 3 >= bytes.size) return 0
        return ByteBuffer.wrap(bytes, offset, 4)
            .order(ByteOrder.BIG_ENDIAN)
            .int
    }

    private fun decodeAscii(bytes: ByteArray, offset: Int, length: Int): String {
        if (offset + length > bytes.size) return ""
        return runCatching {
            String(bytes, offset, length, Charsets.ISO_8859_1)
        }.getOrDefault("")
    }

    private fun detectFramesStart(version: Int, flags: Int, tagBytes: ByteArray): Int {
        val hasExtendedHeader = (flags and 0x40) != 0
        if (!hasExtendedHeader) return 0

        if (tagBytes.size < 4) return 0

        return when (version) {
            3 -> {
                val extSize = readInt32(tagBytes, 0)
                when {
                    extSize <= 0 -> 0
                    4 + extSize <= tagBytes.size -> 4 + extSize
                    extSize <= tagBytes.size -> extSize
                    else -> 0
                }
            }

            4 -> {
                val extSize = readSyncSafeInt(tagBytes, 0)
                if (extSize in 1..tagBytes.size) extSize else 0
            }

            else -> 0
        }
    }

    private fun formatLrcTimestamp(timeMs: Long): String {
        val safe = timeMs.coerceAtLeast(0L)
        val totalSeconds = safe / 1000L
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        val millis = safe % 1000L
        return "%02d:%02d.%03d".format(minutes, seconds, millis)
    }
}
