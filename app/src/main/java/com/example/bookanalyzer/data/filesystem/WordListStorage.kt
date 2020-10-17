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
        val listFileContent = readWordListFile(ind)
        listFileContent?.let {
            val lines = (listFileContent.substring(1, listFileContent.length - 1).split(','))
            val rowDataList = convertFileLinesToRowDataList(ArrayList(lines))
            return (rowDataList)
        }
        return (null)
    }

    private fun readWordListFile(ind: Int): String? {
        return try {
            val listFileInput = ctx.openFileInput(savedWordListPathByInd(ind))
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
        return (rowDataList)
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