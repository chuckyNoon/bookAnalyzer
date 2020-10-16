package com.example.bookanalyzer.data.filesystem

import android.content.Context
import com.example.bookanalyzer.common.Utils
import java.io.FileInputStream

class Fb2PreviewParser(ctx: Context) : BookPreviewParser(ctx) {
    override fun getParsedData(path: String): ParsedBookData {
        val fileInputStream = FileInputStream(path)
        val unhandledStr = fileInputStream.readBytes().toString(Charsets.UTF_8)
        fileInputStream.close()

        val bookTitle = getBookTitle(unhandledStr)
        val author = getAuthor(unhandledStr)
        val bitmap = Utils.byteArrayToBitmap(getImage(unhandledStr))
        val saveImgPath = if (bitmap != null) "${bookTitle}.jpg" else null
        if (saveImgPath != null && bitmap != null) {
            saveImage(bitmap, saveImgPath)
        }
        return (ParsedBookData(path, bookTitle, author, saveImgPath))
    }

    private fun getAuthor(unhandledStr: String): String? {
        val firstName = getAuthorFirstName(unhandledStr)
        val middleName = getAuthorMiddleName(unhandledStr)
        val lastName = getAuthorLastName(unhandledStr)
        return ("$firstName $middleName $lastName")
    }

    private fun getAuthorFirstName(unhandledStr: String): String {
        var firstName = ""
        var firstNameStart = unhandledStr.indexOf("<first-name>")
        val firstNameEnd = unhandledStr.indexOf("</first-name>")

        if (firstNameStart in 0 until firstNameEnd) {
            firstNameStart += "<first-name>".length
            if (firstNameStart <= firstNameEnd) {
                firstName = unhandledStr.substring(firstNameStart, firstNameEnd)
            }
        }
        return (firstName)
    }

    private fun getAuthorMiddleName(unhandledStr: String): String {
        var middleName = ""
        var middleNameStart = unhandledStr.indexOf("<middle-name>")
        val middleNameEnd = unhandledStr.indexOf("</middle-name>")

        if (middleNameStart in 0 until middleNameEnd) {
            middleNameStart += "<middle-name>".length
            if (middleNameStart <= middleNameEnd) {
                middleName = unhandledStr.substring(middleNameStart, middleNameEnd)
            }
        }
        return (middleName)
    }

    private fun getAuthorLastName(unhandledStr: String): String {
        var lastName = ""
        var lastNameStart = unhandledStr.indexOf("<last-name>")
        val lastNameEnd = unhandledStr.indexOf("</last-name>")

        if (lastNameStart in 0 until lastNameEnd) {
            lastNameStart += "<last-name>".length
            if (lastNameStart <= lastNameEnd) {
                lastName = unhandledStr.substring(lastNameStart, lastNameEnd)
            }
        }
        return (lastName)
    }

    private fun getBookTitle(unhandledStr: String): String? {
        var bookTitle = ""
        var titleStart = unhandledStr.indexOf("<book-title>")
        val titleEnd = unhandledStr.indexOf("</book-title>")

        if (titleStart >= 0 && titleEnd >= 0) {
            titleStart += "<book-title>".length
            bookTitle = unhandledStr.substring(titleStart, titleEnd)
        }
        return (bookTitle)
    }

    private fun getImage(unhandledStr: String): ByteArray? {
        val imgName = getImageName(unhandledStr)

        val imgStart = unhandledStr.indexOf(">", unhandledStr.indexOf("<binary id=$imgName")) + 1
        val imgEnd = unhandledStr.indexOf("</binary", imgStart) - 1
        var imgByteArray: ByteArray? = null
        if (imgStart >= 0 && imgEnd >= 0) {
            imgByteArray = unhandledStr.substring(imgStart, imgEnd).toByteArray()
        }
        return (imgByteArray)
    }

    private fun getImageName(unhandledStr: String): String? {
        var imgName = ""
        var imgNameStart = unhandledStr.indexOf("xlink:href=")

        if (imgNameStart >= 0) {
            imgNameStart += ("xlink:href=").length
            val imgNameEnd = unhandledStr.indexOf("\"", imgNameStart + 1)
            if (imgNameEnd >= 0) {
                imgName = unhandledStr.substring(imgNameStart, imgNameEnd + 1).replace("#", "")
            }
        }
        return imgName
    }
}