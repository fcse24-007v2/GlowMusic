package com.example.glowmusic.domain.usecase

import com.example.glowmusic.domain.model.LyricLine
import com.example.glowmusic.domain.model.LyricWord
import kotlin.math.max

class ParseSyncedLyricsUseCase {

    private val lineTimestampRegex = Regex("\\[(\\d{1,2}:\\d{2}(?:[.:]\\d{1,3})?)\\]")
    private val wordTimestampRegex = Regex("<(\\d{1,2}:\\d{2}(?:[.:]\\d{1,3})?)>")

    fun parse(rawLyrics: String?): List<LyricLine> {
        if (rawLyrics.isNullOrBlank()) return emptyList()

        val parsed = mutableListOf<LyricLine>()

        rawLyrics
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { row ->
                val lineTags = lineTimestampRegex.findAll(row).toList()
                val rowWithoutLineTags = row.replace(lineTimestampRegex, "").trim()

                val words = parseWordSyncedLyrics(rowWithoutLineTags)
                val plainText = rowWithoutLineTags
                    .replace(wordTimestampRegex, "")
                    .replace(Regex("\\s+"), " ")
                    .trim()

                if (lineTags.isNotEmpty()) {
                    lineTags.forEach { tag ->
                        val lineTimeMs = parseTimestampToMs(tag.groupValues[1])
                        if (lineTimeMs >= 0) {
                            parsed += LyricLine(
                                timeMs = lineTimeMs,
                                text = plainText,
                                words = words
                            )
                        }
                    }
                } else if (words.isNotEmpty()) {
                    parsed += LyricLine(
                        timeMs = words.first().timeMs,
                        text = plainText,
                        words = words
                    )
                }
            }

        return parsed.sortedBy { it.timeMs }
    }

    fun findCurrentLineIndex(lines: List<LyricLine>, playbackPositionMs: Long): Int {
        if (lines.isEmpty()) return -1
        val index = lines.indexOfLast { it.timeMs <= playbackPositionMs }
        return max(index, -1)
    }

    fun findCurrentWordIndex(line: LyricLine?, playbackPositionMs: Long): Int {
        if (line == null || line.words.isEmpty()) return -1
        val index = line.words.indexOfLast { it.timeMs <= playbackPositionMs }
        return max(index, -1)
    }

    private fun parseWordSyncedLyrics(content: String): List<LyricWord> {
        val matches = wordTimestampRegex.findAll(content).toList()
        if (matches.isEmpty()) return emptyList()

        val words = mutableListOf<LyricWord>()

        for (i in matches.indices) {
            val current = matches[i]
            val start = current.range.last + 1
            val endExclusive = if (i + 1 < matches.size) matches[i + 1].range.first else content.length
            val token = content.substring(start, endExclusive)
                .replace(wordTimestampRegex, "")
                .replace(Regex("\\s+"), " ")
                .trim()

            val time = parseTimestampToMs(current.groupValues[1])
            if (token.isNotBlank() && time >= 0) {
                words += LyricWord(text = token, timeMs = time)
            }
        }

        return words
    }

    private fun parseTimestampToMs(value: String): Long {
        val clean = value.trim().replace(':', ':').replace('.', ':')
        val parts = clean.split(':')
        if (parts.size < 2) return -1L

        return runCatching {
            val minutes = parts[0].toLong()
            val seconds = parts[1].toLong()
            val fraction = if (parts.size >= 3) parts[2] else "0"
            val fractionMs = when (fraction.length) {
                0 -> 0L
                1 -> fraction.toLong() * 100L
                2 -> fraction.toLong() * 10L
                else -> fraction.take(3).toLong()
            }
            (minutes * 60_000L) + (seconds * 1_000L) + fractionMs
        }.getOrDefault(-1L)
    }
}
