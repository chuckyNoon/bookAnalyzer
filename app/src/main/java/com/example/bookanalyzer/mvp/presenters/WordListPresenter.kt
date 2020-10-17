package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.data.filesystem.WordListRowData
import com.example.bookanalyzer.mvp.repositories.WordListRepository
import com.example.bookanalyzer.mvp.views.WordListView
import com.example.bookanalyzer.ui.adapters.WordListItem
import kotlinx.coroutines.*
import moxy.MvpPresenter

class WordListPresenter(private val repository: WordListRepository) : MvpPresenter<WordListView>() {
    private var wordListSize = 0
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun onProgressChanged(Progress: Int) {
        var progress = Progress
        if (progress == 0) {
            progress++
        }
        viewState.setPositionViewText("$progress from $wordListSize")
        viewState.scrollToPosition(progress)
    }

    fun onViewCreated(bookId: Int) {
        scope.launch {
            val rowList = repository.getWordList(bookId)
            rowList?.let {
                wordListSize = rowList.size
                val wordListItemArray = ArrayList<WordListItem>().apply {
                    rowList.forEach { row ->
                        add(row.toWordListItem())
                    }
                }
                viewState.setupWordItems(wordListItemArray)
                viewState.setSeekBarMaxValue(wordListSize)
                viewState.setPositionViewText("1 from $wordListSize")
            }
        }
    }

    private fun WordListRowData.toWordListItem(): WordListItem {
        return WordListItem(word, frequency.toString(), pos.toString())
    }

    fun onOptionsItemBackSelected() {
        viewState.finishActivity()
    }
}