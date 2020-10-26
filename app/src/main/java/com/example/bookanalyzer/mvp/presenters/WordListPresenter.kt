package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.domain.models.WordListRowEntity
import com.example.bookanalyzer.domain.repositories.WordListRepository
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

    fun onViewCreated(analysisId: Int) {
        scope.launch {
            val rowEntities = repository.getWordList(analysisId)
            rowEntities?.let {
                wordListSize = rowEntities.size
                val wordListItems = ArrayList<WordListItem>().apply {
                    rowEntities.forEach { entity ->
                        add(wordListRowEntityToItem(entity))
                    }
                }
                viewState.setupWordItems(wordListItems)
                viewState.setSeekBarMaxValue(wordListSize)
                viewState.setPositionViewText("1 from $wordListSize")
            }
        }
    }

    private fun wordListRowEntityToItem(entity: WordListRowEntity) = WordListItem(
        entity.word,
        entity.frequency.toString(),
        entity.pos.toString()
    )


    fun onOptionsItemBackSelected() {
        viewState.finishActivity()
    }
}