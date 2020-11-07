package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.domain.models.WordListRowEntity
import com.example.bookanalyzer.domain.repositories.WordListRepository
import com.example.bookanalyzer.mvp.views.WordListView
import com.example.bookanalyzer.ui.adapters.word_list_adapter.WordCell
import kotlinx.coroutines.*
import moxy.MvpPresenter
import kotlin.coroutines.CoroutineContext

class WordListPresenter(
    private val repository: WordListRepository,
) : MvpPresenter<WordListView>(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var wordListSize = 0

    fun onProgressChanged(Progress: Int) {
        var progress = Progress
        if (progress == 0) {
            progress++
        }
        viewState.setPositionViewText("$progress from $wordListSize")
        viewState.scrollToPosition(progress)
    }

    fun onViewCreated(analysisId: Int) {
        launch {
            val rowEntities = repository.getWordList(analysisId)

            rowEntities?.let {
                wordListSize = rowEntities.size
                val wordListItems = ArrayList<WordCell>().apply {
                    rowEntities.forEach { entity ->
                        add(wordListRowEntityToItem(entity))
                    }
                }
                viewState.setupWordCells(wordListItems)
                viewState.setSeekBarMaxValue(wordListSize)
                viewState.setPositionViewText("1 from $wordListSize")
            }
        }
    }

    fun onOptionsItemBackSelected() {
        viewState.finishActivity()
    }


    private fun wordListRowEntityToItem(entity: WordListRowEntity) = WordCell(
        entity.word,
        entity.frequency.toString(),
        entity.pos.toString()
    )
}