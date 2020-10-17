package com.example.bookanalyzer.analyzer.book_text_parser

class TxtTextParser : BookTextParser() {
    override fun parseFile(path: String): ParsedTextData {
        val sourceText = readAsPlainText(path)
        return ParsedTextData().apply {
            text = sourceText
        }
    }
}