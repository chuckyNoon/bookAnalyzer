package com.example.bookanalyzer.mvp.presenters

import android.os.Handler
import com.example.bookanalyzer.*
import com.example.bookanalyzer.common.BookSearch
import com.example.bookanalyzer.mvp.repositories.StartActivityRepository
import com.example.bookanalyzer.mvp.views.StartView
import moxy.MvpPresenter
import java.io.File
import kotlin.concurrent.thread

class StartActivityPresenter( private val repository: StartActivityRepository) : MvpPresenter<StartView>(){
    private var isListCreating = false
    private var books:ArrayList<MenuBookModel>? = null
    private var handler = Handler()

    fun onViewCreated(){
        isListCreating = true
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
            isListCreating = false
        }
    }

    private fun addBookToList(bookPath:String) {
        val view = viewState
        thread {
            val book = repository.getDetailedBookInfo(bookPath)
            handler.post {
                view.addBook(book)
            }
            repository.saveBookPath(bookPath)
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
            val view = viewState
            view.showSearchSettingsDialog()
        }
    }

    fun onOptionsItemSelected() {
        val view = viewState
        view.showSideMenu()
    }

    fun onActivityResult(bookPath:String) {
        addBookToList(bookPath)
    }

    fun onRestart() {
        val view = viewState
        thread {
            books?.let {
                for (book in it) {
                    if (book.wordCount != repository.getUniqueWordCount(book.path)) {
                        handler.post {
                            view.updateBook(repository.getDetailedBookInfo(book.path))
                        }
                        break
                    }
                }
            }
        }
    }
}