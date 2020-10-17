package com.example.bookanalyzer.analyzer.book_text_parser

class TxtTextParser : BookTextParser() {
    override fun parseFile(path: String): ParsedData {
        val sourceText = readAsPlainText(path)
        return ParsedData().apply {
            text = sourceText
        }
    }
}