package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.analyzer.AnalyzedBookModel
import com.example.bookanalyzer.data.filesystem.WordListStorage
import com.example.bookanalyzer.analyzer.BookAnalysis
import com.example.bookanalyzer.data.database.AppDataBase
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.models.DbBookAnalysisData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoaderScreenRepository(private val ctx:Context) {
    private val wordListStorage = WordListStorage(ctx)
    private val analysis = BookAnalysis(ctx)
    private var analysisDao:BookAnalysisDao?=null

    suspend fun analyzeBook(path:String) = withContext(Dispatchers.Default){
        (analysis.getAnalysis(path))
    }

    suspend fun saveAnalysis(bookPath:String, ind:Int, analyzedBookModel: AnalyzedBookModel)
            = withContext(Dispatchers.Default){
        if (analysisDao == null){
            analysisDao = AppDataBase.getDataBase(ctx)?.bookAnalysisDao()
        }
        val bookInfoModel = DbBookAnalysisData(bookPath,
            analyzedBookModel.uniqueWordCount,
            analyzedBookModel.allWordCount,
            analyzedBookModel.allCharCount,
            analyzedBookModel.avgSentenceLenInWrd,
            analyzedBookModel.avgSentenceLenInChr,
            analyzedBookModel.avgWordLen,
            wordListStorage.savedWordListPath(ind))

        analysisDao?.insertBookAnalysis(bookInfoModel)
        wordListStorage.saveWordList(analyzedBookModel.wordMap, ind)
    }
}