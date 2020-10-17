package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.data.database.AppDataBase
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.daos.BookPreviewDao
import com.example.bookanalyzer.data.database.models.DbBookPreviewData
import com.example.bookanalyzer.data.filesystem.preview_parser.BookPreviewListParser
import com.example.bookanalyzer.data.filesystem.preview_parser.ParsedBookData
import com.example.bookanalyzer.mvp.presenters.BookData
import com.example.bookanalyzer.mvp.presenters.ANALYSIS_NOT_EXIST
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class StartScreenRepository(private val ctx: Context) {
    private var bookPreviewListParser = BookPreviewListParser(ctx)
    private var analysisDao: BookAnalysisDao? = null
    private var previewDao: BookPreviewDao? = null

    fun initDataSources() {
        analysisDao = AppDataBase.getDataBase(ctx)?.bookAnalysisDao()
        previewDao = AppDataBase.getDataBase(ctx)?.bookPreviewDao()
    }

    suspend fun getInitialDataList(paths: ArrayList<String>) =
        withContext(Dispatchers.Default) {
            val bookDataList = ArrayList<BookData>().apply {
                paths.forEach { path ->
                    val data = BookData(
                        path,
                        null,
                        null,
                        null,
                        0,
                        ANALYSIS_NOT_EXIST
                    )
                    add(data)
                }
            }
            (bookDataList)
        }

    suspend fun insertDataFromPathsInDb(bookPaths: ArrayList<String>) =
        withContext(Dispatchers.Default) {
            previewDao?.let { previewDao ->
                previewDao.nukeTable()
                val parsedDataList = bookPreviewListParser.getParsedDataList(bookPaths)
                for (parsedData in parsedDataList) {
                    previewDao.insertBookPreview(parsedData.toDbBookPreviewData())
                }
            }
        }

    suspend fun getCompleteDataList() = withContext(Dispatchers.Default) {
        val dbItems = previewDao?.getBookPreviews() ?: ArrayList()
        val bookDataList = ArrayList<BookData>().apply {
            for (dbItem in dbItems) {
                val bookData = dbItem.toBookData().apply {
                    uniqueWordCount = getUniqueWordCountByPath(path)
                    analysisId = getAnalysisIdByPath(path)
                }
                add(bookData)
            }
        }
        (bookDataList)
    }

    suspend fun getAnalysisIdByPath(bookPath: String) =
        withContext(Dispatchers.Default) {
            (analysisDao?.getBookAnalysisByPath(bookPath)?.id ?: ANALYSIS_NOT_EXIST)
        }

    suspend fun getCompleteBookData(bookPath: String) = withContext(Dispatchers.Default) {
        val bookData = previewDao?.getBookPreviewByPath(bookPath)?.toBookData()?.apply {
            uniqueWordCount = getUniqueWordCountByPath(bookPath)
        }
        (bookData)
    }

    suspend fun getUniqueWordCountByPath(bookPath: String) = withContext(Dispatchers.Default) {
        val analysis = analysisDao?.getBookAnalysisByPath(bookPath)
        (analysis?.uniqueWordCount ?: 0)
    }

    suspend fun saveCurrentBookList(dataList: ArrayList<BookData>) =
        withContext(Dispatchers.Default) {
            previewDao?.nukeTable()
            for (item in dataList) {
                previewDao?.insertBookPreview(item.toDbBookPreviewData())
            }
        }

    private fun DbBookPreviewData.toBookData(): BookData {
        return BookData(path, title, author, imgPath, 0, analysisId)
    }

    private fun BookData.toDbBookPreviewData(): DbBookPreviewData {
        return (DbBookPreviewData(path, title, author, imgPath, analysisId))
    }

    private fun ParsedBookData.toDbBookPreviewData(): DbBookPreviewData {
        return (DbBookPreviewData(path, title, author, imgPath, ANALYSIS_NOT_EXIST))
    }

}