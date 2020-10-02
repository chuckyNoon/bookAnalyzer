package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.WordListRepository
import com.example.bookanalyzer.mvp.views.WordListView
import moxy.MvpPresenter

class WordListPresenter(private val repository: WordListRepository) : MvpPresenter<WordListView>(){
    private var wordListSize = 0
    fun onOptionsItemSelected() {
        viewState.finishActivity()
    }

    fun onProgressChanged(Progress:Int) {
        var progress = Progress
        if (progress == 0)
            progress++
        viewState.setPositionText("$progress from $wordListSize")
        viewState.scrollToPosition(progress - 1)
    }

    fun onViewCreated(bookInd:Int) {
        val linesList = repository.getWordList(bookInd) ?: return
        wordListSize = linesList.size
        viewState.setupWordLines(linesList)
        viewState.setSeekBarMaxValue(wordListSize)
        viewState.setPositionText("1 from $wordListSize")
    }
}