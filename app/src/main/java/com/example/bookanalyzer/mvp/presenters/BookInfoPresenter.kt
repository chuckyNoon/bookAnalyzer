package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView

class BookInfoPresenter(private val view: BookInfoView, private val repository: BookInfoRepository){
    fun onOptionsItemSelected() {
        view.finishActivity()
    }

    fun onWordListButtonClicked(ind:Int) {
        view.startWordListActivity(ind)
    }

    fun onViewCreated(ind:Int) {
        val model = repository.readInfo(ind)
        view.setViewsText(model.path, model.uniqueWordCount, model.allWordCount,
            model.allCharsCount, model.avgSentenceLenInWrd, model.avgSentenceLenInChr, model.avgWordLen)
    }
}