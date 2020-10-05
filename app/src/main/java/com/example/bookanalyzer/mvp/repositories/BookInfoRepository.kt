package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.BookInfoModel
import com.example.bookanalyzer.data.AnalyzedInfoSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookInfoRepository(val ctx: Context)  {
    private val analyzedInfoSaver = AnalyzedInfoSaver(ctx)

    /*suspend fun readInfo(ind:Int) = withContext(Dispatchers.Default) {
        (analyzedInfoSaver.getAnalyzedInfo(ind))
    }*/
    fun readInfo(ind:Int) : BookInfoModel {
        return (analyzedInfoSaver.getAnalyzedInfo(ind))
    }
}