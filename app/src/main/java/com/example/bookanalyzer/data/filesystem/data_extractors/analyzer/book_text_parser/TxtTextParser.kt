package com.example.bookanalyzer.data.filesystem.data_extractors.analyzer.book_text_parser

class TxtTextParser : BookTextParser() {
    override fun parseFile(path: String): ParsedTextData {
        val sourceText = readAsPlainText(path)
        return ParsedTextData().apply {
            text = sourceText
        }
    }
}