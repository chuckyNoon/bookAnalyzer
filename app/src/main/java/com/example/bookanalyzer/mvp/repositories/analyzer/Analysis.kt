package com.example.bookanalyzer.mvp.repositories.analyzer

import android.content.Context
import java.io.InputStream
import kotlin.math.roundToInt

class BookAnalysis(private val inStream: InputStream, val path:String, val ctx:Context)
{
    var avgWordLen:Double = 0.0
    var avgSentenceLen:Double= 0.0
    var avgSentenceLenChr:Double = 0.0
    var wordCount = 0
    var charCount = 0
    var uniqWordCount = 0
    var author:String? = null
    var bookName:String? = null
    var img:ByteArray? = null
    lateinit var normalizedWordMap:Map<String,Int>

    fun doit() {
        val parser = BookParser()
        val time1 = System.currentTimeMillis()
        val normalizer = WordNormalizer(ctx)

        val simpleText = parser.parseFile(inStream, path)?:""
        val time2 = System.currentTimeMillis()
        if (simpleText.isEmpty()){
            normalizedWordMap = mapOf()
            return
        }
        charCount = simpleText.length

        img = parser.img
        author = parser.author
        bookName = parser.bookName
        val sourceWordMap = parser.parseWords(simpleText)
        val time3 = System.currentTimeMillis()
        normalizedWordMap = normalizeWordMap(normalizer, sourceWordMap)
        val time4 = System.currentTimeMillis()
        calcWordCount(sourceWordMap)
        calcAvgWordLen(sourceWordMap)
        calcAvgSentenceLen(simpleText)

        val time5 = System.currentTimeMillis()
        uniqWordCount = normalizedWordMap.size
        println((time2.toDouble() - time1) / 1000)
        println((time3.toDouble() - time2) / 1000)
        println((time4.toDouble() - time3) / 1000)
        println((time5.toDouble() - time4) / 1000)
        println((time5.toDouble() - time1) / 1000)
    }

    private fun calcWordCount(sourceWordMap: MutableMap<String, Int>){
        var ans:Int = 0
        for ((word, count) in sourceWordMap) {
            ans += count
        }
        wordCount = ans
    }

    private fun calcAvgWordLen(sourceWordMap: MutableMap<String, Int>){
        var sumLen:Long = 0
        for ((a, b) in sourceWordMap) {
            sumLen += a.length * b
        }
        avgWordLen = if (wordCount != 0)  roundDouble(sumLen.toDouble()/ wordCount) else 0.0
    }

    private fun calcAvgSentenceLen(simpleText:String){
        val sentences = simpleText.split(".")
        val sentenceCount = sentences.size
        avgSentenceLen = if (sentenceCount != 0) roundDouble(wordCount.toDouble() / sentenceCount) else 0.0
        if (wordCount != 0)
            avgSentenceLenChr = roundDouble((charCount.toDouble()) / sentences.size)
    }

    private fun roundDouble(d:Double):Double{
        return ((d * 100).roundToInt().toDouble() / 100)
    }

    private fun normalizeWordMap(normalizer: WordNormalizer, sourceMap:MutableMap<String,Int>):Map<String,Int>{
        val ansMap = mutableMapOf<String,Int>()
        for ((a, b) in sourceMap){
            val newWord = normalizer.getLemma(a)
            if (newWord == null){
                //ansMap[a] = (ansMap[a]?:0) + b
            }else{
                ansMap[newWord] = (ansMap[newWord]?:0) + b
            }
        }
        return (ansMap.toList().sortedBy { it.second }.reversed().toMap())
    }
}