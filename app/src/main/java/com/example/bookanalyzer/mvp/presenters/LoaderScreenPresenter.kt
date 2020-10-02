package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import kotlinx.coroutines.*
import moxy.MvpPresenter
import java.io.FileInputStream

class LoaderScreenPresenter(private val repository: LoaderScreenRepository) :MvpPresenter<LoaderScreenView>(){
    private lateinit var job: Job
    private val scope = MainScope()

    fun onOptionsItemSelected(){
        viewState.finishActivity()
    }

    fun onViewCreated(bookInd:Int, inStream:FileInputStream, path:String){
        job = scope.launch(Dispatchers.IO){
            val info = repository.analyzeBook(inStream, path)
            yield()

            repository.saveAnalysis(path, bookInd,  info, false)
            viewState.goToInfoActivity(bookInd)
        }
    }

    fun onStop(){
        job.cancel()
    }
}