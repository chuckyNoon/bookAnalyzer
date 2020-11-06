package com.example.bookanalyzer.domain.repositories

import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.models.DbBookAnalysisData
import com.example.bookanalyzer.domain.models.BookInfoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookInfoRepository(private val analysisDao: BookAnalysisDao?) {

    suspend fun readInfo(analysisId: Int) = withContext(Dispatchers.Default) {
        var data = analysisDao?.getBookAnalysisById(analysisId)?.toBookAnalysisData()

        if (data == null) {
            data = BookInfoEntity(
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

    private fun DbBookAnalysisData.toBookAnalysisData(): BookInfoEntity {
        return BookInfoEntity(
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