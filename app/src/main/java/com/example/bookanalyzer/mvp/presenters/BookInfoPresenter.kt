package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.domain.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView
import com.example.bookanalyzer.ui.activities.BookInfoModel
import kotlinx.coroutines.*
import moxy.MvpPresenter
import kotlin.coroutines.CoroutineContext

class BookInfoPresenter(private val repository: BookInfoRepository) : MvpPresenter<BookInfoView>(),
    CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val job = SupervisorJob()

    fun onViewCreated(analysisId: Int) {
        launch {
            val bookInfoEntity = repository.readInfo(analysisId)
            val bookInfoModel = BookInfoModel(
                bookInfoEntity.path.split("/").last(),
                bookInfoEntity.uniqueWordCount.toString(),
                bookInfoEntity.allWordCount.toString(),
                bookInfoEntity.allCharsCount.toString(),
                bookInfoEntity.avgSentenceLenInWrd.toString(),
                bookInfoEntity.avgSentenceLenInChr.toString(),
                bookInfoEntity.avgWordLen.toString()
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