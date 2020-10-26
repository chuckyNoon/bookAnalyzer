package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.common.FilesSearch
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.domain.models.BookPreviewEntity
import com.example.bookanalyzer.mvp.views.StartView
import com.example.bookanalyzer.ui.adapters.BookItem
import kotlinx.coroutines.*
import moxy.MvpPresenter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

const val ANALYSIS_NOT_EXIST = -1

class StartScreenPresenter(private val repository: StartScreenRepository) :
    MvpPresenter<StartView>() {

    companion object {
        private const val LOADING_CONTENT_TEXT = "Loading content..."
        private const val LOADING_ENDED_TEXT = "Loading ended"
        private const val UNKNOWN_AUTHOR_TEXT = "Unknown"
        private const val TIME_BEFORE_HIDING_LOADING_STATE_VIEW: Long = 3000
        private const val NO_BOOK_OPENED = -1
    }

    private var bookPreviewEntityList: ArrayList<BookPreviewEntity>? = null
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private var lastOpenedBookInd = NO_BOOK_OPENED

    fun onViewCreated() {
        scope.launch {
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
        bookPreviewEntityList = initialDataList
        val initialItemList = convertDataListToItemList(initialDataList)
        viewState.showBookList(initialItemList)
    }

    private suspend fun buildCompleteBookList() {
        val completeDataList = (repository.getCompleteDataList())
        bookPreviewEntityList = completeDataList
        val completeItemList = convertDataListToItemList(completeDataList)
        viewState.showBookList(completeItemList)
    }

    private fun indicateContentLoadingStart() {
        viewState.showLoadingStateView()
        viewState.moveLoadingStateViewUp(300)
        viewState.setLoadingStateViewText(LOADING_CONTENT_TEXT)
    }

    private suspend fun indicateContentLoadingEnd() {
        viewState.updateLoadingStateView(LOADING_ENDED_TEXT, 250, 300)
        delay(TIME_BEFORE_HIDING_LOADING_STATE_VIEW)
        viewState.moveLoadingStateViewDown(250)
        viewState.hideLoadingStateView()
    }

    private fun convertDataListToItemList(previewEntityList: ArrayList<BookPreviewEntity>): ArrayList<BookItem> {
        return ArrayList<BookItem>().apply {
            for (data in previewEntityList) {
                add(data.toBookListItem())
            }
        }
    }

    private fun BookPreviewEntity.toBookListItem(): BookItem {
        val bookFormat = path.split(".").last().toUpperCase(Locale.ROOT)
        val relativePath = path.split("/").last()
        val title = title ?: relativePath
        val author = author ?: UNKNOWN_AUTHOR_TEXT
        val uniqueWordCountText = makeWordCountText(uniqueWordCount)
        return BookItem(
            path = relativePath,
            title = title,
            author = author,
            format = bookFormat,
            imgPath = imgPath,
            uniqueWordCount = uniqueWordCountText,
            barProgress = uniqueWordCount,
            analysisId = analysisId
        )
    }

    private fun makeWordCountText(uniqueWordCount: Int): String {
        val firstPart = if (uniqueWordCount != 0) {
            (uniqueWordCount.toString())
        } else {
            ("?")
        }
        return "$firstPart words"
    }

    private fun addBookItemToList(bookPath: String) {
        //to fix
    }

    fun onSelectedSearchSettings(bookFormats: ArrayList<String>, rootDir: File) {
        scope.launch {
            val bookPaths = FilesSearch.findFiles(rootDir, bookFormats)
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
        bookPreviewEntityList?.let { bookDataList ->
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
        if (lastOpenedBookInd != NO_BOOK_OPENED) {
            return
        }
        bookPreviewEntityList?.let { bookDataList ->
            lastOpenedBookInd = position
            val book = bookDataList[position]
            val analysisId = book.analysisId
            if (isBookAnalyzed(analysisId)) {
                viewState.startBookInfoActivity(analysisId)
            } else {
                viewState.startLoaderScreenActivity(book.path)
            }
        }
    }

    private fun isBookAnalyzed(analysisId: Int) = (analysisId != ANALYSIS_NOT_EXIST)

    fun onRestart() {
        bookPreviewEntityList?.let { bookDataList ->
            if (lastOpenedBookInd in bookDataList.indices) {
                scope.launch {
                    val newUniqueWordCount =
                        repository.getUniqueWordCountByPath(bookDataList[lastOpenedBookInd].path)
                    val newId =
                        repository.getAnalysisIdByPath(bookDataList[lastOpenedBookInd].path)
                    if (bookDataList[lastOpenedBookInd].analysisId != newId) {
                        bookDataList[lastOpenedBookInd].uniqueWordCount = newUniqueWordCount
                        bookDataList[lastOpenedBookInd].analysisId = newId
                        viewState.showBookList(convertDataListToItemList(bookDataList))
                    }
                    lastOpenedBookInd = NO_BOOK_OPENED
                }
            }
        }
    }

    fun onStop() {
        bookPreviewEntityList?.let { bookDataList ->
            scope.launch {
                repository.saveCurrentBookList(bookDataList)
            }
        }
    }
}

