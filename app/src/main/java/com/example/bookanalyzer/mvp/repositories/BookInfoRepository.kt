package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.BookInfoModel
import com.example.bookanalyzer.data.AnalyzedInfoSaver

class BookInfoRepository(val ctx: Context)  {
    private val analyzedInfoSaver = AnalyzedInfoSaver(ctx)
    fun readInfo(ind:Int) : BookInfoModel {
        return analyzedInfoSaver.getAnalyzedInfo(ind)
    }
}