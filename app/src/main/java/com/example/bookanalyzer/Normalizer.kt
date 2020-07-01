package com.example.bookanalyzer

import android.content.Context
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.concurrent.thread


class WordNormalizer(private val ctx: Context)
{
    private val nounMap:MutableMap<String,Int> = mutableMapOf()
    private val verbMap:MutableMap<String,Int> = mutableMapOf()
    private val adjMap:MutableMap<String,Int> = mutableMapOf()
    private val advMap:MutableMap<String,Int> = mutableMapOf()

    private val nounExMap:MutableMap<String,String> = mutableMapOf()
    private val verbExMap:MutableMap<String,String> = mutableMapOf()
    private val adjExMap:MutableMap<String,String> = mutableMapOf()

    private lateinit var nounRules:Array<Pair<String,String>>
    private lateinit var verbRules:Array<Pair<String,String>>
    private lateinit var adjRules:Array<Pair<String,String>>

    init {
        val t1 = thread {parseFile(R.raw.inoun,nounMap, ::insertInMap)}
        val t2 = thread {parseFile(R.raw.iverb,verbMap, ::insertInMap)}
        val t3 = thread {parseFile(R.raw.iadj,adjMap, ::insertInMap)}
        val t4 = thread {parseFile(R.raw.iadv,advMap, ::insertInMap)}

        val tt1 = thread {parseFile(R.raw.noun,nounExMap, ::insertInExMap) }
        val tt2 = thread {parseFile(R.raw.verb,verbExMap, ::insertInExMap)}
        val tt3 = thread {parseFile(R.raw.adj,adjExMap, ::insertInExMap)}

        t1.join()
        t2.join()
        t3.join()
        t4.join()

        tt1.join()
        tt2.join()
        tt3.join()
        initRules()
    }

    private fun initRules(){
        verbRules = arrayOf(
            ("s" to "" ),
            ("ies" to "y" ),
            ("es"  to "e" ),
            ("es"  to ""  ),
            ("ed"  to "e" ),
            ("ed"  to ""  ),
            ("ing" to "e" ),
            ("ing" to ""  ))

        nounRules = arrayOf(
            ("'"   to ""    ),
            ("'s"   to ""    ),
            ("s"    to ""    ),
            ("’s"   to ""    ),
            ("’"    to ""    ),
            ("ses"  to "s"   ),
            ("xes"  to "x"   ),
            ("zes"  to "z"   ),
            ("ches" to "ch"  ),
            ("shes" to "sh"  ),
            ("men"  to "man" ),
            ("ies"  to "y"   ))

        adjRules = arrayOf(
            ("er"  to "" ),
            ("er"  to "e"),
            ("est" to "" ),
            ("est" to "e"))
    }

    private fun getWord(str:String,off:Int):String?{
        var i = off
        var j = i
        while (i < str.length){
            if(str[i].isLetter()){
                j = i
                while (j < str.length && str[j].isLetter())
                    j++
                j--
                break
            }
            i++
        }
        return (if(j > i) str.substring(i..j) else null)
    }

    private fun insertInMap(line:String, map:MutableMap<String,Int> ){
        if (line.isNotEmpty()) {
            val firstWord = getWord(line, 0)//line.split(" ")[0]
            if (firstWord != null)
                map[firstWord] = 1
        }
    }

    private fun insertInExMap(line:String, map:MutableMap<String,String> ){
        if (line.isNotEmpty()){
            val words = line.split(" ")
            if (words.size >= 2)
                map[words[0]] = words[1]
        }
    }

    private fun <T>parseFile(resId: Int, map:MutableMap<String,T>, foo:(String,MutableMap<String,T>) ->Unit){
        val str = readRawTextFile(ctx, resId)
        val lines = str!!.split("\n")
        for (line in lines){
            foo(line, map)
        }
    }

    private fun readRawTextFile(ctx: Context, resId: Int): String? {
        val inputStream: InputStream = ctx.resources.openRawResource(resId)
        ByteArrayOutputStream().use { result ->
            val buffer = ByteArray(1024 * 8)
            var length: Int
            while (inputStream.read(buffer).also { length = it } != -1) {
                result.write(buffer, 0, length)
            }
            return result.toString("UTF-8")
        }
    }

    private fun tryNorm(word:String,map:MutableMap<String,Int>,rules:Array<Pair<String,String>>?) :String?{
        if (rules == null)
            return (null)
        for ((a,b) in rules){
            if (word.endsWith(a)){
                var x = word.substring(0, word.length - a.length)
                x += b
                if (map.contains(x))
                    return (x)
            }
        }
        return (null)
    }

    fun getLemma(word:String):String?{
        when {
            nounExMap.contains(word) -> return (nounExMap[word])
            verbExMap.contains(word) -> return (verbExMap[word])
            adjExMap.contains(word) -> return (adjExMap[word])
        }
        val pre = tryNorm(word, verbMap,verbRules)?:tryNorm(word, adjMap, adjRules)
        if (pre != null)
            return (pre)
        when{
            nounMap.contains(word) -> return (word)
            verbMap.contains(word) -> return (word)
            adjMap.contains(word) -> return (word)
        }
        return (tryNorm(word, nounMap, nounRules)?: if (advMap.contains(word)) word else null)
    }
}