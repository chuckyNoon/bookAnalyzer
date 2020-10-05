package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.AnalyzedBookModel
import com.example.bookanalyzer.data.AnalyzedInfoSaver
import com.example.bookanalyzer.analyzer.BookAnalysis
import com.example.bookanalyzer.data.AnalyzedPathsSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream

class LoaderScreenRepository(private val ctx:Context) {
    private val analyzedInfoSaver = AnalyzedInfoSaver(ctx)
    private val analyzedPathsSaver = AnalyzedPathsSaver(ctx)
    private val analysis = BookAnalysis(ctx)

    suspend fun analyzeBook(inStream:FileInputStream, path:String) = withContext(Dispatchers.Default){
        (analysis.startAnalyze(inStream, path))
    }

    suspend fun saveAnalysis(bookPath:String, ind:Int, analyzedBookModel: AnalyzedBookModel, redo:Boolean)
            = withContext(Dispatchers.Default){
        analyzedInfoSaver.saveAnalyzedInfo(bookPath, ind,analyzedBookModel, redo)
        analyzedPathsSaver.savePath(bookPath, ind)
    }
}