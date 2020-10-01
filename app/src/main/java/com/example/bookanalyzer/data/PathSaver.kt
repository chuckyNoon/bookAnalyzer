package com.example.bookanalyzer.data

import android.content.Context
import com.example.bookanalyzer.ABookInfo
import java.io.IOException

class PathSaver(private val ctx:Context) {
    companion object {
        val SAVED_PATHS_FILE:String = "main"
    }

    fun saveAll(books:ArrayList<ABookInfo>, gb:Int = 0){
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
        val paths = findSavedPaths()
        paths.add(path)
        writePaths(paths)
    }

    fun deletePath(path:String){
        val paths = findSavedPaths()
        paths.remove(path)
        writePaths(paths)
    }

    private fun writePaths(paths:ArrayList<String>){
        try{
            val output = ctx.openFileOutput(SAVED_PATHS_FILE, 0)
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

    private fun findSavedPaths() : ArrayList<String>{
        return try{
            val input = ctx.openFileInput(SAVED_PATHS_FILE)
            val paths = input.readBytes().toString(Charsets.UTF_8).split("\n")
            input.close()
            (ArrayList(paths))
        }catch (e:IOException){
            (ArrayList())
        }
    }
}