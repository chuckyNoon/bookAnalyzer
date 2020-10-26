package com.example.bookanalyzer.data.filesystem.data_extractors.preview_parser

import android.content.Context
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.FileInputStream

class EpubPreviewParser(ctx: Context) : BookPreviewParser(ctx) {
    override fun getParsedData(path: String): ParsedPreviewData {
        val inStream = FileInputStream(path)
        val book: Book = EpubReader().readEpub(inStream)

        val author = getAuthor(book)
        val bookTitle = book.title
        val imgByteArray = book.coverImage?.data
        return ParsedPreviewData(
            path,
            bookTitle,
            author,
            imgByteArray
        )
    }

    private fun getAuthor(book: Book): String? {
        return if (book.metadata.authors.size > 0) {
            val firstName = book.metadata.authors[0].firstname
            val lastName = book.metadata.authors[0].lastname
            ("$firstName $lastName")
        } else {
            (null)
        }
    }
}