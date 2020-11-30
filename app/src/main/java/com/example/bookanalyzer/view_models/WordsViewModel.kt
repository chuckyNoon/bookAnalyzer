package com.example.bookanalyzer.view_models

import androidx.lifecycle.*
import com.example.bookanalyzer.MyNavigation
import com.example.bookanalyzer.SingleEventLiveData
import com.example.bookanalyzer.domain.models.WordEntity
import com.example.bookanalyzer.domain.repositories.WordListRepository
import com.example.bookanalyzer.ui.adapters.word_list_adapter.WordCell
import kotlinx.coroutines.launch

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

class WordsViewModel(private val repository: WordListRepository) : ViewModel(){

    private var wordEntities = ArrayList<WordEntity>()
    private var bottomPanelState = BottomPanelState()
    private var isFirstLaunch = true

    private val _wordCells = MutableLiveData<ArrayList<WordCell>>()
    private val _cursorPosition = MutableLiveData(0)
    private val _bottomPanelViewState = MutableLiveData<BottomPanelViewState>()
    private val _navigation = SingleEventLiveData<MyNavigation>()

    val wordCells: LiveData<ArrayList<WordCell>> = _wordCells
    val cursorPosition: LiveData<Int> = _cursorPosition
    val bottomPanelViewState: LiveData<BottomPanelViewState> = _bottomPanelViewState
    val navigation: LiveData<MyNavigation> = _navigation

    fun onProgressChanged(progress: Int) {
        _cursorPosition.value = progress.coerceAtLeast(1)
         bottomPanelState.text = "${_cursorPosition.value} from ${wordEntities.size}"
        _bottomPanelViewState.value = bottomPanelState.toViewState()
    }

    fun onViewCreated(analysisId: Int) {
        if (!isFirstLaunch) {
            return
        }
        viewModelScope.launch {
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
        _navigation.value = MyNavigation.Exit()
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