package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.data.database.AppDataBase
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.daos.BookPreviewDao
import com.example.bookanalyzer.data.database.models.DbBookPreviewData
import com.example.bookanalyzer.data.filesystem.*
import com.example.bookanalyzer.mvp.presenters.BookData
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
                    add(BookData(path, null, null, null))
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
                    uniqueWordCount = getUniqueWordCountByPath(dbItem.path)
                }
                add(bookData)
            }
        }
        (bookDataList)
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

    suspend fun saveCurrentBookList(dataListItems: ArrayList<BookData>) =
        withContext(Dispatchers.Default) {
            previewDao?.nukeTable()
            for (item in dataListItems) {
                previewDao?.insertBookPreview(item.toDbBookPreviewData())
            }
        }

    suspend fun getAnalyzedBookCount() = withContext(Dispatchers.Default) {
        val analyses = analysisDao?.getBookAnalyses()
        (analyses?.size ?: 0)
    }

    suspend fun getBookIndByPath(path: String) = withContext(Dispatchers.Default) {
        val analysis = analysisDao?.getBookAnalysisByPath(path)
        (analysis?.id ?: -1)
    }

    private fun DbBookPreviewData.toBookData(): BookData {
        return BookData(path, title, author, imgPath, 0, id)
    }

    private fun BookData.toDbBookPreviewData(): DbBookPreviewData {
        return (DbBookPreviewData(path, title, author, imgPath, id))
    }

    private fun ParsedBookData.toDbBookPreviewData(): DbBookPreviewData {
        return (DbBookPreviewData(path, title, author, imgPath))
    }

    private fun ParsedBookData.toBookData(): BookData {
        return (BookData(path, title, author, imgPath))
    }
}