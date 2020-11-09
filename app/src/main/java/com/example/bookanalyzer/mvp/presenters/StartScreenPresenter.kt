package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.common.FilesSearch
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.domain.models.BookEntity
import com.example.bookanalyzer.mvp.views.StartScreenView
import com.example.bookanalyzer.ui.adapters.book_items_adapter.BookCell
import kotlinx.coroutines.*
import moxy.MvpPresenter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

const val ANALYSIS_NOT_EXIST = -1

class StartScreenPresenter(
    private val repository: StartScreenRepository,
    private val resourceManager: ResourceManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) :
    MvpPresenter<StartScreenView>(), CoroutineScope {

    companion object {
        private const val TIME_BEFORE_HIDING_LOADING_STATE_VIEW: Long = 3000
        private const val NO_BOOK_OPENED = -1
    }

    override val coroutineContext: CoroutineContext
        get() = job + dispatcher

    private var bookEntities = ArrayList<BookEntity>()
    private val job = SupervisorJob()
    private var lastOpenedBookInd = NO_BOOK_OPENED

    fun onViewCreated() {
        launch {
            buildListFromSavedData()
        }
    }

    fun onSelectedSearchSettings(bookFormats: ArrayList<String>, rootDir: File) {
        launch {
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
        bookEntities.removeAt(position)
        viewState.setupCells(convertBookEntitiesToCells(bookEntities))
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
        lastOpenedBookInd = position
        val book = bookEntities[position]
        val analysisId = book.analysisId
        if (isBookAnalyzed(analysisId)) {
            viewState.startBookInfoActivity(analysisId)
        } else {
            viewState.startLoaderScreenActivity(book.path)
        }
    }

    fun onRestart() {
        if (lastOpenedBookInd in bookEntities.indices) {
            val entity = bookEntities[lastOpenedBookInd]
            launch {
                val newUniqueWordCount =
                    repository.getUniqueWordCountByPath(entity.path)
                val newAnalysisId =
                    repository.getAnalysisIdByPath(entity.path)
                if (entity.analysisId != newAnalysisId) {
                    entity.uniqueWordCount = newUniqueWordCount
                    entity.analysisId = newAnalysisId
                    viewState.setupCells(convertBookEntitiesToCells(bookEntities))
                }
                lastOpenedBookInd = NO_BOOK_OPENED
            }
        }
    }

    fun onStop() {
        launch {
            repository.saveCurrentBookList(bookEntities)
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

    private fun indicateContentLoadingStart() {
        viewState.showLoadingStateView()
        viewState.moveLoadingStateViewUp(300)
        viewState.setLoadingStateViewText(R.string.loading_content_started)
    }

    private suspend fun indicateContentLoadingEnd() {
        viewState.updateLoadingStateView(R.string.loading_content_ended, 250, 300)
        delay(TIME_BEFORE_HIDING_LOADING_STATE_VIEW)
        viewState.moveLoadingStateViewDown(250)
        viewState.hideLoadingStateView()
    }

    private suspend fun buildInitialBookList(bookPaths: ArrayList<String>) {
        bookEntities = repository.getInitialBookEntities(bookPaths)
        viewState.setupCells(convertBookEntitiesToCells(bookEntities))
    }

    private suspend fun buildCompleteBookList() {
        bookEntities = repository.getCompleteBookEntities()
        viewState.setupCells(convertBookEntitiesToCells(bookEntities))
    }

    private fun convertBookEntitiesToCells(entities: ArrayList<BookEntity>): ArrayList<BookCell> {
        return ArrayList<BookCell>().apply {
            for (entity in entities) {
                add(entity.toBookCell())
            }
        }
    }

    private fun BookEntity.toBookCell(): BookCell {
        val bookFormat = path.split(".").last().toUpperCase(Locale.ROOT)
        val relativePath = path.split("/").last()
        val title = title ?: relativePath
        val author = author ?: resourceManager.getString(R.string.unknown_author)
        val uniqueWordCountText = makeWordCountText(uniqueWordCount)
        return BookCell(
            filePath = relativePath,
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
        val wordCount = if (uniqueWordCount != 0) {
            (uniqueWordCount.toString())
        } else {
            ("?")
        }
        return "$wordCount words"
    }

    private fun addBookItemToList(bookPath: String) {
        //to fix
    }

    private fun isBookAnalyzed(analysisId: Int) = (analysisId != ANALYSIS_NOT_EXIST)
}

