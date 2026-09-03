package com.example.glowmusic.data.local.scanner

import android.content.Context
import android.net.Uri
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.Locale

class FlacLyricsExtractor(
    private val context: Context
) {

    private val lyricTagKeys = setOf(
        "LYRICS",
        "LYRIC",
        "UNSYNCEDLYRICS",
        "UNSYNCED LYRICS",
        "SYNCEDLYRICS",
        "SYNCED LYRICS",
        "LRC"
    )

    fun extractFromContentUri(contentUri: String): String? {
        val uri = runCatching { Uri.parse(contentUri) }.getOrNull() ?: return null

        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                extractFromStream(input)
            }
        }.getOrNull()
    }

    private fun extractFromStream(input: InputStream): String? {
        if (!moveToFlacStart(input)) return null

        var isLast = false
        while (!isLast) {
            val blockHeader = ByteArray(4)
            if (input.read(blockHeader) != 4) break

            val firstByte = blockHeader[0].toInt() and 0xFF
            isLast = (firstByte and 0x80) != 0
            val blockType = firstByte and 0x7F
            val blockLength = ((blockHeader[1].toInt() and 0xFF) shl 16) or
                    ((blockHeader[2].toInt() and 0xFF) shl 8) or
                    (blockHeader[3].toInt() and 0xFF)

            if (blockType == 4) {
                val data = readFully(input, blockLength)
                if (data.size < blockLength) break
                val lyrics = parseVorbisCommentBlock(data)
                if (!lyrics.isNullOrBlank()) return lyrics
            } else {
                if (!skipFully(input, blockLength.toLong())) break
            }
        }

        return null
    }

    private fun moveToFlacStart(input: InputStream): Boolean {
        val first4 = ByteArray(4)
        if (input.read(first4) != 4) return false

        if (first4.contentEquals(
                byteArrayOf(
                    'f'.code.toByte(),
                    'L'.code.toByte(),
                    'a'.code.toByte(),
                    'C'.code.toByte()
                )
            )
        ) {
            return true
        }

        if (first4[0] == 'I'.code.toByte() && first4[1] == 'D'.code.toByte() && first4[2] == '3'.code.toByte()) {
            val rest = ByteArray(6)
            if (input.read(rest) != 6) return false
            val id3Header = first4 + rest
            val id3Size = readSyncSafeInt(id3Header, 6)
            if (id3Size > 0 && !skipFully(input, id3Size.toLong())) return false

            val marker = readFully(input, 4)
            return marker.size == 4 && marker.contentEquals(
                byteArrayOf(
                    'f'.code.toByte(),
                    'L'.code.toByte(),
                    'a'.code.toByte(),
                    'C'.code.toByte()
                )
            )
        }

        return false
    }

    private fun parseVorbisCommentBlock(data: ByteArray): String? {
        var offset = 0

        fun readLeInt(): Int {
            if (offset + 4 > data.size) return -1
            val value = (data[offset].toInt() and 0xFF) or
                    ((data[offset + 1].toInt() and 0xFF) shl 8) or
                    ((data[offset + 2].toInt() and 0xFF) shl 16) or
                    ((data[offset + 3].toInt() and 0xFF) shl 24)
            offset += 4
            return value
        }

        val vendorLength = readLeInt()
        if (vendorLength < 0 || offset + vendorLength > data.size) return null
        offset += vendorLength

        val commentCount = readLeInt()
        if (commentCount < 0) return null

        var firstUnsyncedCandidate: String? = null

        repeat(commentCount) {
            val length = readLeInt()
            if (length <= 0 || offset + length > data.size) return@repeat

            val comment = String(data, offset, length, StandardCharsets.UTF_8)
            offset += length

            val separator = comment.indexOf('=')
            if (separator <= 0) return@repeat

            val key = comment.substring(0, separator).trim().uppercase(Locale.US)
            val value = comment.substring(separator + 1).trim()
            if (value.isBlank()) return@repeat

            if (key in lyricTagKeys) {
                val normalized = normalizeLyrics(value)
                if (isLikelySynced(normalized)) {
                    return normalized
                }
                if (firstUnsyncedCandidate == null) {
                    firstUnsyncedCandidate = normalized
                }
            }
        }

        return firstUnsyncedCandidate
    }

    private fun normalizeLyrics(value: String): String {
        return value
            .replace("\\r\\n", "\n")
            .replace("\\n", "\n")
            .replace('\r', '\n')
            .trim()
    }

    private fun isLikelySynced(value: String): Boolean {
        if (value.contains(Regex("\\[\\d{1,2}:\\d{2}(?:[.:]\\d{1,3})?]"))) return true
        if (value.contains(Regex("<\\d{1,2}:\\d{2}(?:[.:]\\d{1,3})?>"))) return true
        return false
    }

    private fun readSyncSafeInt(bytes: ByteArray, offset: Int): Int {
        if (offset + 3 >= bytes.size) return 0
        return ((bytes[offset].toInt() and 0x7F) shl 21) or
                ((bytes[offset + 1].toInt() and 0x7F) shl 14) or
                ((bytes[offset + 2].toInt() and 0x7F) shl 7) or
                (bytes[offset + 3].toInt() and 0x7F)
    }

    private fun readFully(input: InputStream, size: Int): ByteArray {
        val safeSize = size.coerceIn(0, 1_048_576)
        val output = ByteArray(safeSize)
        var offset = 0

        while (offset < safeSize) {
            val read = input.read(output, offset, safeSize - offset)
            if (read <= 0) break
            offset += read
        }

        return if (offset == safeSize) output else output.copyOf(offset)
    }

    private fun skipFully(input: InputStream, bytesToSkip: Long): Boolean {
        var remaining = bytesToSkip
        while (remaining > 0L) {
            val skipped = input.skip(remaining)
            if (skipped > 0L) {
                remaining -= skipped
                continue
            }

            val single = input.read()
            if (single == -1) return false
            remaining -= 1L
        }
        return true
    }
}
