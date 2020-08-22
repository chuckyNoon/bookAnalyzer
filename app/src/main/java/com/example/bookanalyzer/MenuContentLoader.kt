package com.example.bookanalyzer

import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MenuContentLoader(private val ctx: Context) {
    companion object{
        val ALL_ANALYZED_PATH = "all"
    }
    private var paths:ArrayList<String> = ArrayList()

    init{
        try{
            val input = ctx.openFileInput(PathSaver.SAVED_PATHS_FILE)
            val saved = input.readBytes().toString(Charsets.UTF_8).split("\n")
            for (s in saved){
                if (s.isNotEmpty())
                    paths.add(s)
            }
            input.close()
        }catch (e:IOException){
            /*val time4 = System.currentTimeMillis()
            paths = BookSearch.findAll(dir)
            val time5 = System.currentTimeMillis()
            PathSaver(ctx).saveAll(paths)
            val time6 = System.currentTimeMillis()
            println("q = " + (time5 - time4).toDouble() / 1000)
            println("q= " + (time6 - time5).toDouble() / 1000)*/
        }
    }

    fun firstStage() : ArrayList<ABookInfo>{
        val bookList = ArrayList<ABookInfo>()
        for (path in paths){
            bookList.add(ABookInfo(path, null, null, null, 0))
        }

        return bookList
    }

    /*fun finalStage() : ArrayList<ABookInfo>{
        val bookList = ArrayList<ABookInfo>()
        for (path in paths){
            val book = getDetailedInfo(path)
            bookList.add(book)
        }
        return bookList
    }*/

    fun searchSavedWordCount(path:String) : Int{
        var ind = -1
        try{
            val inputStream = ctx.openFileInput(ALL_ANALYZED_PATH)
            val strs = inputStream.readBytes().toString(Charsets.UTF_8).split("\n")
            for (i in strs.indices){
                if (strs[i] == path && i > 0){
                    ind = strs[i-1].toInt()
                    break
                }
            }
            inputStream.close()
            if (ind == -1)
                return (0)
        }catch (e:IOException){
            return 0
        }
        try{
            val scanner = Scanner(ctx.openFileInput("info$ind"))
            var wordCount = 0
            if (scanner.hasNextLine()){
                scanner.nextLine()
                if(scanner.hasNextLine()){
                    scanner.nextLine()
                    if (scanner.hasNextLine())
                        wordCount = scanner.nextLine().toInt()
                }
            }
            scanner.close()
            return wordCount
        }catch (e:IOException){
            return (0)
        }
    }

    fun getDetailedInfo(path: String) : ABookInfo{
        val book = when(getFormat(path)) {
            "epub" -> parseEpub(path)
            "fb2" -> parseFb2(path)
            else -> parseTxt(path)
        }
        book.wordCount = searchSavedWordCount(path)
        return ABookInfo(book)
    }


    private fun getFormat(path:String) : String{
        return when{
            path.endsWith(".epub") -> "epub"
            path.endsWith(".fb2") -> "fb2"
            else  -> "txt"
        }
    }

    private fun parseEpub(path:String) : BookInfo{
        val inStream = FileInputStream(path)
        val ebook: Book = EpubReader().readEpub(inStream)

        val author = if (ebook.metadata.authors.size > 0)
            ebook.metadata.authors[0].firstname + " " + ebook.metadata.authors[0].lastname
            else ""
        val bookName = ebook.title

        return (BookInfo(path, bookName, author, ebook.coverImage?.data))
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

    private fun parseFb2(path:String) :BookInfo{
        val fileInputStream = FileInputStream(path)
        val unhandledStr = fileInputStream.readBytes().toString(Charsets.UTF_8)
        fileInputStream.close()

        val bookTitle= getBookTitleFromFb2(unhandledStr)
        val author = getAuthorFromFb2(unhandledStr)
        val imgByteArray = getImageFromFb2(unhandledStr)

        return (BookInfo(path, bookTitle, author, imgByteArray))
    }

    private fun parseTxt(path:String) : BookInfo{
        return BookInfo(path, null, null, null)
    }
}