package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.MenuBookModel
import com.example.bookanalyzer.data.AnalyzedInfoSaver
import com.example.bookanalyzer.data.AnalyzedPathsSaver
import com.example.bookanalyzer.data.FoundPathsSaver
import com.example.bookanalyzer.data.PresentationInfoLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class StartScreenRepository(private val ctx:Context){
    private var pathSaver: FoundPathsSaver = FoundPathsSaver(ctx)
    private var menuContentLoader = PresentationInfoLoader(ctx)
    private var analyzedInfoSaver = AnalyzedInfoSaver(ctx)
    private var analyzedPathsSaver = AnalyzedPathsSaver(ctx)

    private lateinit var bookList:ArrayList<MenuBookModel>

    suspend fun getPreviewList() = withContext(Dispatchers.Default){
        val savedPaths = pathSaver.getSavedPaths()
        bookList = menuContentLoader.getPreviewList(savedPaths)
        (bookList)
    }

    suspend fun getDetailedBookInfo(path:String) = withContext(Dispatchers.Default){
        val model =  menuContentLoader.getDetailedBookInfo(path)
        val ind = analyzedPathsSaver.getIndByPath(path)
        model.wordCount = analyzedInfoSaver.getSavedWordCount(ind)
        (model)
    }

    suspend fun getUniqueWordCount(path:String) = withContext(Dispatchers.Default){
        (analyzedInfoSaver.getSavedWordCount(analyzedPathsSaver.getIndByPath(path)))
    }

    suspend fun saveAllBookPaths(paths:ArrayList<String>) = withContext(Dispatchers.Default){
        pathSaver.saveAll(paths)
    }

    suspend fun saveBookPath(path: String) = withContext(Dispatchers.Default){
        pathSaver.addPath(path)
    }

    suspend fun getAnalyzedBookCount() = withContext(Dispatchers.Default){
        (analyzedPathsSaver.getAnalyzedCount())
    }

    suspend fun getBookIndByPath(path:String)= withContext(Dispatchers.Default){
        (analyzedPathsSaver.getIndByPath(path))
    }
}