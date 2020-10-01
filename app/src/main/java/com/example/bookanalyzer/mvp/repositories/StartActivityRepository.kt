package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.ABookInfo
import com.example.bookanalyzer.data.MenuContentLoader
import com.example.bookanalyzer.data.PathSaver

class StartActivityRepository(private val context:Context){
    private var pathSaver: PathSaver = PathSaver(context)
    private var menuContentLoader = MenuContentLoader(context)
    private lateinit var bookList:ArrayList<ABookInfo>

    fun getPrimaryList() : ArrayList<ABookInfo>{
        bookList = menuContentLoader.firstStage()
        return bookList
    }

    fun getDetailedList() :ArrayList<ABookInfo>{
        for (i in bookList.indices ) {
            val oldElem = bookList[i]
            val newElem = menuContentLoader.getDetailedInfo(oldElem.path)

            oldElem.name = newElem.name
            oldElem.author = newElem.author
            oldElem.bitmap = newElem.bitmap
            oldElem.wordCount = newElem.wordCount
        }
        return bookList
    }

    fun getNewDetailedModel(path:String) : ABookInfo {
        return menuContentLoader.getDetailedInfo(path)
    }

    fun saveAllBookPaths(paths:ArrayList<String>) {
        pathSaver.saveAll(paths)
    }

    fun saveBookPath(path: String) {
        pathSaver.addPath(path)
    }

}