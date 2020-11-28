package com.example.bookanalyzer.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookanalyzer.domain.models.WordEntity
import com.example.bookanalyzer.domain.repositories.WordListRepository
import com.example.bookanalyzer.ui.adapters.word_list_adapter.WordCell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class WordsViewModelFactory(
    private val repository: WordListRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WordsViewModel(repository) as T
    }
}

data class BottomPanelState(
    var text: String = "",
    var isVisible: Boolean = false,
    var seekBarMaxValue: Int = 0
)

data class BottomPanelViewState(
    val text: String,
    val isVisible: Boolean,
    val seekBarMaxValue: Int
)

class WordsViewModel(private val repository: WordListRepository) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var wordEntities = ArrayList<WordEntity>()
    private var bottomPanelState = BottomPanelState()
    private var isFirstLaunch = true

    private val _wordCells = MutableLiveData<ArrayList<WordCell>>()
    private val _cursorPosition = MutableLiveData(0)
    private val _bottomPanelViewState = MutableLiveData<BottomPanelViewState>()
    private val _isFragmentFinishRequired = MutableLiveData(false)

    val wordCells: LiveData<ArrayList<WordCell>> = _wordCells
    val cursorPosition: LiveData<Int> = _cursorPosition
    val bottomPanelViewState: LiveData<BottomPanelViewState> = _bottomPanelViewState
    val isFragmentFinishRequired: LiveData<Boolean> = _isFragmentFinishRequired

    fun onProgressChanged(progress: Int) {
        _cursorPosition.value = Math.max(progress, 1)
         bottomPanelState.text = "${_cursorPosition.value} from ${wordEntities.size}"
        _bottomPanelViewState.value = bottomPanelState.toViewState()
    }

    fun onViewCreated(analysisId: Int) {
        if (!isFirstLaunch) {
            return
        }
        launch {
            wordEntities = repository.getWordEntities(analysisId) ?: return@launch
            _wordCells.value = wordEntities.toCells()

            val cellsCount = _wordCells.value?.size?:0
            bottomPanelState.seekBarMaxValue = cellsCount
            bottomPanelState.text = "1 from $cellsCount"
            _bottomPanelViewState.value = bottomPanelState.toViewState()
            isFirstLaunch = false
        }
    }

    fun onOptionsItemBackSelected() {
        _isFragmentFinishRequired.value = true
        _isFragmentFinishRequired.value = false
    }

    fun onWordClicked(){
        bottomPanelState.isVisible = !bottomPanelState.isVisible
        _bottomPanelViewState.value = bottomPanelState.toViewState()
    }

    private fun WordEntity.toCell() = WordCell(
        word,
        frequency.toString(),
        pos.toString()
    )

    private fun ArrayList<WordEntity>.toCells(): ArrayList<WordCell> {
        val cells = ArrayList<WordCell>()
        for (entity in this) {
            cells.add(entity.toCell())
        }
        return cells
    }

    private fun BottomPanelState.toViewState() = BottomPanelViewState(
        text,
        isVisible,
        seekBarMaxValue
    )
}