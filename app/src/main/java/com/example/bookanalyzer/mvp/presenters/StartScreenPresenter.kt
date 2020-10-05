package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.*
import com.example.bookanalyzer.common.BookSearch
import com.example.bookanalyzer.mvp.repositories.StartScreenRepository
import com.example.bookanalyzer.mvp.views.StartView
import kotlinx.coroutines.*
import moxy.MvpPresenter
import java.io.File
import kotlin.collections.ArrayList

class StartScreenPresenter(private val repository: StartScreenRepository) : MvpPresenter<StartView>(){
    private lateinit var books:ArrayList<MenuBookModel>
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private var startedActivityInd = -1

    fun onViewCreated(){
        scope.launch {
            viewState.showLoadingStateView()
            val preList = repository.getPreviewList()
            viewState.showList(preList)
            viewState.moveLoadingStateViewUp(300)
            viewState.setLoadingStateViewText("Loading content...")

            var updatedList :ArrayList<MenuBookModel> = copyList(preList)
            for (i in preList.indices){
                val detailedBook = withContext(Dispatchers.Default){
                    repository.getDetailedBookInfo(preList[i].path)
                }
                updatedList[i].name = detailedBook.name
                updatedList[i].bitmap = detailedBook.bitmap
                updatedList[i].author = detailedBook.author
                updatedList[i].wordCount = detailedBook.wordCount

                viewState.showList(updatedList)
                updatedList = copyList(updatedList)
            }
            books = updatedList
            viewState.updateLoadingStateView("Loading ended", 250, 300)
            delay(3000)
            viewState.moveLoadingStateViewDown(250)
            viewState.hideLoadingStateView()
        }
    }

    private fun copyItem(model: MenuBookModel):MenuBookModel{
        return MenuBookModel(model.path,model.name,model.author,model.bitmap,model.wordCount)
    }

    private fun copyList(ar:ArrayList<MenuBookModel>) : ArrayList<MenuBookModel>{
        val newList = ArrayList<MenuBookModel>()
        for (item in ar){
            newList.add(copyItem(item))
        }
        return (newList)
    }


    private fun addBookToList(bookPath:String) {
        scope.launch {
            repository.saveBookPath(bookPath)
            val book = repository.getDetailedBookInfo(bookPath)
            books = copyList(books)
            books.add(book)
            viewState.showList(books)
        }
    }

    fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File) {
        scope.launch {
            repository.saveAllBookPaths(BookSearch.findAll(dir, formats))
            onViewCreated()
        }
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

    fun onBookDismiss(position: Int){
        books = copyList(books)
        books.removeAt(position)
        viewState.showList(books)
    }

    fun onBookMove(fromPosition: Int, toPosition: Int){
        books = copyList(books)
        val prev = books.removeAt(fromPosition)
        books.add(toPosition, prev)
        viewState.showList(books)
    }

    fun onBookClicked(position:Int){
        if (startedActivityInd == -1){
            startedActivityInd = position
            scope.launch {
                val book = books[position]
                val ind = repository.getBookIndByPath(book.path)
                if (ind != -1){
                    viewState.startInfoActivity(ind)
                }else{
                    val newInd = repository.getAnalyzedBookCount()
                    viewState.startLoadingActivity(book.path, newInd)
                }
            }
        }

    }

    fun onRestart() {
        scope.launch {
            println(startedActivityInd)
            if (startedActivityInd >= 0){
                val wordCount = repository.getUniqueWordCount(books[startedActivityInd].path)
                if (books[startedActivityInd].wordCount != wordCount){
                    books = copyList(books)
                    books[startedActivityInd].wordCount = wordCount
                    viewState.showList(books)
                }
            }
            startedActivityInd = -1
        }
    }

    fun onStop() {
        scope.launch{
            val paths = ArrayList<String>()
            for (book in books){
                paths.add(book.path)
            }
            repository.saveAllBookPaths(paths)
        }
    }

}