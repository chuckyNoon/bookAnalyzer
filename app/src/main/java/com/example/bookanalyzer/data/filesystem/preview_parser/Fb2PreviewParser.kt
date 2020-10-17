package com.example.bookanalyzer.data.filesystem.preview_parser

import android.content.Context
import android.graphics.Bitmap
import com.example.bookanalyzer.common.Utils
import java.io.FileInputStream
import java.io.IOException

class Fb2PreviewParser(ctx: Context) : BookPreviewParser(ctx) {

    companion object {
        private const val FIRST_NAME_OPEN_TAG = "<first-name>"
        private const val FIRST_NAME_CLOSE_TAG = "</first-name>"
        private const val MIDDLE_NAME_OPEN_TAG = "<middle-name>"
        private const val MIDDLE_NAME_CLOSE_TAG = "</middle-name>"
        private const val LAST_NAME_OPEN_TAG = "<last-name>"
        private const val LAST_NAME_CLOSE_TAG = "</last-name>"
        private const val BOOK_TITLE_OPEN_TAG = "<book-title>"
        private const val BOOK_TITLE_CLOSE_TAG = "</book-title>"
    }

    override fun getParsedData(path: String): ParsedBookData {
        val sourceText = readSourceText(path) ?: ""
        val bookTitle = getBookTitle(sourceText)
        val author = getAuthor(sourceText)
        val bitmap = Utils.byteArrayToBitmap(getImage(sourceText))
        val saveImgPath = getSaveImgPath(bitmap, bookTitle)

        saveImage(bitmap, saveImgPath)
        return ParsedBookData(path, bookTitle, author, saveImgPath)
    }

    private fun getSaveImgPath(bitmap: Bitmap?, bookTitle: String?): String? {
        return if (bitmap != null) {
            ("${bookTitle}.jpg")
        } else {
            (null)
        }
    }

    private fun readSourceText(path: String): String? {
        return try {
            val fileInputStream = FileInputStream(path)
            val sourceText = fileInputStream.readBytes().toString(Charsets.UTF_8)
            fileInputStream.close()
            (sourceText)
        } catch (e: IOException) {
            println(e)
            (null)
        }
    }

    private fun getAuthor(sourceText: String): String? {
        val firstName =
            getAuthorNamePartByTag(sourceText, FIRST_NAME_OPEN_TAG, FIRST_NAME_CLOSE_TAG)
        val middleName =
            getAuthorNamePartByTag(sourceText, MIDDLE_NAME_OPEN_TAG, MIDDLE_NAME_CLOSE_TAG)
        val lastName = getAuthorNamePartByTag(sourceText, LAST_NAME_OPEN_TAG, LAST_NAME_CLOSE_TAG)
        return "$firstName $middleName $lastName"
    }

    private fun getAuthorNamePartByTag(
        sourceText: String,
        openTag: String,
        closeTag: String
    ): String {
        var namePart = ""
        val partStart = sourceText.indexOf(openTag) + openTag.length
        val partEnd = sourceText.indexOf(closeTag)

        if (partEnd >= 0 && partStart in 0 until partEnd) {
            namePart = sourceText.substring(partStart, partEnd)
        }
        return namePart
    }

    private fun getBookTitle(sourceText: String): String? {
        var bookTitle = ""
        var titleStart = sourceText.indexOf(BOOK_TITLE_OPEN_TAG)
        val titleEnd = sourceText.indexOf(BOOK_TITLE_CLOSE_TAG)

        if (titleStart >= 0 && titleEnd >= 0) {
            titleStart += BOOK_TITLE_OPEN_TAG.length
            bookTitle = sourceText.substring(titleStart, titleEnd)
        }
        return bookTitle
    }

    private fun getImage(sourceText: String): ByteArray? {
        val imgName = getImageName(sourceText)

        val imgStart = sourceText.indexOf(">", sourceText.indexOf("<binary id=$imgName")) + 1
        val imgEnd = sourceText.indexOf("</binary", imgStart) - 1
        var imgByteArray: ByteArray? = null
        if (imgStart >= 0 && imgEnd >= 0) {
            imgByteArray = sourceText.substring(imgStart, imgEnd).toByteArray()
        }
        return imgByteArray
    }

    private fun getImageName(sourceText: String): String? {
        var imgName = ""
        var imgNameStart = sourceText.indexOf("xlink:href=")

        if (imgNameStart >= 0) {
            imgNameStart += ("xlink:href=").length
            val imgNameEnd = sourceText.indexOf("\"", imgNameStart + 1)
            if (imgNameEnd >= 0) {
                imgName = sourceText.substring(imgNameStart, imgNameEnd + 1).replace("#", "")
            }
        }
        return imgName
    }
}