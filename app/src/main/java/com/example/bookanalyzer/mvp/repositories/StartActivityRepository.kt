package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.MenuBookModel
import com.example.bookanalyzer.data.AnalyzedInfoSaver
import com.example.bookanalyzer.data.AnalyzedPathsSaver
import com.example.bookanalyzer.data.FoundPathsSaver
import com.example.bookanalyzer.data.PresentationInfoLoader
import kotlin.collections.ArrayList

class StartActivityRepository(private val ctx:Context){
    private var pathSaver: FoundPathsSaver = FoundPathsSaver(ctx)
    private var menuContentLoader = PresentationInfoLoader(ctx)
    private var analyzedInfoSaver = AnalyzedInfoSaver(ctx)
    private var analyzedPathsSaver = AnalyzedPathsSaver(ctx)

    private lateinit var bookList:ArrayList<MenuBookModel>

    fun getPreviewList() : ArrayList<MenuBookModel>{
        val savedPaths = pathSaver.getSavedPaths()
        for (book in savedPaths){
            println("t${book}")
        }
        bookList = menuContentLoader.getPreviewList(savedPaths)
        return bookList
    }

    fun getDetailedBookInfo(path:String) : MenuBookModel{
        val model = menuContentLoader.getDetailedBookInfo(path)
        val ind = analyzedPathsSaver.getIndByPath(path)
        model.wordCount = analyzedInfoSaver.getSavedWordCount(ind)
        return model
    }

    fun getUniqueWordCount(path:String): Int{
        return analyzedInfoSaver.getSavedWordCount(analyzedPathsSaver.getIndByPath(path))
    }

    fun saveAllBookPaths(paths:ArrayList<String>) {
        pathSaver.saveAll(paths)
    }

    fun saveBookPath(path: String) {
        pathSaver.addPath(path)
    }
}