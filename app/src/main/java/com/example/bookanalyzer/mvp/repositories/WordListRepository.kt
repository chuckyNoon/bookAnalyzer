package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.data.AnalyzedInfoSaver
import com.example.bookanalyzer.ui.adapters.WordListElemModel
import java.io.IOException

class WordListRepository(private val ctx: Context) {
    private val analyzedInfoSaver = AnalyzedInfoSaver(ctx)

    fun getWordList(ind: Int): ArrayList<WordListElemModel>?{
        return analyzedInfoSaver.getWordList(ind)
    }
}