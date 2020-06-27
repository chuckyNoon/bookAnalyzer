package com.example.bookanalyzer

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.widget.TextView
import java.io.InputStream
import kotlin.math.roundToInt


class BookAnalysis(private val inStream: InputStream, val path:String, val ctx:Context)

{
    private var parser:FileParser? = null
    private var simpleText:String? = null
    var finalMap:Map<String,Int>? = null
    var avgWordLen:Double = 0.0
    var avgSentenceLen:Double= 0.0
    var wordsPerTwo:Int = 0
    var wordsCount = 0
    var unWordsCount = 0
    var img:ByteArray? = null
    private var normalizer: WordNormalizer? = null
    var intent: Intent?= null

    fun doit() {
        parser = FileParser()
        normalizer = WordNormalizer(ctx)
        simpleText = when{
            path.contains(".txt") -> parser!!.parseTxt(inStream)
            path.contains(".epub") -> parser!!.epubToTxt(inStream)
            else -> parser!!.parseTxt(inStream)
        }
        img = parser?.img
        finalMap = normalizeWordMap(parser!!.parseWords(simpleText!!))
        calcWordCount()
        calcAvgWordLen()
        calcAvgSentenceLen()
        unWordsCount = finalMap?.size ?:0
        println(unWordsCount)
        /* for((a,b) in finalMap!!)
             println("$a $b")*/
    }

    private fun calcWordCount(){
        var ans:Int = 0
        finalMap?.let {
            for ((a, b) in it) {
                ans += b
            }
        }
        wordsCount = ans
    }

    fun roundDouble(d:Double):Double{
        return ((d * 100).roundToInt().toDouble() / 100)
    }

    private fun calcAvgWordLen(){
        var sumLen:Long = 0
        finalMap?.let {
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
            val newWord = normalizer!!.getLemma(a)
            if (newWord == null){
                ansMap[a] = (ansMap[a]?:0) + b
            }else{
                ansMap[newWord] = (ansMap[newWord]?:0) + b
            }
        }
        return (ansMap.toList().sortedBy { it.second }.reversed().toMap())
    }
}