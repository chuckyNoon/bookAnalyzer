package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.WordListRepository
import com.example.bookanalyzer.mvp.views.WordListView

class WordListPresenter(private val view: WordListView, private val repository: WordListRepository){
    private var wordListSize = 0
    fun onOptionsItemSelected() {
        view.finishActivity()
    }

    fun onProgressChanged(Progress:Int) {
        var progress = Progress
        if (progress == 0)
            progress++
        view.setPositionText("$progress from $wordListSize")
        view.scrollToPosition(progress - 1)
    }

    fun onViewCreated(bookInd:Int) {
        val linesList = repository.getWordList(bookInd) ?: return
        wordListSize = linesList.size
        view.setupWordLines(linesList)
        view.setSeekBarMaxValue(wordListSize)
        view.setPositionText("1 from $wordListSize")
    }
}