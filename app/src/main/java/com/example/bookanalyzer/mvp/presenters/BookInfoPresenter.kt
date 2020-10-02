package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView
import moxy.MvpPresenter

class BookInfoPresenter(private val repository: BookInfoRepository) : MvpPresenter<BookInfoView>(){
    fun onOptionsItemSelected() {
        viewState.finishActivity()
    }

    fun onWordListButtonClicked(ind:Int) {
        viewState.startWordListActivity(ind)
    }

    fun onViewCreated(ind:Int) {
        val model = repository.readInfo(ind)
        viewState.setViewsText(model.path, model.uniqueWordCount, model.allWordCount,
            model.allCharsCount, model.avgSentenceLenInWrd, model.avgSentenceLenInChr, model.avgWordLen)
    }
}