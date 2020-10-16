package com.example.bookanalyzer.data.filesystem

import android.content.Context
import java.io.IOException
import kotlin.collections.ArrayList

class WordListRowData(var word: String, var frequency: Int, var pos: Int)

class WordListStorage(val ctx: Context) {
    fun savedWordListPathByInd(bookInd: Int) = "list$bookInd"

    fun saveWordList(wordMap: Map<String, Int>, ind: Int) {
        try {
            val lstOut = ctx.openFileOutput(savedWordListPathByInd(ind), 0)
            lstOut.write(wordMap.toString().toByteArray())
            lstOut.close()
        } catch (e: IOException) {
            println(e)
        }
    }

    fun getWordList(ind: Int): ArrayList<WordListRowData>? {
        return try {
            val list = ArrayList<WordListRowData>()
            val listFileInput = ctx.openFileInput(savedWordListPathByInd(ind))
            val listFileContent = listFileInput.readBytes().toString(Charsets.UTF_8)
            val lines = (listFileContent.substring(1, listFileContent.length - 1).split(','))
            for (i in lines.indices) {
                val line = lines[i]
                val parts = line.split("=")
                if (parts.size == 2) {
                    list.add(WordListRowData(parts[0], parts[1].toInt(), (i + 1)))
                }
            }
            listFileInput.close()
            return (list)
        } catch (e: IOException) {
            println(e)
            (null)
        }
    }
}