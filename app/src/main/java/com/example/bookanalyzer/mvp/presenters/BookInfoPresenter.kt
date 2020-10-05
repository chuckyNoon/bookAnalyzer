package com.example.bookanalyzer.mvp.presenters

import android.os.Handler
import com.example.bookanalyzer.BookInfoModel
import com.example.bookanalyzer.mvp.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView
import kotlinx.coroutines.*
import moxy.MvpPresenter
import kotlin.concurrent.thread

class BookInfoPresenter(private val repository: BookInfoRepository) : MvpPresenter<BookInfoView>(){
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val handler = Handler()

    fun onViewCreated(ind:Int) {
        /*thread {
            val model = repository.readInfo(ind)
            Thread.sleep(10000)
            handler.post {
                viewState.setViewsText(model.path, model.uniqueWordCount, model.allWordCount, model.allCharsCount,
                    model.avgSentenceLenInWrd, model.avgSentenceLenInChr, model.avgWordLen
                )
            }
        }*/
        scope.launch {
            val time1= System.currentTimeMillis()
            val model = withContext(Dispatchers.Default) {
               val t =  repository.readInfo(ind)
                t
            }
            val time2= System.currentTimeMillis()
            println((time2 - time1).toDouble() / 1000)

            viewState.setViewsText(
                model.path,
                model.uniqueWordCount,
                model.allWordCount,
                model.allCharsCount,
                model.avgSentenceLenInWrd,
                model.avgSentenceLenInChr,
                model.avgWordLen
            )
        }
    }

    /*
    }*/



    fun onOptionsItemSelected() {
        viewState.finishActivity()
    }

    fun onWordListButtonClicked(ind:Int) {
        viewState.startWordListActivity(ind)
    }
}