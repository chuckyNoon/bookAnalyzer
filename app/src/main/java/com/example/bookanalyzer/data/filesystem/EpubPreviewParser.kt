package com.example.bookanalyzer.data.filesystem

import android.content.Context
import com.example.bookanalyzer.common.Utils
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.FileInputStream

class EpubPreviewParser(ctx: Context) : BookPreviewParser(ctx) {
    override fun getParsedData(path: String): ParsedBookData {
        val inStream = FileInputStream(path)
        val book: Book = EpubReader().readEpub(inStream)

        val author = getAuthor(book)
        val bookName = book.title
        val bitmap = Utils.byteArrayToBitmap(book.coverImage?.data)
        val saveImgPath = if (book.coverImage != null) "$bookName.jpg" else null
        if (saveImgPath != null && bitmap != null) {
            saveImage(bitmap, saveImgPath)
        }
        return (ParsedBookData(path, bookName, author, saveImgPath))
    }

    private fun getAuthor(book:Book) : String?{
        return if (book.metadata.authors.size > 0){
            val firstName = book.metadata.authors[0].firstname
            val lastName = book.metadata.authors[0].lastname
            ("$firstName $lastName")
        }else{
            (null)
        }
    }
}