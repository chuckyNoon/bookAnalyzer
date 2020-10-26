package com.example.bookanalyzer.data.filesystem.data_extractors.analyzer.book_text_parser

import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.FileInputStream
import java.io.InputStream

class EpubTextParser() : BookTextParser() {
    companion object {
        private const val DIV_TAG = "div"
        private const val P_TAG = "p"
    }

    override fun parseFile(path: String): ParsedTextData {
        val htmlText = readHtmlFromEpub(path)
        val finalText = parseHtml(htmlText)
        return ParsedTextData().apply {
            text = finalText
        }
    }

    private fun readHtmlFromEpub(path: String): String {
        val inStream: InputStream = FileInputStream(path)
        val book: Book = EpubReader().readEpub(inStream)
        val htmlText = java.lang.StringBuilder()

        for (content in book.contents) {
            val text = content.reader.readText()
            htmlText.append(text)
        }
        return htmlText.toString()
    }

    private fun parseHtml(htmlText: String): String {
        val doc = Jsoup.parse(htmlText)
        val htmlElement = doc.body()

        val divTextBlocks = getTextBlocksByTag(htmlElement, DIV_TAG)
        val pTextBlocks = getTextBlocksByTag(htmlElement, P_TAG)
        val finalText = getFinalTextFromBlocks(divTextBlocks, pTextBlocks)
        return finalText.toString()
    }

    private fun getTextBlocksByTag(htmlElement: Element, selectedTag: String): Elements {
        return htmlElement.select(selectedTag)
    }

    private fun getFinalTextFromBlocks(
        divTextBlocks: Elements,
        pTextBlocks: Elements
    ): StringBuilder {
        return if (divTextBlocks.size > pTextBlocks.size) {
            (concatenateTextBlocks(divTextBlocks))
        } else {
            (concatenateTextBlocks(pTextBlocks))
        }
    }

    private fun concatenateTextBlocks(textBlocks: Elements): StringBuilder {
        val finalText = StringBuilder()
        for (textBlock in textBlocks) {
            finalText.append(textBlock.text() + "\n")
        }
        return finalText
    }
}