package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.domain.models.WordEntity
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
            val wordEntities = repository.getWordEntities(analysisId)

            wordEntities?.let {
                wordListSize = wordEntities.size
                val wordCells = ArrayList<WordCell>().apply {
                    wordEntities.forEach { entity ->
                        add(wordEntityToCell(entity))
                    }
                }
                viewState.setupWordCells(wordCells)
                viewState.setSeekBarMaxValue(wordListSize)
                viewState.setPositionViewText("1 from $wordListSize")
            }
        }
    }

    fun onOptionsItemBackSelected() {
        viewState.finishActivity()
    }


    private fun wordEntityToCell(entity: WordEntity) = WordCell(
        entity.word,
        entity.frequency.toString(),
        entity.pos.toString()
    )
}