package com.example.bookanalyzer.analyzer

import android.content.Context
import com.example.bookanalyzer.analyzer.book_text_parser.*
import kotlin.math.roundToInt

class BookAnalyzer(private val ctx: Context) {
    companion object {
        private const val EPUB_ENDING = ".epub"
        private const val FB2_ENDING = ".fb2"
    }

    fun getAnalysis(path: String): AnalyzedBookModel {
        val textParser = getBookTextParser(path)
        val wordsParser = WordsParser()
        val normalizer = WordNormalizer(ctx)

        val parsedFileData = textParser.parseFile(path)
        val sourceWordMap = wordsParser.getWordMap(parsedFileData.text)

        val normalizedWordMap = normalizeWordMap(normalizer, sourceWordMap)
        val charCount = parsedFileData.text.length
        val wordCount = calculateWordCount(sourceWordMap)
        val avgWordLen = calculateAvgWordLen(wordCount, sourceWordMap)
        val avgSentenceLenPair =
            calculateAvgSentenceLenPair(wordCount, parsedFileData.text)
        val uniqueWordCount = normalizedWordMap.size

        return AnalyzedBookModel(
            path = path,
            uniqueWordCount = uniqueWordCount,
            allWordCount = wordCount,
            allCharCount = charCount,
            avgSentenceLenInWrd = avgSentenceLenPair.first,
            avgSentenceLenInChr = avgSentenceLenPair.second,
            avgWordLen = avgWordLen,
            wordMap = normalizedWordMap
        )
    }

    private fun getBookTextParser(path: String): BookTextParser {
        return when {
            path.contains(EPUB_ENDING) -> EpubTextParser()
            path.contains(FB2_ENDING) -> Fb2TextParser()
            else -> TxtTextParser()
        }
    }

    private fun calculateWordCount(sourceWordMap: MutableMap<String, Int>): Int {
        var wordCount = 0
        for ((word, count) in sourceWordMap) {
            wordCount += count
        }
        return wordCount
    }

    private fun calculateAvgWordLen(
        wordCount: Int,
        sourceWordMap: MutableMap<String, Int>
    ): Double {
        var sumLen: Long = 0
        for ((word, count) in sourceWordMap) {
            sumLen += word.length * count
        }
        var avgWordLen = 0.0
        if (wordCount != 0) {
            avgWordLen = roundDouble(sumLen.toDouble() / wordCount)
        }
        return avgWordLen
    }

    private fun calculateAvgSentenceLenPair(
        wordCount: Int,
        simpleText: String
    ): Pair<Double, Double> {
        val charCount = simpleText.length
        val sentences = simpleText.split(".")
        val sentenceCount = sentences.size
        val avgSentenceLenWrd = if (sentenceCount != 0) {
            (roundDouble(wordCount.toDouble() / sentenceCount))
        } else {
            (0.0)
        }
        val avgSentenceLenChr = if (wordCount != 0) {
            (roundDouble((charCount.toDouble()) / sentenceCount))
        } else {
            (0.0)
        }
        return (avgSentenceLenWrd to avgSentenceLenChr)
    }

    private fun roundDouble(d: Double): Double {
        return ((d * 100).roundToInt().toDouble() / 100)
    }

    private fun normalizeWordMap(
        normalizer: WordNormalizer,
        sourceMap: MutableMap<String, Int>
    ): Map<String, Int> {
        val normalizedMap = mutableMapOf<String, Int>()
        for ((word, count) in sourceMap) {
            val wordBase = normalizer.getWordBase(word)
            if (wordBase != null) {
                normalizedMap[wordBase] = (normalizedMap[wordBase] ?: 0) + count
            }
        }
        return normalizedMap.toList().sortedBy { it.second }.reversed().toMap()
    }
}

class AnalyzedBookModel(
    var path: String,
    var uniqueWordCount: Int,
    var allWordCount: Int,
    var allCharCount: Int,
    var avgSentenceLenInWrd: Double,
    var avgSentenceLenInChr: Double,
    var avgWordLen: Double,
    var wordMap: Map<String, Int>
)