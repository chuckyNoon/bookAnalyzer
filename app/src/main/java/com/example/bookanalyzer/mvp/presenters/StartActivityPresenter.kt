package com.example.bookanalyzer.mvp.presenters

import android.os.Handler
import com.example.bookanalyzer.*
import com.example.bookanalyzer.common.BookSearch
import com.example.bookanalyzer.mvp.repositories.StartActivityRepository
import com.example.bookanalyzer.mvp.views.StartView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import moxy.MvpPresenter
import java.io.File
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class StartActivityPresenter( private val repository: StartActivityRepository) : MvpPresenter<StartView>(){
    private var books:ArrayList<MenuBookModel>? = null
    private val handler = Handler()

    fun onViewCreated(){
        viewState.showLoadingStateView()
        thread {
            books = repository.getPreviewList()

            handler.post {
                viewState.setupBooks(books!!)
                viewState.moveLoadingStateViewUp(300)
                viewState.setLoadingStateViewText("Loading content...")
            }

            for (book in books!!){
                val detailedBook = repository.getDetailedBookInfo(book.path)
                handler.post {
                    viewState.updateBook(detailedBook)
                }
            }
            handler.post {
                viewState.updateLoadingStateView("Loading ended", 250, 300)
            }
            handler.postDelayed({
                 viewState.moveLoadingStateViewDown(250)
                 viewState.hideLoadingStateView()
             },4000)
        }
    }

    private fun addBookToList(bookPath:String) {
        Observable.fromCallable {
            repository.saveBookPath(bookPath)
            val book = repository.getDetailedBookInfo(bookPath)
            (book)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribe {
                viewState.addBook(it)
            }

    }

    fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File) {
        repository.saveAllBookPaths(BookSearch.findAll(dir, formats))
        onViewCreated()
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            viewState.showSearchSettingsDialog()
        }
    }

    fun onOptionsItemSelected() {
        viewState.showSideMenu()
    }

    fun onActivityResult(bookPath:String) {
        addBookToList(bookPath)
    }

    fun onRestart() {
        thread {
            books?.let {
                for (book in it) {
                    if (book.wordCount != repository.getUniqueWordCount(book.path)) {
                        handler.post {
                            viewState.updateBook(repository.getDetailedBookInfo(book.path))
                        }
                        break
                    }
                }
            }
        }
    }

}