package com.example.bookanalyzer

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.widget.TextView
import java.io.InputStream
import java.text.Normalizer
import kotlin.math.roundToInt


class BookAnalysis(private val inStream: InputStream, val path:String, val ctx:Context)

{
    var avgWordLen:Double = 0.0
    var avgSentenceLen:Double= 0.0
    var wordCount = 0
    var uniqWordCount = 0
    var img:ByteArray? = null
    lateinit var normalizedWordMap:Map<String,Int>

    fun doit() {
        val parser = BookParser()
        val time1 = System.currentTimeMillis()
        val normalizer = WordNormalizer(ctx)
        val time2 = System.currentTimeMillis()
        val simpleText = parser.parseFile(inStream, path)?:""
        if (simpleText.isEmpty()){
            normalizedWordMap = mapOf()
            return
        }
        val time3 = System.currentTimeMillis()
        img = parser.img
        val sourceWordMap = parser.parseWords(simpleText)
        normalizedWordMap = normalizeWordMap(normalizer, sourceWordMap)
        val time4 = System.currentTimeMillis()
        calcWordCount(sourceWordMap)
        calcAvgWordLen(sourceWordMap)
        calcAvgSentenceLen(simpleText)
        val time5 = System.currentTimeMillis()
        uniqWordCount = normalizedWordMap.size
        println("t norm=" + ((time2- time1).toDouble() / 1000 ).toString())
        println("t pars=" + ((time3- time2).toDouble() / 1000 ).toString())
        println("t normmap=" + ((time4- time3).toDouble() / 1000 ).toString())
        println("t last=" + ((time5- time4).toDouble() / 1000 ).toString())
        println("t all=" + ((time5- time1).toDouble() / 1000 ).toString())
        println(uniqWordCount)
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
    }

    private fun roundDouble(d:Double):Double{
        return ((d * 100).roundToInt().toDouble() / 100)
    }

    private fun normalizeWordMap(normalizer:WordNormalizer, sourceMap:MutableMap<String,Int>):Map<String,Int>{
        val ansMap = mutableMapOf<String,Int>()
        for ((a, b) in sourceMap){
            val newWord = normalizer.getLemma(a)
            if (newWord == null){
                ansMap[a] = (ansMap[a]?:0) + b
            }else{
                ansMap[newWord] = (ansMap[newWord]?:0) + b
            }
        }
        return (ansMap.toList().sortedBy { it.second }.reversed().toMap())
    }
}