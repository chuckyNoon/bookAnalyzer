package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.BookInfoModel
import java.io.IOException
import java.util.*

class BookInfoRepository(val ctx: Context)  {
    fun readInfo(path: String) : BookInfoModel {
        try {
            val fileInput = ctx.openFileInput(path)
            val scanner = Scanner(fileInput)


            val bookName = scanner.nextLine().split("/").last()
            val uniqueWord= scanner.nextLine()
            val allWord= scanner.nextLine()
            val avgSentenceLenInWrd= scanner.nextLine()
            val avgWordLen = scanner.nextLine()
            val avgSentenceLenInChr = scanner.nextLine()
            val allCharsCount = scanner.nextLine()
            val model = BookInfoModel(bookName,allWord,uniqueWord,allCharsCount,
                avgSentenceLenInWrd,avgSentenceLenInChr,avgWordLen)
            scanner.close()
            return (model)

        }catch (e: IOException){
            println("reading info error")
            return (BookInfoModel("","","","","","",""))
        }
    }
}