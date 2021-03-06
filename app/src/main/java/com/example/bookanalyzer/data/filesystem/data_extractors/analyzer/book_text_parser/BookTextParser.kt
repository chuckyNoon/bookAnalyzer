package com.example.bookanalyzer.data.filesystem.data_extractors.analyzer.book_text_parser

import java.io.*

class ParsedTextData(var text: String = "")

abstract class BookTextParser() {
    abstract fun parseFile(path: String): ParsedTextData

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