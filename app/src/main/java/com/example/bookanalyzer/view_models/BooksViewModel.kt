package com.example.bookanalyzer.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.common.FilesSearch
import com.example.bookanalyzer.domain.models.BookEntity
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.ui.adapters.book_items_adapter.BookCell
import com.example.bookanalyzer.ui.fragments.ShowBookIntention
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

const val ANALYSIS_NOT_EXIST = -1

class BooksViewModelFactory(
    private val repository: StartScreenRepository,
    private val resourceManager: ResourceManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BooksViewModel(repository, resourceManager, dispatcher) as T
    }
}

class BooksViewModel(
    private val repository: StartScreenRepository,
    private val resourceManager: ResourceManager,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel(), CoroutineScope {

    companion object {
        private const val TIME_BEFORE_HIDING_LOADING_STATE_VIEW: Long = 3000
    }

    override val coroutineContext: CoroutineContext
        get() = job + dispatcher

    enum class ContentLoadingState {
        Inactive, Active, Finished
    }

    enum class SideMenuState {
        Hidden, Showed
    }

    private var bookEntities = ArrayList<BookEntity>()
    private val job = SupervisorJob()
    private var isBookListCreated = false

    private val _bookCells = MutableLiveData<ArrayList<BookCell>>()
    private val _contentLoadingState = MutableLiveData<ContentLoadingState>()
    private val _sideMenuState = MutableLiveData(SideMenuState.Hidden)
    private val _bookToAnalyze = MutableLiveData<String?>(null)
    private val _showBookIntent = MutableLiveData<ShowBookIntention>()

    val bookCells: LiveData<ArrayList<BookCell>> = _bookCells
    val contentLoadingState: LiveData<ContentLoadingState> = _contentLoadingState
    val sideMenuState: LiveData<SideMenuState> = _sideMenuState
    val bookToAnalyze: LiveData<String?> = _bookToAnalyze
    val showBookIntent: LiveData<ShowBookIntention> = _showBookIntent

    fun onStart() {
        launch {
            if (!isBookListCreated) {
                _contentLoadingState.value = ContentLoadingState.Active
                buildCompleteBookList()
                _contentLoadingState.value = ContentLoadingState.Finished
                delay(TIME_BEFORE_HIDING_LOADING_STATE_VIEW)
                _contentLoadingState.value = ContentLoadingState.Inactive
            } else {
                buildCompleteBookList()
            }
            isBookListCreated = true
        }
    }

    fun onStop() {
        launch {
            repository.saveCurrentBookList(bookEntities)
        }
    }

    fun onSearchSettingsSelected(bookFormats: ArrayList<String>, rootDir: File) {
        launch {
            val bookPaths = FilesSearch.findFiles(rootDir, bookFormats)
            _contentLoadingState.value = ContentLoadingState.Active
            buildInitialBookList(bookPaths)
            repository.insertDataFromPathsInDb(bookPaths)
            buildCompleteBookList()
            isBookListCreated = true
            _contentLoadingState.value = ContentLoadingState.Finished
            delay(TIME_BEFORE_HIDING_LOADING_STATE_VIEW)
            _contentLoadingState.value = ContentLoadingState.Inactive
        }
    }

    fun onOptionsMenuItemSelected() {
        _sideMenuState.value = SideMenuState.Showed
        _sideMenuState.value = SideMenuState.Hidden
    }

    fun onActivityResult(bookPath: String) {
        addBookItemToList(bookPath)
    }

    fun onBookDismiss(position: Int) {
        bookEntities.removeAt(position)
        _bookCells.value = convertBookEntitiesToCells(bookEntities)
    }

    fun onBookMove(fromPosition: Int, toPosition: Int) {
        /*val newList = copyList(items)
         val prev = newList.removeAt(fromPosition)
         newList.add(toPosition, prev)
         items = newList
         viewState.showList(newList)*/
    }

    fun onBookClicked(adapterPos: Int, yOffset: Float) {
        val bookEntity = bookEntities[adapterPos]
        val analysisId = bookEntity.analysisId
        if (isBookAnalyzed(analysisId)) {
            _showBookIntent.value =
                ShowBookIntention(bookEntity.toBookCell(), analysisId, yOffset)
            _showBookIntent.value = null
        } else {
            _bookToAnalyze.value = bookEntity.path
            _bookToAnalyze.value = null
        }
    }

    private suspend fun buildInitialBookList(bookPaths: ArrayList<String>) {
        bookEntities = repository.getInitialBookEntities(bookPaths)
        _bookCells.value = convertBookEntitiesToCells(bookEntities)
    }

    private suspend fun buildCompleteBookList() {
        bookEntities = repository.getCompleteBookEntities()
        _bookCells.value = convertBookEntitiesToCells(bookEntities)
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
        launch {
            repository.insertDataFromPathsInDb(arrayListOf(bookPath), toReplace = false)
            buildCompleteBookList()
        }
    }

    private fun isBookAnalyzed(analysisId: Int) = (analysisId != ANALYSIS_NOT_EXIST)
}