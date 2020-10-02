package com.example.bookanalyzer.data

import android.content.Context
import com.example.bookanalyzer.AnalyzedBookModel
import com.example.bookanalyzer.BookInfoModel
import com.example.bookanalyzer.ui.adapters.WordListElemModel
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class AnalyzedInfoSaver(val ctx:Context) : FileDataStorage() {
    fun saveAnalyzedInfo(bookPath:String, ind:Int, analyzedBookModel: AnalyzedBookModel, redo:Boolean) {
        try {
            analyzedBookModel.img?.let {
                val imgOut = ctx.openFileOutput(savedImgPath(ind), 0)
                imgOut.write(it)
                imgOut.close()
            }
            val lstOut = ctx.openFileOutput(savedWordListPath(ind), 0)
            lstOut.write(analyzedBookModel.wordMap.toString().toByteArray())

            val infoOut = ctx.openFileOutput(savedInfoPath(ind), 0)
            val info = "$bookPath\n${analyzedBookModel.allWordCount}\n${analyzedBookModel.uniqueWordCount}\n${analyzedBookModel.avgSentenceLenInWrd}\n${analyzedBookModel.avgWordLen}\n"+
                    "${analyzedBookModel.avgSentenceLenInChr}\n${analyzedBookModel.allCharCount}\n"
            infoOut.write(info.toByteArray())

            lstOut.close()
            infoOut.close()
        }catch (e: IOException){
            println("saving error")
        }
    }

    fun getAnalyzedInfo(ind:Int) : BookInfoModel{
        try {
            val fileInput = ctx.openFileInput(savedInfoPath(ind))
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

    fun getWordList(ind: Int): ArrayList<WordListElemModel>? {
        return try {
            val list = ArrayList<WordListElemModel>()
            val listIn = ctx.openFileInput(savedWordListPath(ind))
            val strMap = listIn.readBytes().toString(Charsets.UTF_8)
            val lines = (strMap.substring(1, strMap.length - 1).split(','))
            for (i in lines.indices) {
                val line = lines[i]
                val parts = line.split("=")
                if (parts.size == 2) {
                    list.add(WordListElemModel(parts[0], parts[1], (i + 1).toString()))
                }
            }
            return (list)
        } catch (e: IOException) {
            println("reading list error")
            (null)
        }
    }

    fun getSavedWordCount(ind:Int) : Int{
        try{
            val scanner = Scanner(ctx.openFileInput(savedInfoPath(ind)))
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
}