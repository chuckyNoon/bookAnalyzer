package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.domain.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import kotlinx.coroutines.*
import moxy.MvpPresenter

class LoaderScreenPresenter(private val repository: LoaderScreenRepository) :
    MvpPresenter<LoaderScreenView>() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private var firstLaunch = true

    fun onOptionsItemBackSelected() {
        viewState.finishActivity()
    }

    fun onViewCreated(bookPath: String) {
        if (!firstLaunch){
            return
        }
        scope.launch {
            val sourceAnalysisEntity = repository.analyzeBook(bookPath)
            sourceAnalysisEntity.let {
                repository.saveAnalysis(sourceAnalysisEntity)
                val analysisId = repository.getAnalysisIdByPath(bookPath)
                analysisId?.let {
                    viewState.goToAnalysisActivity(analysisId)
                }
                firstLaunch = false
            }
        }
    }

    fun onStop() {
        job.cancel()
    }
}