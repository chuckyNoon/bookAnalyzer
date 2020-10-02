package com.example.bookanalyzer.data

import android.content.Context
import com.example.bookanalyzer.MenuBookModel
import com.example.bookanalyzer.common.Utils
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.FileInputStream
import kotlin.collections.ArrayList

class PresentationInfoLoader(private val ctx: Context) : FileDataStorage(){
    fun getPreviewList(savedPaths:ArrayList<String>) : ArrayList<MenuBookModel>{
        val bookList = ArrayList<MenuBookModel>()
        for (path in savedPaths){
            bookList.add(MenuBookModel(path, null, null, null, 0))
        }

        return bookList
    }

    fun getDetailedBookInfo(path:String) : MenuBookModel {
        val book = when(getFormat(path)) {
            "epub" -> parseEpub(path)
            "fb2" -> parseFb2(path)
            else -> parseTxt(path)
        }
        return book
    }

    private fun getFormat(path:String) : String{
        return when{
            path.endsWith(".epub") -> "epub"
            path.endsWith(".fb2") -> "fb2"
            else  -> "txt"
        }
    }

    private fun parseEpub(path:String) : MenuBookModel {
        val inStream = FileInputStream(path)
        val ebook: Book = EpubReader().readEpub(inStream)

        val author = if (ebook.metadata.authors.size > 0)
            ebook.metadata.authors[0].firstname + " " + ebook.metadata.authors[0].lastname
            else ""
        val bookName = ebook.title

        return (MenuBookModel(path, bookName, author, Utils.byteArrayToBitmap(ebook.coverImage?.data)))
    }

    private fun parseFb2(path:String) : MenuBookModel{
        val fileInputStream = FileInputStream(path)
        val unhandledStr = fileInputStream.readBytes().toString(Charsets.UTF_8)
        fileInputStream.close()

        val bookTitle = getBookTitleFromFb2(unhandledStr)
        val author = getAuthorFromFb2(unhandledStr)
        val imgByteArray = getImageFromFb2(unhandledStr)

        return (MenuBookModel(path, bookTitle, author, Utils.byteArrayToBitmap(imgByteArray)))
    }

    private fun parseTxt(path:String) : MenuBookModel {
        return MenuBookModel(path, null, null, null)
    }

    private fun getAuthorFromFb2(unhandledStr:String) : String?{
        var fInd1 = unhandledStr.indexOf("<first-name>")
        var fInd2 = unhandledStr.indexOf("</first-name>")
        var mInd1 = unhandledStr.indexOf("<middle-name>")
        var mInd2 = unhandledStr.indexOf("</middle-name>")
        var lInd1 = unhandledStr.indexOf("<last-name>")
        var lInd2 = unhandledStr.indexOf("</last-name>")

        var firstName = ""
        var middleName = ""
        var lastName = ""

        if (fInd1 in 0 until fInd2){
            fInd1 += "<first-name>".length
            if (fInd1 <= fInd2)
                firstName = unhandledStr.substring(fInd1, fInd2)
        }
        if (mInd1 in 0 until mInd2){
            mInd1 += "<middle-name>".length
            if (mInd1 <= mInd2)
                middleName = unhandledStr.substring(mInd1, mInd2)
        }
        if (lInd1 in 0 until lInd2){
            lInd1 += "<last-name>".length
            if (lInd1 <= lInd2)
                lastName = unhandledStr.substring(lInd1, lInd2)
        }
        return ("$firstName $middleName $lastName")
    }

    private fun getBookTitleFromFb2(unhandledStr:String) : String?{
        var bookTitle = ""
        var ind1 = unhandledStr.indexOf("<book-title>")
        var ind2 = unhandledStr.indexOf("</book-title>")
        if (ind1 >=0 && ind2 >= 0){
            ind1 += "<book-title>".length
            bookTitle = unhandledStr.substring(ind1, ind2)
        }
        return bookTitle
    }

    private fun getImageFromFb2(unhandledStr:String) : ByteArray?{
        var imgNameStart = unhandledStr.indexOf( "xlink:href=")
        var name = ""
        if (imgNameStart >= 0) {
            imgNameStart += ("xlink:href=").length
            val imgNameEnd = unhandledStr.indexOf("\"", imgNameStart + 1)
            if (imgNameEnd >= 0){
                name = unhandledStr.substring(imgNameStart, imgNameEnd + 1).replace("#","")
            }
        }
        val imgStart = unhandledStr.indexOf(">",unhandledStr.indexOf("<binary id=$name")) + 1
        val imgEnd = unhandledStr.indexOf("</binary", imgStart) - 1
        var imgByteArray:ByteArray? = null
        if (imgStart >=0 && imgEnd>= 0) {
            imgByteArray = unhandledStr.substring(imgStart, imgEnd).toByteArray()
        }
        return imgByteArray
    }

}