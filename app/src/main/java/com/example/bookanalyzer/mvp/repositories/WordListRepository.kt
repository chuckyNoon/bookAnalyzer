package com.example.bookanalyzer.mvp.repositories

import android.content.Context
import com.example.bookanalyzer.data.filesystem.WordListStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordListRepository(private val ctx: Context) {
    private val wordListStorage = WordListStorage(ctx)

    suspend fun getWordList(ind: Int) = withContext(Dispatchers.Default) {
        (wordListStorage.getWordList(ind))
    }
}