package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import kotlinx.coroutines.*
import moxy.MvpPresenter

class LoaderScreenPresenter(private val repository: LoaderScreenRepository) :MvpPresenter<LoaderScreenView>(){
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun onOptionsItemSelected(){
        viewState.finishActivity()
    }

    fun onViewCreated(bookInd:Int, path:String){

        scope.launch{
            val info = repository.analyzeBook(path)
            yield()

            repository.saveAnalysis(path, bookInd,  info)
            viewState.goToInfoActivity(bookInd)
        }
    }

    fun onStop(){
        job.cancel()
    }
}