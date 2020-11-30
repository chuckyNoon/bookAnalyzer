package com.example.bookanalyzer.view_models

import androidx.lifecycle.*
import com.example.bookanalyzer.MyNavigation
import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.SingleEventLiveData
import com.example.bookanalyzer.common.FilesSearch
import com.example.bookanalyzer.domain.models.BookEntity
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.ui.adapters.book_items_adapter.BookCell
import com.example.bookanalyzer.ui.fragments.ResultFragmentExtra
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

const val ANALYSIS_NOT_EXIST = -1

class BooksViewModelFactory(
    private val repository: StartScreenRepository,
    private val resourceManager: ResourceManager
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BooksViewModel(repository, resourceManager) as T
    }
}

class BooksViewModel(
    private val repository: StartScreenRepository,
    private val resourceManager: ResourceManager
) : ViewModel() {

    companion object {
        private const val TIME_BEFORE_HIDING_LOADING_STATE_VIEW: Long = 3000
    }

    enum class ContentLoadingState {
        Hidden, Active, Finished
    }

    enum class SideMenuState {
        Hidden, Showed
    }

    private var bookEntities = ArrayList<BookEntity>()
    private var isBookListCreated = false

    private val _bookCells = MutableLiveData<ArrayList<BookCell>>()
    private val _contentLoadingState = MutableLiveData<ContentLoadingState>()
    private val _sideMenuState = SingleEventLiveData<SideMenuState>()
    private val _navigation = SingleEventLiveData<MyNavigation>()

    val bookCells: LiveData<ArrayList<BookCell>> = _bookCells
    val contentLoadingState: LiveData<ContentLoadingState> = _contentLoadingState
    val sideMenuState: LiveData<SideMenuState> = _sideMenuState
    val navigation: LiveData<MyNavigation> = _navigation

    fun onStart() {
        viewModelScope.launch {
            if (!isBookListCreated) {
                _contentLoadingState.value = ContentLoadingState.Active
                buildCompleteBookList()
                _contentLoadingState.value = ContentLoadingState.Finished
                delay(TIME_BEFORE_HIDING_LOADING_STATE_VIEW)
                _contentLoadingState.value = ContentLoadingState.Hidden
            } else {
                buildCompleteBookList()
            }
            isBookListCreated = true
        }
    }

    fun onSearchSettingsSelected(bookFormats: ArrayList<String>, rootDir: File) {
        viewModelScope.launch {
            val bookPaths = FilesSearch.findFiles(rootDir, bookFormats)
            _contentLoadingState.value = ContentLoadingState.Active
            buildInitialBookList(bookPaths)
            repository.insertDataFromPathsInDb(bookPaths)
            buildCompleteBookList()
            isBookListCreated = true
            _contentLoadingState.value = ContentLoadingState.Finished
            delay(TIME_BEFORE_HIDING_LOADING_STATE_VIEW)
            _contentLoadingState.value = ContentLoadingState.Hidden
        }
    }

    fun onOptionsMenuItemSelected() {
        _sideMenuState.value = SideMenuState.Showed
    }

    fun onActivityResult(bookPath: String) {
        addBookItemToList(bookPath)
    }

    fun onBookDismiss(position: Int) {
        bookEntities.removeAt(position)
        _bookCells.value = convertBookEntitiesToCells(bookEntities)
        viewModelScope.launch {
            repository.saveCurrentBookList(bookEntities)
        }
    }

    fun onBookClicked(adapterPos: Int, yOffset: Float) {
        val bookEntity = bookEntities[adapterPos]
        val analysisId = bookEntity.analysisId
        if (isBookAnalyzed(analysisId)) {
            val extra = ResultFragmentExtra(bookEntity.toBookCell(), analysisId, yOffset)
            _navigation.value = MyNavigation.ToResultFragment(extra)
        } else {
            _navigation.value = MyNavigation.ToProcessFragment(bookEntity.path)
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
        viewModelScope.launch {
            repository.insertDataFromPathsInDb(arrayListOf(bookPath), toReplace = false)
            buildCompleteBookList()
        }
    }

    private fun isBookAnalyzed(analysisId: Int) = (analysisId != ANALYSIS_NOT_EXIST)
}