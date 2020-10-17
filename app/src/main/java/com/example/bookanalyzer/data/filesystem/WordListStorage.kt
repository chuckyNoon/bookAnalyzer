package com.example.bookanalyzer.data.filesystem

import android.content.Context
import java.io.IOException
import kotlin.collections.ArrayList

class WordListRowData(var word: String, var frequency: Int, var pos: Int)

class WordListStorage(val ctx: Context) {
    fun savedWordListPathByInd(ind: Int) = "list$ind"

    fun saveWordList(wordMap: Map<String, Int>, ind: Int) {
        try {
            val lstOut = ctx.openFileOutput(savedWordListPathByInd(ind), 0)
            lstOut.write(wordMap.toString().toByteArray())
            lstOut.close()
        } catch (e: IOException) {
            println(e)
        }
    }

    fun getWordList(listPath:String): ArrayList<WordListRowData>? {
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

    private fun convertFileLinesToRowDataList(lines: ArrayList<String>): ArrayList<WordListRowData> {
        val rowDataList = ArrayList<WordListRowData>()
        for (i in lines.indices) {
            val rowData = convertFileLineToRowData(lines[i], i)
            rowData?.let {
                rowDataList.add(it)
            }
        }
        return rowDataList
    }

    private fun convertFileLineToRowData(line: String, ind: Int): WordListRowData? {
        val parts = line.split("=")
        return if (parts.size == 2) {
            val word = parts[0]
            val count = parts[1].toInt()
            val pos = ind + 1
            (WordListRowData(word, count, pos))
        } else {
            (null)
        }
    }
}