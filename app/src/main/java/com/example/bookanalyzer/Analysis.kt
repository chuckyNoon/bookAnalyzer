package com.example.bookanalyzer

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.widget.TextView
import java.io.InputStream
import kotlin.math.roundToInt


class BookAnalysis(private val inStream: InputStream, val path:String, val ctx:Context)

{
    private lateinit var parser:FileParser
    private var normalizer: WordNormalizer? = null

    private var simpleText:String? = null
    lateinit var finalMap:Map<String,Int>
    var avgWordLen:Double = 0.0
    var avgSentenceLen:Double= 0.0
    var wordsCount = 0
    var unWordsCount = 0
    var img:ByteArray? = null

    fun doit() {
        parser = FileParser()
        normalizer = WordNormalizer(ctx)
        simpleText = when{
            path.contains(".txt") -> parser.parseTxt(inStream)
            path.contains(".epub") -> parser.epubToTxt(inStream)
            path.contains(".fb2") ->parser.fb2ToTxt(inStream)
            else -> parser.parseTxt(inStream)
        }
        img = parser.img
        finalMap = normalizeWordMap(parser.parseWords(simpleText!!))
        calcWordCount()
        calcAvgWordLen()
        calcAvgSentenceLen()
        unWordsCount = finalMap.size
        println(unWordsCount)
    }

    private fun calcWordCount(){
        var ans:Int = 0
        finalMap.let {
            for ((a, b) in it) {
                ans += b
            }
        }
        wordsCount = ans
    }

    private fun roundDouble(d:Double):Double{
        return ((d * 100).roundToInt().toDouble() / 100)
    }

    private fun calcAvgWordLen(){
        var sumLen:Long = 0
        finalMap.let {
            for ((a, b) in it) {
                sumLen += a.length * b
            }
        }
        avgWordLen = if (wordsCount != 0)  roundDouble(sumLen.toDouble()/ wordsCount) else 0.0
    }

    private fun calcAvgSentenceLen(){
        val strs = simpleText?.split(".")
        val sentencesCount = strs?.size?:0
        avgSentenceLen = if (sentencesCount != 0) roundDouble(wordsCount.toDouble() / sentencesCount) else 0.0
    }

    private fun normalizeWordMap(sourceMap:MutableMap<String,Int>):Map<String,Int>{
        val ansMap = mutableMapOf<String,Int>()
        for ((a, b) in sourceMap){
            val newWord = normalizer?.getLemma(a)
            if (newWord == null){
                ansMap[a] = (ansMap[a]?:0) + b
            }else{
                ansMap[newWord] = (ansMap[newWord]?:0) + b
            }
        }
        return (ansMap.toList().sortedBy { it.second }.reversed().toMap())
    }
}