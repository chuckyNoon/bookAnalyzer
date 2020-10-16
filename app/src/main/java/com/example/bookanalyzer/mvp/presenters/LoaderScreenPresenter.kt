package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import kotlinx.coroutines.*
import moxy.MvpPresenter

class LoaderScreenPresenter(private val repository: LoaderScreenRepository) :
    MvpPresenter<LoaderScreenView>() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun onOptionsItemBackSelected() {
        viewState.finishActivity()
    }

    fun onViewCreated(bookInd: Int, path: String) {
        scope.launch {
            repository.initDataSources()
            val analyzedBookModel = repository.analyzeBook(path)
            analyzedBookModel?.let {
                repository.saveAnalysis(path, bookInd, analyzedBookModel)
                viewState.goToInfoActivity(bookInd)
            }
        }
    }

    fun onStop() {
        job.cancel()
    }
}