package com.example.bookanalyzer.analyzer

import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import java.io.*
import java.util.*


class ParserInfo(var text: String = "")

class BookParser() {
    fun parseFile(path: String): ParserInfo {
        val inStream: InputStream = FileInputStream(path)
        return when {
            path.contains(".txt") -> parseTxt(inStream)
            path.contains(".epub") -> parseEpub(inStream)
            path.contains(".fb2") -> parseFb2(inStream)
            else -> parseTxt(inStream)
        }
    }

    fun parseWords(text: String): MutableMap<String, Int> {
        val reg = "\\s*\\s|,|>|<|“|—|\\[|]|!|;|:|”|/|\\*|-|…|\\)|\\(|\\?|\\.|\"\\s*"
        val words = text.split(reg.toRegex()).toTypedArray()
        val map = mutableMapOf<String, Int>()

        for (word in words) {
            if (word.isNotEmpty() && !isNumber(word)) {
                val lowerWord = word.toLowerCase(Locale.ROOT)
                map[lowerWord] = 1 + (map[lowerWord] ?: 0)
            }
        }
        return (map)
    }

    private fun parseTxt(inStream: InputStream): ParserInfo {
        val inputReader = InputStreamReader(inStream)
        val buffReader = BufferedReader(inputReader)
        val parserInfo = ParserInfo()

        try {
            parserInfo.text = buffReader.readText()
        } catch (e: IOException) {
            println(e)
        }
        return (parserInfo)
    }

    private fun parseEpub(inStream: InputStream): ParserInfo {
        val book: Book = EpubReader().readEpub(inStream)
        val htmlText = java.lang.StringBuilder()
        val parserInfo = ParserInfo()

        for (content in book.contents) {
            val text = content.reader.readText()
            htmlText.append(text)
        }
        val doc = Jsoup.parse(htmlText.toString())
        val htmlElements = doc.body()
        val simpleText = StringBuilder("")

        val divTexts = htmlElements.select("div")
        val pTexts = htmlElements.select("p")
        if (divTexts.size > pTexts.size) {
            for (divText in divTexts) {
                simpleText.append(divText.text() + "\n")
            }
        } else {
            for (pText in pTexts) {
                simpleText.append(pText.text() + "\n")
            }
        }
        parserInfo.text = simpleText.toString()
        return parserInfo
    }

    private fun parseFb2(inStream: InputStream): ParserInfo {
        val parserInfo = parseTxt(inStream)
        val unhandledStr = parserInfo.text

        val bodyStart = unhandledStr.indexOf("<body>")
        val bodyEnd = unhandledStr.indexOf("</body>")

        val handledStr = if (bodyStart >= 0 && bodyEnd >= 0) {
            val bodyStr = unhandledStr.substring(bodyStart + "<body>".length, bodyEnd - 1)
            (bodyStr.replace("(\\<(/?[^>]+)>)".toRegex(), ""))
        } else {
            (unhandledStr)
        }
        parserInfo.text = handledStr
        return (parserInfo)
    }

    private fun isNumber(str: String): Boolean {
        str.forEach { if (it.isDigit()) return (true) }
        return (false)
    }
}