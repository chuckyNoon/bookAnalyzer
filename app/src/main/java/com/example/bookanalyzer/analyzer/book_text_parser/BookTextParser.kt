package com.example.bookanalyzer.analyzer.book_text_parser

import java.io.*

class ParsedData(var text: String = "")

abstract class BookTextParser() {
    abstract fun parseFile(path: String): ParsedData

    protected fun readAsPlainText(path: String): String {
        var plainText = ""
        try {
            val inStream: InputStream = FileInputStream(path)
            val inputReader = InputStreamReader(inStream)
            val buffReader = BufferedReader(inputReader)

            plainText = buffReader.readText()
        } catch (e: IOException) {
            println(e)
        }
        return (plainText)
    }
}