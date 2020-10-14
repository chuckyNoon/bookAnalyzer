package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.data.database.AppDataBase
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.daos.BookPreviewDao
import com.example.bookanalyzer.data.database.models.DbBookPreviewData
import com.example.bookanalyzer.data.filesystem.ParsedBookData
import com.example.bookanalyzer.data.filesystem.PreviewDataParser
import com.example.bookanalyzer.mvp.presenters.BookDisplayData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class StartScreenRepository(private val ctx:Context){
    private var previewDataParser = PreviewDataParser(ctx)
    private var analysisDao:BookAnalysisDao? = null
    private var previewDao: BookPreviewDao?=null

    suspend fun getSimplePreviewList(paths:ArrayList<String>) = withContext(Dispatchers.Default) {
        val ar = ArrayList<BookDisplayData>().apply {
            paths.forEach {path->
                add(BookDisplayData(path,null,null,null))
            }
        }
        (ar)
    }

    suspend fun getCompletePreviewList(paths:ArrayList<String>?) = withContext(Dispatchers.Default){
        if (analysisDao == null){
            analysisDao = AppDataBase.getDataBase(ctx)?.bookAnalysisDao()
        }
        if (previewDao == null){
            previewDao = AppDataBase.getDataBase(ctx)?.bookPreviewDao()
        }
        val dbItems = if (paths != null){
            previewDao?.nukeTable()
            val parsedDataList =  previewDataParser.getPreviewList(paths)
            val dbDataList = ArrayList<DbBookPreviewData>()
            previewDao?.let {
                parsedDataList.forEach { item ->
                    val dbItem = item.toDbBookPreviewData()
                    it.insertBookPreview(dbItem)
                    dbDataList.add(dbItem)
                }
            }
            (dbDataList)
        }else {
            (previewDao?.getBookPreviews()?:ArrayList())
        }
        val bookDisplayDataList = ArrayList<BookDisplayData>().apply{
            for (item in dbItems){
                val displayData = item.toDisplayData()
                displayData.wordCount = getUniqueWordCount(item.path)
                add(displayData)
            }
        }
        (bookDisplayDataList)
    }

    suspend fun getDetailedBookInfo(path:String) = withContext(Dispatchers.Default){
        val model =  previewDataParser.getPreviewData(path).toDisplayData()
        model.wordCount = getUniqueWordCount(path)
        (model)
    }

    suspend fun getUniqueWordCount(path:String) = withContext(Dispatchers.Default){
        (analysisDao?.getBookAnalysisByPath(path)?.uniqueWordCount?:0)
    }

    suspend fun saveCurrentMenu(items:ArrayList<BookDisplayData>) = withContext(Dispatchers.Default){
        previewDao?.nukeTable()
        for (item in items){
            previewDao?.insertBookPreview(item.toDbBookPreviewData())
        }
    }

    suspend fun getAnalyzedBookCount() = withContext(Dispatchers.Default){
        (analysisDao?.getBookAnalyses()?.size?:0)
    }

    suspend fun getBookIndByPath(path:String)= withContext(Dispatchers.Default){
        (analysisDao?.getBookAnalysisByPath(path)?.id?:-1)
    }

    private fun DbBookPreviewData.toDisplayData() : BookDisplayData {
        return BookDisplayData( path, title, author, imgPath,0, id)
    }

    private fun BookDisplayData.toDbBookPreviewData():DbBookPreviewData{
        return (DbBookPreviewData(path, title, author, imgPath, id))
    }

    private fun ParsedBookData.toDbBookPreviewData():DbBookPreviewData{
        return (DbBookPreviewData(path, title, author, imgPath))
    }

    private fun ParsedBookData.toDisplayData(): BookDisplayData{
        return (BookDisplayData(path, title, author, imgPath))
    }
}