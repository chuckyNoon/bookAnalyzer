package com.example.bookanalyzer.mvp.presenters

import android.os.Handler
import com.example.bookanalyzer.*
import com.example.bookanalyzer.common.BookSearch
import com.example.bookanalyzer.mvp.repositories.StartActivityRepository
import com.example.bookanalyzer.mvp.views.StartView
import java.io.File
import kotlin.concurrent.thread

class StartActivityPresenter(private val view: StartView, private val repository: StartActivityRepository){
    private var isListCreating = false
    private var books:ArrayList<MenuBookModel>? = null
    private var handler = Handler()

    fun onViewCreated(){
        isListCreating = true
        view.showLoadingStateView()

        thread {
            books = repository.getPreviewList()
            handler.post {
                view.setupBooks(books!!)
                view.moveLoadingStateViewUp(300)
                view.setLoadingStateViewText("Loading content...")
            }

            for (book in books!!){
                val detailedBook = repository.getDetailedBookInfo(book.path)
                handler.post {
                    view.updateBook(detailedBook)
                }
            }
            handler.post {
                view.updateLoadingStateView("Loading ended", 250, 300)
            }
            handler.postDelayed({
                view.moveLoadingStateViewDown(250)
                view.hideLoadingStateView()
            },4000)
            isListCreating = false
        }
    }

    private fun addBookToList(bookPath:String) {
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
            view.showSearchSettingsDialog()
        }
    }

    fun onOptionsItemSelected() {
        view.showSideMenu()
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
                            view.updateBook(repository.getDetailedBookInfo(book.path))
                        }
                        break
                    }
                }
            }
        }
    }
}