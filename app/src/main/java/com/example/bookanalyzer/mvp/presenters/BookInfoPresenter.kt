package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.domain.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView
import com.example.bookanalyzer.ui.activities.BookInfoModel
import kotlinx.coroutines.*
import moxy.MvpPresenter

class BookInfoPresenter(private val repository: BookInfoRepository) : MvpPresenter<BookInfoView>() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun onViewCreated(analysisId: Int) {
        scope.launch {
            val bookData = repository.readInfo(analysisId)
            val bookInfoModel = BookInfoModel(
                bookData.path.split("/").last(),
                bookData.uniqueWordCount.toString(),
                bookData.allWordCount.toString(),
                bookData.allCharsCount.toString(),
                bookData.avgSentenceLenInWrd.toString(),
                bookData.avgSentenceLenInChr.toString(),
                bookData.avgWordLen.toString()
            )
            viewState.setViewsText(bookInfoModel)
        }
    }

    fun onOptionsItemBackSelected() {
        viewState.finishActivity()
    }

    fun onWordListButtonClicked(analysisId: Int) {
        viewState.startWordListActivity(analysisId)
    }
}