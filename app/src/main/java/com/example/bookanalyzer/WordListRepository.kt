package com.example.bookanalyzer

import android.content.Context
import java.io.IOException

class WordListRepository(val ctx: Context) : IWordListContract.Repository {
    override fun readWordList(listPath: String): ArrayList<WordListElemModel>? {
        return try {
            val list = ArrayList<WordListElemModel>()
            val listIn = ctx.openFileInput(listPath)
            val strMap = listIn.readBytes().toString(Charsets.UTF_8)
            val lines = (strMap.substring(1, strMap.length - 1).split(','))
            for (i in lines.indices){
                val line = lines[i]
                val parts = line.split("=")
                if (parts.size == 2){
                    list.add(WordListElemModel(parts[0], parts[1], (i+1).toString()))
                }
            }
            return (list)
        }catch (e: IOException){
            println("reading list error")
            (null)
        }
    }
}