package com.example.bookanalyzer.analyzer.book_text_parser

class Fb2TextParser() : BookTextParser() {
    companion object {
        private const val BODY_OPEN_TAG = "<body>"
        private const val BODY_CLOSE_TAG = "</body>"
    }

    override fun parseFile(path: String): ParsedTextData {
        val sourceText = readAsPlainText(path)
        val bodyText = getBodyText(sourceText)
        val finalText = deleteAllTags(bodyText)

        return ParsedTextData().apply {
            text = finalText
        }
    }

    private fun getBodyText(sourceText: String): String {
        val bodyStart = sourceText.indexOf(BODY_OPEN_TAG)
        val bodyEnd = sourceText.indexOf(BODY_CLOSE_TAG)
        var bodyText = ""
        if (bodyStart >= 0 && bodyEnd >= 0) {
            bodyText = sourceText.substring(bodyStart + BODY_OPEN_TAG.length, bodyEnd - 1)
        }
        return bodyText
    }

    private fun deleteAllTags(bodyText: String): String {
        val regex = "(\\<(/?[^>]+)>)".toRegex()
        val replacement = ""
        return bodyText.replace(regex, replacement)
    }
}