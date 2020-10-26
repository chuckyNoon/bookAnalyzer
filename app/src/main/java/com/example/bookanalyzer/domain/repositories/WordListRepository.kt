package com.example.bookanalyzer.domain.repositories

import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.filesystem.storage.WordListRowData
import com.example.bookanalyzer.data.filesystem.storage.WordListStorage
import com.example.bookanalyzer.domain.models.WordListRowEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordListRepository(
    private var wordListStorage: WordListStorage?,
    private var analysisDao: BookAnalysisDao?
) {

    suspend fun getWordList(analysisId: Int) = withContext(Dispatchers.Default) {
        val listPath = analysisDao?.getBookAnalysisById(analysisId)?.wordListPath
        listPath?.let {
            val dataList = wordListStorage?.getWordList(listPath)
            val entityList = ArrayList<WordListRowEntity>().apply {
                dataList?.forEach { data ->
                    this.add(wordListRowDataToEntity(data))
                }
            }
            (entityList)
        }
    }

    private fun wordListRowDataToEntity(data: WordListRowData) =
        WordListRowEntity(data.word, data.frequency, data.pos)
}
