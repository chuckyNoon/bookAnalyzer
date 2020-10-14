package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.common.BookSearch
import com.example.bookanalyzer.mvp.repositories.StartScreenRepository
import com.example.bookanalyzer.mvp.views.StartView
import com.example.bookanalyzer.ui.adapters.BookListItem
import kotlinx.coroutines.*
import moxy.MvpPresenter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class StartScreenPresenter(private val repository: StartScreenRepository) : MvpPresenter<StartView>(){
    private var items:ArrayList<BookDisplayData>? = null
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private var startedActivityInd = -1

    fun onViewCreated(){
        buildList(null)
    }

    private fun buildList(paths:ArrayList<String>?){
        scope.launch {
            viewState.showLoadingStateView()
            viewState.moveLoadingStateViewUp(300)
            viewState.setLoadingStateViewText("Loading content...")

            if (paths != null){
                val simpleDataList = repository.getSimplePreviewList(paths)
                items = simpleDataList
                viewState.showList(getShowedList(simpleDataList))
            }
            val completeDataList = (repository.getCompletePreviewList(paths))
            items = completeDataList
            viewState.showList(getShowedList(completeDataList))

            viewState.updateLoadingStateView("Loading ended", 250, 300)
            delay(3000)
            viewState.moveLoadingStateViewDown(250)
            viewState.hideLoadingStateView()
        }
    }

    private fun getShowedList(ar:ArrayList<BookDisplayData>) : ArrayList<BookListItem>{
        val showedList = ArrayList<BookListItem>()
        for (item in ar){
            showedList.add(item.toBookListItem())
        }
        return (showedList)
    }

    private fun BookDisplayData.toBookListItem() : BookListItem{
        val format = path.split(".").last().toUpperCase(Locale.ROOT)
        val relPath = path.split("/").last()
        val title = if (title != null) title else relPath
        val author = if (!author.isNullOrEmpty()) author else "Unknown"
        val wordCountText = (if (wordCount != 0) wordCount.toString() else "?") + " words"
        return BookListItem(relPath, title!!, author!!, format, imgPath, wordCountText, wordCount, id)
    }

    private fun addBookToList(bookPath:String) {
        scope.launch {
            val book = repository.getDetailedBookInfo(bookPath)
            items?.let{items->
                items.add(book)
                viewState.showList(getShowedList(items))
            }
        }
    }

    fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File) {
        scope.launch {
            val paths = BookSearch.findAll(dir, formats)
            buildList(paths)
        }
    }

    fun onOptionsItemSelected() {
        viewState.showSideMenu()
    }

    fun onActivityResult(bookPath:String) {
        addBookToList(bookPath)
    }

    fun onBookDismiss(position: Int){
        items?.let {items->
            items.removeAt(position)
            viewState.showList(getShowedList(items))
        }
    }

    fun onBookMove(fromPosition: Int, toPosition: Int){
       /* val newList = copyList(items)
        val prev = newList.removeAt(fromPosition)
        newList.add(toPosition, prev)
        items = newList
        viewState.showList(newList)*/
    }

    fun onBookClicked(position:Int){
        if (startedActivityInd != -1) {
            return
        }
        items?.let{items->
            startedActivityInd = position
            scope.launch {
                val book = items[position]
                val ind = repository.getBookIndByPath(book.path)
                if (ind != -1){
                    viewState.startInfoActivity(ind)
                }else{
                    val newInd = repository.getAnalyzedBookCount() + 1
                    viewState.startLoadingActivity(book.path, newInd)
                }
            }
        }
    }

    fun onRestart() {
        items?.let{items->
            scope.launch {
                if (startedActivityInd in items.indices){
                    val wordCount = repository.getUniqueWordCount(items[startedActivityInd].path)
                    if (items[startedActivityInd].wordCount != wordCount){
                        items[startedActivityInd].wordCount = wordCount
                        viewState.showList(getShowedList(items))
                    }
                }
                startedActivityInd = -1
            }
        }
    }

    fun onStop() {
        items?.let{items->
            scope.launch{
                repository.saveCurrentMenu(items)
            }
        }
    }
}

data class BookDisplayData(var path:String,
                           var title:String?,
                           var author:String?,
                           var imgPath: String?,
                           var wordCount:Int = 0,
                           var id:Int = 0){

}
