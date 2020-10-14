package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView
import kotlinx.coroutines.*
import moxy.MvpPresenter

data class BookAnalysisData(
    var path:String,
    var uniqueWordCount:Int,
    var allWordCount:Int,
    var allCharsCount:Int,
    var avgSentenceLenInWrd:Double,
    var avgSentenceLenInChr:Double,
    var avgWordLen:Double,
    var wordListPath:String)
{
}

class BookInfoPresenter(private val repository: BookInfoRepository) : MvpPresenter<BookInfoView>(){
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun onViewCreated(ind:Int) {
        scope.launch {
            val model = repository.readInfo(ind)

            viewState.setViewsText(
                model.path.split("/").last(),
                model.uniqueWordCount.toString(),
                model.allWordCount.toString(),
                model.allCharsCount.toString(),
                model.avgSentenceLenInWrd.toString(),
                model.avgSentenceLenInChr.toString(),
                model.avgWordLen.toString()
            )
        }
    }

    fun onOptionsItemSelected() {
        viewState.finishActivity()
    }

    fun onWordListButtonClicked(ind:Int) {
        viewState.startWordListActivity(ind)
    }
}