package com.example.bookanalyzer.domain.repositories

import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.daos.BookPreviewDao
import com.example.bookanalyzer.data.database.models.DbBookPreviewData
import com.example.bookanalyzer.data.filesystem.storage.ImageStorage
import com.example.bookanalyzer.data.filesystem.data_extractors.preview_parser.BookPreviewListParser
import com.example.bookanalyzer.data.filesystem.data_extractors.preview_parser.ParsedPreviewData
import com.example.bookanalyzer.domain.models.BookPreviewEntity
import com.example.bookanalyzer.mvp.presenters.ANALYSIS_NOT_EXIST
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class StartScreenRepository(
    private val analysisDao: BookAnalysisDao?,
    private val previewDao: BookPreviewDao?,
    private val bookPreviewListParser: BookPreviewListParser,
    private val imageStorage: ImageStorage
) {

    suspend fun getInitialDataList(paths: ArrayList<String>) =
        withContext(Dispatchers.Default) {
            val bookDataList = ArrayList<BookPreviewEntity>().apply {
                paths.forEach { path ->
                    val data = BookPreviewEntity(path = path, analysisId = ANALYSIS_NOT_EXIST)
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
                    val dbPreviewData = parsedData.toDbBookPreviewData().apply {
                        this.imgPath = saveImageFromBookInLocalStorage(parsedData)
                    }
                    previewDao.insertBookPreview(dbPreviewData)
                }
            }
        }

    private fun saveImageFromBookInLocalStorage(parsedData: ParsedPreviewData): String? {
        parsedData.imgByteArray?.let { imgByteArray ->
            val forSavingImgPath = imageStorage.getSaveImgPathByTitle(parsedData.title)
            if (forSavingImgPath != null) {
                val isSuccessfullySaved =
                    imageStorage.saveImage(imgByteArray, forSavingImgPath)
                return if (isSuccessfullySaved) {
                    (forSavingImgPath)
                } else {
                    (null)
                }
            }
        }
        return null
    }

    suspend fun getCompleteDataList() = withContext(Dispatchers.Default) {
        val dbItems = previewDao?.getBookPreviews() ?: ArrayList()
        val bookDataList = ArrayList<BookPreviewEntity>().apply {
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

    suspend fun getUniqueWordCountByPath(bookPath: String) = withContext(Dispatchers.Default) {
        val analysis = analysisDao?.getBookAnalysisByPath(bookPath)
        (analysis?.uniqueWordCount ?: 0)
    }

    suspend fun saveCurrentBookList(previewEntityList: ArrayList<BookPreviewEntity>) =
        withContext(Dispatchers.Default) {
            previewDao?.nukeTable()
            for (item in previewEntityList) {
                previewDao?.insertBookPreview(item.toDbBookPreviewData())
            }
        }

    private fun DbBookPreviewData.toBookData(): BookPreviewEntity {
        return BookPreviewEntity(path, title, author, imgPath, 0, analysisId)
    }

    private fun BookPreviewEntity.toDbBookPreviewData(): DbBookPreviewData {
        return DbBookPreviewData(path, title, author, imgPath, analysisId)
    }

    private fun ParsedPreviewData.toDbBookPreviewData(): DbBookPreviewData {
        return DbBookPreviewData(path, title, author, null, ANALYSIS_NOT_EXIST)
    }
}