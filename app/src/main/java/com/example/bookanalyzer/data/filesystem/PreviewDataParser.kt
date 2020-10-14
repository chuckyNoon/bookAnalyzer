package com.example.bookanalyzer.data.filesystem

import android.content.Context
import android.graphics.Bitmap
import com.example.bookanalyzer.common.Utils
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.FileInputStream
import java.io.IOException

class ParsedBookData(
    var path:String,
    var title:String?,
    var author:String?,
    var imgPath: String?)
{
}

class PreviewDataParser(private val ctx: Context) {
    fun getPreviewList(paths: ArrayList<String>) : ArrayList<ParsedBookData>{
        val bookList = ArrayList<ParsedBookData>()
        for (path in paths){
            bookList.add(getPreviewData(path))
        }
        return bookList
    }

    fun getPreviewData(path: String) : ParsedBookData {
        val book = when(getFormat(path)) {
            "epub" -> parseEpub(path)
            "fb2" -> parseFb2(path)
            else -> parseTxt(path)
        }
        return book
    }

    private fun getFormat(path: String) : String{
        return when{
            path.endsWith(".epub") -> "epub"
            path.endsWith(".fb2") -> "fb2"
            else  -> "txt"
        }
    }

    private fun saveImage(bitmap:Bitmap, imgPath:String){
        try{
            val out = ctx.openFileOutput(imgPath,0)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
        }catch (e:IOException){
            println(e)
        }
    }

    private fun parseEpub(path: String) : ParsedBookData {
        val inStream = FileInputStream(path)
        val ebook: Book = EpubReader().readEpub(inStream)
        val author = if (ebook.metadata.authors.size > 0)
            ebook.metadata.authors[0].firstname + " " + ebook.metadata.authors[0].lastname
            else ""
        val bookName = ebook.title
        val bitmap = Utils.byteArrayToBitmap(ebook.coverImage?.data)
        val imgPath = if(ebook.coverImage != null) "$bookName.jpg" else null
        if (imgPath != null && bitmap != null) {
            saveImage(bitmap, imgPath)
        }

        return (ParsedBookData(path, bookName, author,imgPath))
    }

    private fun parseFb2(path: String) : ParsedBookData{
        val fileInputStream = FileInputStream(path)
        val unhandledStr = fileInputStream.readBytes().toString(Charsets.UTF_8)
        fileInputStream.close()

        val bookTitle = getBookTitleFromFb2(unhandledStr)
        val author = getAuthorFromFb2(unhandledStr)
        val bitmap = Utils.byteArrayToBitmap(getImageFromFb2(unhandledStr))
        val imgPath = if(bitmap != null) "${bookTitle}.jpg" else null
        if (imgPath != null && bitmap != null) {
            saveImage(bitmap, imgPath)
        }

        return (ParsedBookData(path, bookTitle, author, imgPath))
    }

    private fun parseTxt(path: String) : ParsedBookData {
        return ParsedBookData(path, null, null, null)
    }

    private fun getAuthorFromFb2(unhandledStr: String) : String?{
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

    private fun getBookTitleFromFb2(unhandledStr: String) : String?{
        var bookTitle = ""
        var ind1 = unhandledStr.indexOf("<book-title>")
        var ind2 = unhandledStr.indexOf("</book-title>")
        if (ind1 >=0 && ind2 >= 0){
            ind1 += "<book-title>".length
            bookTitle = unhandledStr.substring(ind1, ind2)
        }
        return bookTitle
    }

    private fun getImageFromFb2(unhandledStr: String) : ByteArray?{
        var imgNameStart = unhandledStr.indexOf("xlink:href=")
        var name = ""
        if (imgNameStart >= 0) {
            imgNameStart += ("xlink:href=").length
            val imgNameEnd = unhandledStr.indexOf("\"", imgNameStart + 1)
            if (imgNameEnd >= 0){
                name = unhandledStr.substring(imgNameStart, imgNameEnd + 1).replace("#", "")
            }
        }
        val imgStart = unhandledStr.indexOf(">", unhandledStr.indexOf("<binary id=$name")) + 1
        val imgEnd = unhandledStr.indexOf("</binary", imgStart) - 1
        var imgByteArray:ByteArray? = null
        if (imgStart >=0 && imgEnd>= 0) {
            imgByteArray = unhandledStr.substring(imgStart, imgEnd).toByteArray()
        }
        return imgByteArray
    }

}