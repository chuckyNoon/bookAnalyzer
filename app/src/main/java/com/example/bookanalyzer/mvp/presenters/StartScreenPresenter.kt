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

class StartScreenPresenter(private val repository: StartScreenRepository) :
    MvpPresenter<StartView>() {

    private var bookDataList: ArrayList<BookData>? = null
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private var lastOpenedBookInd = -1

    fun onViewCreated() {
        scope.launch {
            repository.initDataSources()
            buildListFromSavedData()
        }
    }

    private suspend fun buildListFromSavedData() {
        indicateContentLoadingStart()
        buildCompleteBookList()
        indicateContentLoadingEnd()
    }

    private suspend fun buildListFromNewData(bookPaths: ArrayList<String>) {
        indicateContentLoadingStart()
        buildInitialBookList(bookPaths)
        repository.insertDataFromPathsInDb(bookPaths)
        buildCompleteBookList()
        indicateContentLoadingEnd()
    }

    private suspend fun buildInitialBookList(bookPaths: ArrayList<String>) {
        val initialDataList = repository.getInitialDataList(bookPaths)
        bookDataList = initialDataList
        val initialItemList = convertDataListToItemList(initialDataList)
        viewState.showBookList(initialItemList)
    }

    private suspend fun buildCompleteBookList() {
        val completeDataList = (repository.getCompleteDataList())
        bookDataList = completeDataList
        val completeItemList = convertDataListToItemList(completeDataList)
        viewState.showBookList(completeItemList)
    }

    private fun indicateContentLoadingStart() {
        viewState.showLoadingStateView()
        viewState.moveLoadingStateViewUp(300)
        viewState.setLoadingStateViewText("Loading content...")
    }

    private suspend fun indicateContentLoadingEnd() {
        viewState.updateLoadingStateView("Loading ended", 250, 300)
        delay(3000)
        viewState.moveLoadingStateViewDown(250)
        viewState.hideLoadingStateView()
    }

    private fun convertDataListToItemList(dataList: ArrayList<BookData>): ArrayList<BookListItem> {
        val itemList = ArrayList<BookListItem>()
        for (item in dataList) {
            itemList.add(item.toBookListItem())
        }
        return (itemList)
    }

    private fun BookData.toBookListItem(): BookListItem {
        val bookFormat = path.split(".").last().toUpperCase(Locale.ROOT)
        val relativePath = path.split("/").last()
        val title = title ?: relativePath
        val author = author ?: "Unknown"
        val uniqueWordCountText = makeWordCountText(uniqueWordCount)

        return BookListItem(
            path = relativePath,
            title = title,
            author = author,
            format = bookFormat,
            imgPath = imgPath,
            uniqueWordCount = uniqueWordCountText,
            barProgress = uniqueWordCount,
            id = id
        )
    }

    private fun makeWordCountText(uniqueWordCount: Int): String {
        val firstPart = if (uniqueWordCount != 0) {
            uniqueWordCount.toString()
        } else {
            "?"
        }
        return "$firstPart words"
    }

    private fun addBookItemToList(bookPath: String) {
        scope.launch {
            val book = repository.getCompleteBookData(bookPath)
            book?.let {
                bookDataList?.let { items ->
                    items.add(book)
                    viewState.showBookList(convertDataListToItemList(items))
                }
            }
        }
    }

    fun onSelectedSearchSettings(bookFormats: ArrayList<String>, rootDir: File) {
        scope.launch {
            val bookPaths = BookSearch.findBookPaths(rootDir, bookFormats)
            repository.initDataSources()
            buildListFromNewData(bookPaths)
        }
    }

    fun onOptionsMenuItemSelected() {
        viewState.showSideMenu()
    }

    fun onActivityResult(bookPath: String) {
        addBookItemToList(bookPath)
    }

    fun onBookDismiss(position: Int) {
        bookDataList?.let { bookDataList ->
            bookDataList.removeAt(position)
            viewState.showBookList(convertDataListToItemList(bookDataList))
        }
    }

    fun onBookMove(fromPosition: Int, toPosition: Int) {
        /* val newList = copyList(items)
         val prev = newList.removeAt(fromPosition)
         newList.add(toPosition, prev)
         items = newList
         viewState.showList(newList)*/
    }

    fun onBookClicked(position: Int) {
        if (lastOpenedBookInd != -1) {
            return
        }
        bookDataList?.let { bookDataList ->
            lastOpenedBookInd = position
            scope.launch {
                val book = bookDataList[position]
                val ind = repository.getBookIndByPath(book.path)
                if (ind != -1) {
                    viewState.startBookInfoActivity(ind)
                } else {
                    val newInd = repository.getAnalyzedBookCount() + 1
                    viewState.startLoaderScreenActivity(book.path, newInd)
                }
            }
        }
    }

    fun onRestart() {
        bookDataList?.let { bookDataList ->
            scope.launch {
                if (lastOpenedBookInd in bookDataList.indices) {
                    val newUniqueWordCount =
                        repository.getUniqueWordCountByPath(bookDataList[lastOpenedBookInd].path)
                    if (bookDataList[lastOpenedBookInd].uniqueWordCount != newUniqueWordCount) {
                        bookDataList[lastOpenedBookInd].uniqueWordCount = newUniqueWordCount
                        viewState.showBookList(convertDataListToItemList(bookDataList))
                    }
                }
                lastOpenedBookInd = -1
            }
        }
    }

    fun onStop() {
        bookDataList?.let { bookDataList ->
            scope.launch {
                repository.saveCurrentBookList(bookDataList)
            }
        }
    }
}

data class BookData(
    var path: String,
    var title: String?,
    var author: String?,
    var imgPath: String?,
    var uniqueWordCount: Int = 0,
    var id: Int = 0
)
