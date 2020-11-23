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
    private var position = 1
    private var isFirstLaunch = true

    fun onProgressChanged(progress: Int) {
        position = progress
        if (position == 0) {
            position++
        }
        viewState.setPositionViewText("$position from $wordListSize")
        viewState.scrollToPosition(position)
    }

    fun onViewCreated(analysisId: Int) {
        if (!isFirstLaunch){
            return
        }
        launch {
            val wordEntities = repository.getWordEntities(analysisId)

            wordEntities?.let {
                wordListSize = wordEntities.size
                val wordCells = ArrayList<WordCell>().apply {
                    wordEntities.forEach { entity ->
                        this.add(wordEntityToCell(entity))
                    }
                }
                viewState.setupCells(wordCells)
                viewState.setSeekBarMaxValue(wordListSize)
                viewState.setPositionViewText("1 from $wordListSize")
                isFirstLaunch = false
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