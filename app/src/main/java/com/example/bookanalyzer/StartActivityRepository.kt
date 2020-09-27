package com.example.bookanalyzer

import android.content.Context

class StartActivityRepository(private val context:Context) : IStartContract.Repository {
    private var pathSaver:PathSaver = PathSaver(context)
    private var menuContentLoader = MenuContentLoader(context)
    private lateinit var bookList:ArrayList<ABookInfo>

    override fun getPrimaryList() : ArrayList<ABookInfo>{
        bookList = menuContentLoader.firstStage()
        return bookList
    }

    override fun getDetailedList() :ArrayList<ABookInfo>{
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

    override fun getNewDetailedModel(path:String) : ABookInfo{
        return menuContentLoader.getDetailedInfo(path)
    }

    override fun saveAllBookPaths(paths:ArrayList<String>) {
        pathSaver.saveAll(paths)
    }

    override fun saveBookPath(path: String) {
        pathSaver.addPath(path)
    }

}