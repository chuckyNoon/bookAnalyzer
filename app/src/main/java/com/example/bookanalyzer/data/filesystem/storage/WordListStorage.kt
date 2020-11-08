package com.example.bookanalyzer.data.filesystem.storage

import android.content.Context
import java.io.IOException
import kotlin.collections.ArrayList

class WordData(
    var word: String,
    var frequency: Int,
    var pos: Int
)

class WordListStorage(val ctx: Context) {
    fun getPathForListSaveByInd(ind: Int) = "list$ind"

    fun saveWordDataList(wordMap: Map<String, Int>, ind: Int) {
        try {
            val lstOut = ctx.openFileOutput(getPathForListSaveByInd(ind), 0)
            lstOut.write(wordMap.toString().toByteArray())
            lstOut.close()
        } catch (e: IOException) {
            println(e)
        }
    }

    fun getWordDataList(listPath: String): ArrayList<WordData>? {
        val listFileContent = readWordListFile(listPath)
        listFileContent?.let {
            val lines = (listFileContent.substring(1, listFileContent.length - 1).split(','))
            return convertFileLinesToRowDataList(ArrayList(lines))
        }
        return null
    }

    private fun readWordListFile(listPath: String): String? {
        return try {
            val listFileInput = ctx.openFileInput(listPath)
            val listFileContent = listFileInput.readBytes().toString(Charsets.UTF_8)
            listFileInput.close()
            (listFileContent)
        } catch (e: IOException) {
            println(e)
            (null)
        }
    }

    private fun convertFileLinesToRowDataList(lines: ArrayList<String>): ArrayList<WordData> {
        val wordDataList = ArrayList<WordData>()
        for (i in lines.indices) {
            val wordData = convertFileLineToWordData(lines[i], i)
            wordData?.let {
                wordDataList.add(it)
            }
        }
        return wordDataList
    }

    private fun convertFileLineToWordData(line: String, ind: Int): WordData? {
        val parts = line.split("=")
        return if (parts.size == 2) {
            val word = parts[0]
            val count = parts[1].toInt()
            val pos = ind + 1
            (WordData(word, count, pos))
        } else {
            (null)
        }
    }
}