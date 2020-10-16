package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView
import com.example.bookanalyzer.ui.activities.BookInfoModel
import kotlinx.coroutines.*
import moxy.MvpPresenter

data class BookAnalysisData(
    var path: String,
    var uniqueWordCount: Int,
    var allWordCount: Int,
    var allCharsCount: Int,
    var avgSentenceLenInWrd: Double,
    var avgSentenceLenInChr: Double,
    var avgWordLen: Double,
    var wordListPath: String
)

class BookInfoPresenter(private val repository: BookInfoRepository) : MvpPresenter<BookInfoView>() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun onViewCreated(ind: Int) {
        scope.launch {
            repository.initDataSources()
            val bookData = repository.readInfo(ind)
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

    fun onWordListButtonClicked(ind: Int) {
        viewState.startWordListActivity(ind)
    }
}