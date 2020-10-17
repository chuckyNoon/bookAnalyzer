package com.example.bookanalyzer.data.filesystem.preview_parser

import android.content.Context
import java.io.FileInputStream
import java.io.IOException

class Fb2PreviewParser(ctx: Context) : BookPreviewParser(ctx) {

    companion object {
        private const val FIRST_NAME_OPENING_TAG = "<first-name>"
        private const val FIRST_NAME_CLOSING_TAG = "</first-name>"
        private const val MIDDLE_NAME_OPENING_TAG = "<middle-name>"
        private const val MIDDLE_NAME_CLOSING_TAG = "</middle-name>"
        private const val LAST_NAME_OPENING_TAG = "<last-name>"
        private const val LAST_NAME_CLOSING_TAG = "</last-name>"
        private const val BOOK_TITLE_OPENING_TAG = "<book-title>"
        private const val BOOK_TITLE_CLOSING_TAG = "</book-title>"
        private const val CLOSING_TAG_CHAR = ">"
        private const val BINARY_CLOSING_TAG = "</binary>"
        private const val LINK = "image l:href="
        private const val IMAGE_NAME_BORDER_CHAR = "\""
        private const val UNNECESSARY_CHAR_IN_IMAGE_NAME = "#"
        private const val BINARY_PART_TAG = "<binary"
    }

    override fun getParsedData(path: String): ParsedPreviewData {
        val sourceText = readSourceText(path) ?: ""
        val bookTitle = getBookTitle(sourceText)
        val author = getAuthor(sourceText)
        val imgByteArray = getImageByteArray(sourceText)

        return ParsedPreviewData(path, bookTitle, author, imgByteArray)
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
            getTagContent(sourceText, FIRST_NAME_OPENING_TAG, FIRST_NAME_CLOSING_TAG)
        val middleName =
            getTagContent(sourceText, MIDDLE_NAME_OPENING_TAG, MIDDLE_NAME_CLOSING_TAG)
        val lastName = getTagContent(sourceText, LAST_NAME_OPENING_TAG, LAST_NAME_CLOSING_TAG)
        return "$firstName $middleName $lastName"
    }

    private fun getBookTitle(sourceText: String): String? {
        return getTagContent(sourceText, BOOK_TITLE_OPENING_TAG, BOOK_TITLE_CLOSING_TAG)
    }

    private fun getTagContent(
        sourceText: String,
        openTag: String,
        closeTag: String
    ): String {
        val contentStart = getTagContentStart(sourceText, openTag)
        val contentEnd = getTagContentEnd(sourceText, closeTag)
        return getContentFromBorders(sourceText, contentStart, contentEnd)
    }

    private fun getTagContentStart(sourceText: String, openingTag: String): Int {
        val openingTagStart = sourceText.indexOf(openingTag)
        return if (isTextIndexValid(openingTagStart)) {
            (openingTagStart + openingTag.length)
        } else {
            (-1)
        }
    }

    private fun getTagContentEnd(sourceText: String, closingTag: String): Int {
        val closingTagStart = sourceText.indexOf(closingTag)
        return if (isTextIndexValid(closingTagStart)) {
            (closingTagStart)
        } else {
            (-1)
        }
    }

    private fun getContentFromBorders(
        sourceText: String,
        contentStart: Int,
        contentEnd: Int
    ): String {
        var content = ""
        if (isContentBordersValid(contentStart, contentEnd)) {
            content = sourceText.substring(contentStart, contentEnd)
        }
        return content
    }


    private fun getImageByteArray(sourceText: String): ByteArray? {
        var imgByteArray: ByteArray? = null
        val imgName = getImageName(sourceText) ?: ""

        val leftBorder = sourceText.indexOf(imgName, sourceText.indexOf(BINARY_PART_TAG))
        if (isTextIndexValid(leftBorder)) {
            val imgStart = sourceText.indexOf(CLOSING_TAG_CHAR, leftBorder) + 1
            if (isTextIndexValid((imgStart))) {
                val imgEnd = sourceText.indexOf(BINARY_CLOSING_TAG, imgStart)
                imgByteArray = getContentFromBorders(sourceText, imgStart, imgEnd).toByteArray()
            }
        }
        return imgByteArray
    }

    private fun getImageName(sourceText: String): String? {
        var imgName = ""

        val openingTagStart = sourceText.indexOf(LINK)
        if (isTextIndexValid(openingTagStart)) {
            val imgNameStart = openingTagStart + (LINK).length + 1
            val imgNameEnd = sourceText.indexOf(IMAGE_NAME_BORDER_CHAR, imgNameStart)
            imgName = getContentFromBorders(sourceText, imgNameStart, imgNameEnd).replace(
                UNNECESSARY_CHAR_IN_IMAGE_NAME, ""
            )
        }
        return imgName
    }

    private fun isTextIndexValid(textInd: Int) = (textInd > 0)

    private fun isContentBordersValid(textStart: Int, textEnd: Int) =
        (textEnd >= 0 && textStart in 0..textEnd)

}