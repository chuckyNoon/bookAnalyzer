package com.example.bookanalyzer.data

import android.content.Context
import com.example.bookanalyzer.MenuBookModel
import java.io.IOException

class FoundPathsSaver(private val ctx:Context) : FileDataStorage() {
    fun saveAll(books:ArrayList<MenuBookModel>, gb:Int = 0){
        val paths = ArrayList<String>()
        for(book in books){
            paths.add(book.path)
        }
        writePaths(paths)
    }

    fun saveAll(paths:ArrayList<String>){
        writePaths(paths)
    }

    fun addPath(path:String){
        val paths = getSavedPaths()
        paths.add(path)
        writePaths(paths)
    }

    fun deletePath(path:String){
        val paths = getSavedPaths()
        paths.remove(path)
        writePaths(paths)
    }

    private fun writePaths(paths:ArrayList<String>){
        try{
            val output = ctx.openFileOutput(FOUND_BOOKS_LIST, 0)
            val stringBuilder = StringBuilder("")
            for (path in paths){
                stringBuilder.append(path)
                stringBuilder.append("\n")
            }
            output.write(stringBuilder.toString().toByteArray())
            output.close()
        }catch (e:IOException){
        }
    }

    fun getSavedPaths() : ArrayList<String>{
        return try{
            val input = ctx.openFileInput(FOUND_BOOKS_LIST)
            val paths = input.readBytes().toString(Charsets.UTF_8).split("\n")
            input.close()
            val ar = ArrayList<String>()
            for (path in paths){
                if (path.isNotEmpty()){
                    ar.add(path)
                }
            }
            (ar)
        }catch (e:IOException){
            (ArrayList())
        }
    }
}