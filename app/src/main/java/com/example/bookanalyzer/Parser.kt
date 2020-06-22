package com.example.bookanalyzer

import android.content.Context
import android.graphics.Bitmap
import com.kursx.parser.fb2.Element
import com.kursx.parser.fb2.FictionBook
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.*
import kotlin.math.ceil
import kotlin.math.floor

enum class Format{
    FB2,TXT,EPUB
}

class FileParser(){
    var img:ByteArray? = null
    private fun isNumber(str:String):Boolean{
        for (ch in str){
            if(ch.isDigit())
                return (true)
        }
        return (false)
    }

    fun parseWords(text:String):MutableMap<String,Int>{
        val reg = "\\s*\\s|,|>|<|“|—|\\[|]|!|;|:|”|/|\\*|-|…|\\)|\\(|\\?|\\.|\"\\s*"
        val reg1 = "\\W+"
        val words = text.split(reg.toRegex()).toTypedArray()
        val map = mutableMapOf<String,Int>()

        for (word in words){
            if (word != ""  && !isNumber(word)){
                val lowerWord = word.toLowerCase()
                map[lowerWord] = 1 + (map[lowerWord] ?: 0)
            }
        }
        return (map)
    }

    fun parseTxt(inStream: InputStream):String?{
        val inputreader = InputStreamReader(inStream)
        val buffreader = BufferedReader(inputreader)
        var line: String? = ""
        val text = java.lang.StringBuilder()
        try {
            while (buffreader.readLine().also({ line = it }) != null) {
                text.append(line)
                text.append('\n')
            }
        } catch (e: IOException) {
            return null
        }
        return (text.toString())
    }

    fun epubToTxt(inStream: InputStream):String{
        val book: Book = EpubReader().readEpub(inStream)
        var htmlText = java.lang.StringBuilder()

        img = book.coverImage.data
        for (elem in book.contents) {
            val st = elem.reader.readText()
            htmlText = htmlText.append(st)
        }
        //println(htmlText)
        val doc = Jsoup.parse(htmlText.toString())
        val htmlElements = doc.body()
        var simpleText = StringBuilder("")
        val texts = htmlElements.select("div")
        val ptexts = htmlElements.select("p")
        if (texts.size > ptexts.size) {
            for (t in texts) {
                simpleText.append(t.text())
            }
        }else{
            for(p in ptexts){
                simpleText.append(p.text())
            }
        }
        return (simpleText.toString())
    }
}