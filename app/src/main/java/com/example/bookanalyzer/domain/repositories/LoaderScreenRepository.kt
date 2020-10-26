package com.example.bookanalyzer.domain.repositories

import com.example.bookanalyzer.data.filesystem.data_extractors.analyzer.BookAnalysisData
import com.example.bookanalyzer.data.filesystem.storage.WordListStorage
import com.example.bookanalyzer.data.filesystem.data_extractors.analyzer.BookAnalyzer
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.models.DbBookAnalysisData
import com.example.bookanalyzer.domain.models.BookAnalysisEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoaderScreenRepository(
    private var wordListStorage: WordListStorage,
    private var analyzer: BookAnalyzer,
    private var analysisDao: BookAnalysisDao?
) {

    suspend fun analyzeBook(path: String) = withContext(Dispatchers.Default) {
        (analysisDataToEntity(analyzer.getAnalysis(path)))
    }

    suspend fun getAnalysisIdByPath(path: String) = withContext(Dispatchers.Default) {
        (analysisDao?.getBookAnalysisByPath(path)?.id)
    }

    suspend fun saveAnalysis(analysisEntity: BookAnalysisEntity) =
        withContext(Dispatchers.Default) {
            val listInd = getNewListInd()
            val wordListPath = wordListStorage.savedWordListPathByInd(listInd)
            val dbBookAnalysisData = analysisEntityToDbData(analysisEntity).apply {
                this.wordListPath = wordListPath
            }
            analysisDao?.insertBookAnalysis(dbBookAnalysisData)
            wordListStorage.saveWordList(analysisEntity.wordMap, listInd)
        }

    private suspend fun getNewListInd() = withContext(Dispatchers.Default) {
        val analysisCount = analysisDao?.getBookAnalyses()?.size
        val listInd = 1 + (analysisCount ?: 0)
        (listInd)
    }

    private fun analysisDataToEntity(entity: BookAnalysisData) =
        BookAnalysisEntity(
            entity.path,
            entity.uniqueWordCount,
            entity.allWordCount,
            entity.allCharCount,
            entity.avgSentenceLenInWrd,
            entity.avgSentenceLenInChr,
            entity.avgWordLen,
            entity.wordMap
        )

    private fun analysisEntityToDbData(entity: BookAnalysisEntity) = DbBookAnalysisData(
        path = entity.path,
        uniqueWordCount = entity.uniqueWordCount,
        allWordCount = entity.allWordCount,
        allCharCount = entity.allCharCount,
        avgSentenceLenInWrd = entity.avgSentenceLenInWrd,
        avgSentenceLenInChr = entity.avgSentenceLenInChr,
        avgWordLen = entity.avgWordLen,
        wordListPath = ""
    )
}