package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.data.database.AppDataBase
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.filesystem.WordListStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordListRepository(private val ctx: Context) {

    private var wordListStorage: WordListStorage? = null
    private var analysisDao: BookAnalysisDao? = null

    fun initSources() {
        wordListStorage = WordListStorage(ctx)
        analysisDao = AppDataBase.getDataBase(ctx)?.bookAnalysisDao()
    }

    suspend fun getWordList(analysisId: Int) = withContext(Dispatchers.Default) {
        val listPath = analysisDao?.getBookAnalysisById(analysisId)?.wordListPath
        listPath?.let {
            (wordListStorage?.getWordList(listPath))
        }
    }
}