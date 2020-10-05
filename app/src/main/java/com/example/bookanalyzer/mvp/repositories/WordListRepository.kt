package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.data.AnalyzedInfoSaver
import com.example.bookanalyzer.ui.adapters.WordListElemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class WordListRepository(private val ctx: Context) {
    private val analyzedInfoSaver = AnalyzedInfoSaver(ctx)

    suspend fun getWordList(ind: Int) = withContext(Dispatchers.Default){
        (analyzedInfoSaver.getWordList(ind))
    }
}