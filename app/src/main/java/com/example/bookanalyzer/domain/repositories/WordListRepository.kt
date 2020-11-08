package com.example.bookanalyzer.domain.repositories

import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.filesystem.storage.WordData
import com.example.bookanalyzer.data.filesystem.storage.WordListStorage
import com.example.bookanalyzer.domain.models.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordListRepository(
    private var wordListStorage: WordListStorage?,
    private var analysisDao: BookAnalysisDao?
) {

    suspend fun getWordEntities(analysisId: Int) = withContext(Dispatchers.Default) {
        val listPath = analysisDao?.getBookAnalysisById(analysisId)?.wordListPath
        listPath?.let {
            val dataList = wordListStorage?.getWordDataList(listPath)
            val entityList = ArrayList<WordEntity>().apply {
                dataList?.forEach { data ->
                    this.add(data.toWordEntity())
                }
            }
            (entityList)
        }
    }

    private fun WordData.toWordEntity() =
        WordEntity(word, frequency, pos)

}
