package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import kotlinx.coroutines.*
import java.io.FileInputStream

class LoaderScreenPresenter(private val view:LoaderScreenView, private val repository: LoaderScreenRepository) {
    private lateinit var job: Job
    private val scope = MainScope()

    fun onOptionsItemSelected(){
        view.finishActivity()
    }

    fun onViewCreated(bookInd:Int, inStream:FileInputStream, path:String){
        job = scope.launch(Dispatchers.IO){
            val info = repository.analyzeBook(inStream, path)
            yield()

            repository.saveAnalysis(path, bookInd,  info, false)
            view.goToInfoActivity(bookInd)
        }
    }

    fun onStop(){
        job.cancel()
    }
}