package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.data.database.AppDataBase
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.models.DbBookAnalysisData
import com.example.bookanalyzer.mvp.presenters.BookAnalysisData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookInfoRepository(val ctx: Context)  {
    private var analysisDao: BookAnalysisDao?=null
    private fun DbBookAnalysisData.toBookAnalysisData() : BookAnalysisData{
        return BookAnalysisData(
            path,
            uniqueWordCount,
            allWordCount, allCharsCount,
            avgSentenceLenInWrd,
            avgSentenceLenInChr,
            avgWordLen,
            wordListPath)
    }

    suspend fun readInfo(ind:Int) = withContext(Dispatchers.Default){
        if (analysisDao == null){
            analysisDao = AppDataBase.getDataBase(ctx)?.bookAnalysisDao()
        }
        var data = analysisDao?.getBookAnalysisById(ind)?.toBookAnalysisData()
        if (data == null) {
            data = BookAnalysisData(
                "",
                0,
                0,
                0,
                0.0,
                0.0,
                0.0,
                ""
            )
        }
        (data)
    }
}