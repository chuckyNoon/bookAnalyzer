package com.example.bookanalyzer

import android.content.Context
import android.os.AsyncTask
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MenuContentLoader(private val ctx: Context) {
    var ct = 0
    fun firstStage(paths: ArrayList<String>) : ArrayList<ABookInfo>{
        val bookList = ArrayList<ABookInfo>()
        for (path in paths){
            bookList.add(ABookInfo(path, null, null, null, 0))
        }

        return bookList
    }

    fun searchSavedWordCount(path:String) : Int{
        var ind = -1
        try{
            val inputStream = ctx.openFileInput("all")
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

    fun loadMoreInfo(path: String) : ABookInfo{
        val ab = ABookInfo(getBookList(path))
        ab.wordCount = searchSavedWordCount(path)
        return ab
    }


    private fun getBookList(path:String) : BookInfo{
        val bookInfo = when(getFormat(path)){
            "epub" -> parseEpub(path)
            "fb2" -> parseFb2(path)
            else -> parseTxt(path)
        }
        return bookInfo
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

    private fun parseFb2(path:String) :BookInfo{
        val fileInputStream = FileInputStream(path)
        val unhandledStr = fileInputStream.readBytes().toString(Charsets.UTF_8)
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
        fileInputStream.close()
        return (BookInfo(path, null, null, imgByteArray))
    }

    private fun parseTxt(path:String) : BookInfo{
        return BookInfo(path, null, null, null)
    }
}