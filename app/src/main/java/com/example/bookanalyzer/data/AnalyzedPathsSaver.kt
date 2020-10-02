package com.example.bookanalyzer.data

import android.content.Context
import java.io.IOException

class AnalyzedPathsSaver(private val ctx:Context) : FileDataStorage() {
    fun getIndByPath(path: String) : Int{
        var ind = -1
        try{
            val inputStream = ctx.openFileInput(ANALYZED_BOOKS_LIST)
            val strs = inputStream.readBytes().toString(Charsets.UTF_8).split("\n")
            for (i in strs.indices){
                if (strs[i] == path && i > 0){

                    ind = strs[i - 1].toInt()
                    break
                }
            }
            inputStream.close()
        }catch (e: IOException){
        }
        return ind
    }

    fun savePath(path:String, ind:Int){
        val inAll = ctx.openFileOutput(ANALYZED_BOOKS_LIST, Context.MODE_APPEND)
        inAll.write("$ind\n$path\n".toByteArray())
        inAll.close()
    }

    fun getAnalyzedCount():Int{
        return try{
            val inputStream = ctx.openFileInput(ANALYZED_BOOKS_LIST)
            val strs = inputStream.readBytes().toString(Charsets.UTF_8).split("\n")
            inputStream.close()
            (strs.size)
        }catch (e: IOException){
            0
        }
    }
}