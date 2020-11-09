package com.example.bookanalyzer.domain.repositories

import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.models.DbBookAnalysisData
import com.example.bookanalyzer.domain.models.ShowedAnalysisEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookAnalysisRepository(private val analysisDao: BookAnalysisDao?) {

    suspend fun getAnalysis(analysisId: Int) = withContext(Dispatchers.Default) {
        var showedAnalysisEntity = analysisDao?.getBookAnalysisById(analysisId)?.toShowedAnalysisEntity()

        if (showedAnalysisEntity == null) {
            showedAnalysisEntity = ShowedAnalysisEntity(
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
        (showedAnalysisEntity)
    }

    private fun DbBookAnalysisData.toShowedAnalysisEntity(): ShowedAnalysisEntity {
        return ShowedAnalysisEntity(
            path,
            uniqueWordCount,
            allWordCount,
            allCharCount,
            avgSentenceLenInWrd,
            avgSentenceLenInChr,
            avgWordLen,
            wordListPath
        )
    }
}