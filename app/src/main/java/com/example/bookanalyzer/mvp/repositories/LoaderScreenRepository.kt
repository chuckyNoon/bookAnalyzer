package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.analyzer.AnalyzedBookModel
import com.example.bookanalyzer.data.filesystem.WordListStorage
import com.example.bookanalyzer.analyzer.BookAnalyzer
import com.example.bookanalyzer.data.database.AppDataBase
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.models.DbBookAnalysisData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoaderScreenRepository(private val ctx: Context) {
    private var wordListStorage: WordListStorage? = null
    private var analyzer: BookAnalyzer? = null
    private var analysisDao: BookAnalysisDao? = null

    suspend fun initDataSources() = withContext(Dispatchers.Default) {
        wordListStorage = WordListStorage(ctx)
        analyzer = BookAnalyzer(ctx)
        analysisDao = AppDataBase.getDataBase(ctx)?.bookAnalysisDao()
    }

    suspend fun analyzeBook(path: String) = withContext(Dispatchers.Default) {
        (analyzer?.getAnalysis(path))
    }

    suspend fun getAnalysisIdByPath(path: String) = withContext(Dispatchers.Default) {
        (analysisDao?.getBookAnalysisByPath(path)?.id)
    }

    private suspend fun getNewListInd() = withContext(Dispatchers.Default) {
        val analysisCount = analysisDao?.getBookAnalyses()?.size
        val listInd = 1 + (analysisCount ?: 0)
        (listInd)
    }

    suspend fun saveAnalysis(analyzedBookModel: AnalyzedBookModel) =
        withContext(Dispatchers.Default) {
            val listInd = getNewListInd()
            val wordListPath = wordListStorage?.savedWordListPathByInd(listInd)
            val dbBookAnalysisData = DbBookAnalysisData(
                path = analyzedBookModel.path,
                uniqueWordCount = analyzedBookModel.uniqueWordCount,
                allWordCount = analyzedBookModel.allWordCount,
                allCharCount = analyzedBookModel.allCharCount,
                avgSentenceLenInWrd = analyzedBookModel.avgSentenceLenInWrd,
                avgSentenceLenInChr = analyzedBookModel.avgSentenceLenInChr,
                avgWordLen = analyzedBookModel.avgWordLen,
                wordListPath = wordListPath ?: ""
            )
            analysisDao?.insertBookAnalysis(dbBookAnalysisData)
            wordListStorage?.saveWordList(analyzedBookModel.wordMap, listInd)
        }
}