package com.example.bookanalyzer

import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import java.io.*
import java.nio.charset.StandardCharsets

class BookParser(){
    var img:ByteArray? = null
    var author:String? = null
    var bookName:String? = null

    fun parseFile(inStream: InputStream, path:String):String?{
        return when{
            path.contains(".txt") -> parseTxt(inStream)
            path.contains(".epub") -> parseEpub(inStream)
            path.contains(".fb2") -> parseFb2(inStream)
            else -> parseTxt(inStream)
        }
    }

    fun parseWords(text:String):MutableMap<String,Int>{
        val reg = "\\s*\\s|,|>|<|“|—|\\[|]|!|;|:|”|/|\\*|-|…|\\)|\\(|\\?|\\.|\"\\s*"
        val words = text.split(reg.toRegex()).toTypedArray()
        val map = mutableMapOf<String,Int>()

        for (word in words){
            if (word.isNotEmpty()  && !isNumber(word)){
                val lowerWord = word.toLowerCase()
                map[lowerWord] = 1 + (map[lowerWord] ?: 0)
            }
        }
        return (map)
    }

    private fun parseTxt(inStream: InputStream):String?{
        val inputReader = InputStreamReader(inStream)
        val buffReader = BufferedReader(inputReader)

        return try{
           val text = buffReader.readText()
            (text)
        }catch (e: IOException){
            (null)
        }
    }

    private fun parseEpub(inStream: InputStream):String{
        val book: Book = EpubReader().readEpub(inStream)
        val htmlText = java.lang.StringBuilder()
        if (!book.metadata?.authors.isNullOrEmpty()){
            val firstName = book.metadata?.authors?.first()?.firstname
            val secondName = book.metadata?.authors?.first()?.lastname
            author = (firstName?:"") + " " + (secondName?:"")
        }
        println("ff $author")
        bookName = book.title
        img = book.coverImage?.data
        for (elem in book.contents) {
            val text = elem.reader.readText()
            htmlText.append(text)
        }
        val doc = Jsoup.parse(htmlText.toString())
        val htmlElements = doc.body()
        val simpleText = StringBuilder("")
        val texts = htmlElements.select("div")
        val ptexts = htmlElements.select("p")
        if (texts.size > ptexts.size) {
            for (t in texts) {
                simpleText.append(t.text() + "\n")
            }
        }else{
            for(p in ptexts){
                simpleText.append(p.text() +  "\n")
            }
        }
        return (simpleText.toString())
    }

    private fun parseFb2(inStream: InputStream):String?{
        val unhandledStr = parseTxt(inStream) ?: return (null)
        var imgNameStart = unhandledStr.indexOf( "xlink:href=")
        var name = ""
        if (imgNameStart >= 0) {
            imgNameStart += ("xlink:href=").length
            val imgNameEnd = unhandledStr.indexOf("\"", imgNameStart + 1)
            if (imgNameEnd >= 0){
                name = unhandledStr.substring(imgNameStart, imgNameEnd + 1).replace("#","")
            }
        }
        val bodyStart = unhandledStr.indexOf("<body>")
        val bodyEnd = unhandledStr.indexOf("</body>")
        val str = if (bodyStart >= 0 && bodyEnd >= 0){
            val bodyStr = unhandledStr.substring(bodyStart + "<body>".length, bodyEnd - 1)
            bodyStr.replace("(\\<(/?[^>]+)>)".toRegex(),"")
        }else{
            unhandledStr
        }
        val imgStart = unhandledStr.indexOf(">",unhandledStr.indexOf("<binary id=$name")) + 1
        val imgEnd = unhandledStr.indexOf("</binary", imgStart) - 1
        if (imgStart >=0 && imgEnd>= 0) {
            img = unhandledStr.substring(imgStart, imgEnd).toByteArray()
        }
        return (str)
    }

    private fun isNumber(str:String):Boolean{
        str.forEach { if (it.isDigit()) return (true) }
        return (false)
    }

}