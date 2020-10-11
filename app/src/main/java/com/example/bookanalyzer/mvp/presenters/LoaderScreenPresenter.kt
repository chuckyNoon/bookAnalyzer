package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.mvp.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import kotlinx.coroutines.*
import moxy.MvpPresenter
import java.io.FileInputStream

class LoaderScreenPresenter() :MvpPresenter<LoaderScreenView>(){
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var repository: LoaderScreenRepository


    fun setRep(rep:LoaderScreenRepository){
        repository = rep
    }

    fun onOptionsItemSelected(){
        viewState.finishActivity()
    }

    fun onViewCreated(bookInd:Int, inStream:FileInputStream, path:String){
        scope.launch{
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