package com.example.bookanalyzer.analyzer

import android.content.Context
import kotlin.math.roundToInt

class BookAnalysis(private val ctx:Context)
{
    fun getAnalysis(path:String ): AnalyzedBookModel {
        val parser = BookParser()
        val normalizer = WordNormalizer(ctx)
        val parserInfo = parser.parseFile(path)
        val sourceWordMap = parser.parseWords(parserInfo.text)
        val normalizedWordMap = normalizeWordMap(normalizer, sourceWordMap)

        val charCount = parserInfo.text.length
        val wordCount = calcWordCount(sourceWordMap)
        val avgWordLen = calcAvgWordLen(wordCount, sourceWordMap)
        val avgSentenceLen:Pair<Double,Double> = calcAvgSentenceLen(wordCount, parserInfo.text)
        val uniqueWordCount = normalizedWordMap.size

        return AnalyzedBookModel(path, uniqueWordCount, wordCount, charCount, avgSentenceLen.first,
                                avgSentenceLen.second, avgWordLen, normalizedWordMap)
    }

    private fun calcWordCount(sourceWordMap: MutableMap<String, Int>) : Int{
        var ans:Int = 0
        for ((word, count) in sourceWordMap) {
            ans += count
        }
        return ans
    }

    private fun calcAvgWordLen(wordCount: Int,sourceWordMap: MutableMap<String, Int>) : Double{
        var sumLen:Long = 0
        for ((a, b) in sourceWordMap) {
            sumLen += a.length * b
        }
        return (if (wordCount != 0) roundDouble(sumLen.toDouble()/ wordCount) else 0.0)
    }

    private fun calcAvgSentenceLen(wordCount:Int, simpleText:String) : Pair<Double,Double>{
        val charCount = simpleText.length
        val sentences = simpleText.split(".")
        val sentenceCount = sentences.size
        val avgSentenceLenWrd = if (sentenceCount != 0) roundDouble(wordCount.toDouble() / sentenceCount) else 0.0
        val avgSentenceLenChr = if (wordCount != 0) roundDouble((charCount.toDouble()) / sentences.size) else 0.0
        return (avgSentenceLenWrd to avgSentenceLenChr)
    }

    private fun roundDouble(d:Double):Double{
        return ((d * 100).roundToInt().toDouble() / 100)
    }

    private fun normalizeWordMap(normalizer: WordNormalizer, sourceMap:MutableMap<String,Int>):Map<String,Int>{
        val ansMap = mutableMapOf<String,Int>()
        for ((a, b) in sourceMap){
            val newWord = normalizer.getLemma(a)
            if (newWord != null){
                ansMap[newWord] = (ansMap[newWord]?:0) + b
            }
        }
        return (ansMap.toList().sortedBy { it.second }.reversed().toMap())
    }

}

class AnalyzedBookModel(var path:String,
                        var uniqueWordCount:Int,
                        var allWordCount:Int,
                        var allCharCount:Int,
                        var avgSentenceLenInWrd:Double,
                        var avgSentenceLenInChr:Double,
                        var avgWordLen:Double,
                        var wordMap:Map<String,Int>)
{

}