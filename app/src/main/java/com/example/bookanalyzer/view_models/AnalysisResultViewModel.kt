package com.example.bookanalyzer.view_models

import androidx.lifecycle.*
import com.example.bookanalyzer.MyNavigation
import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.SingleEventLiveData
import com.example.bookanalyzer.domain.models.ShowedAnalysisEntity
import com.example.bookanalyzer.domain.repositories.BookAnalysisRepository
import com.example.bookanalyzer.ui.adapters.analysis_params_adapter.AbsAnalysisCell
import kotlinx.coroutines.launch

class AnalysisResultViewModelFactory(
    private val repository: BookAnalysisRepository,
    private val resourceManager: ResourceManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AnalysisResultViewModel(repository, resourceManager) as T
    }
}

class AnalysisResultViewModel(
    private val repository: BookAnalysisRepository,
    private val resourceManager: ResourceManager
) : ViewModel() {

    private var isFirstLaunch = true

    private lateinit var bookAnalysis: ShowedAnalysisEntity

    private val _cells = MutableLiveData<ArrayList<AbsAnalysisCell>>()
    private val _uniqueWordCount = MutableLiveData<Int>()
    private val _navigation = SingleEventLiveData<MyNavigation>()

    val cells: LiveData<ArrayList<AbsAnalysisCell>> = _cells
    val uniqueWordCount: LiveData<Int> = _uniqueWordCount
    val navigation: LiveData<MyNavigation> = _navigation

    fun onViewCreated(analysisId: Int) {
        if (!isFirstLaunch) {
            return
        }
        viewModelScope.launch {
            bookAnalysis = repository.getAnalysis(analysisId)
            _uniqueWordCount.value = bookAnalysis.uniqueWordCount
            _cells.value = bookAnalysis.toParamCells()
            isFirstLaunch = true
        }
    }

    fun onOptionsItemBackSelected() {
        _navigation.value = MyNavigation.Exit()
    }

    fun onWordListButtonClicked(analysisId: Int) {
        _navigation.value = MyNavigation.ToWordsFragment(analysisId)
    }

    private fun ShowedAnalysisEntity.toParamCells(): ArrayList<AbsAnalysisCell> {
        val cells = ArrayList<AbsAnalysisCell>()
        cells.add(
            AbsAnalysisCell.Parameter(
                resourceManager.getString(R.string.file_name),
                path.split("/").last()
            )
        )
        cells.add(
            AbsAnalysisCell.Parameter(
                resourceManager.getString(R.string.unique_word_count),
                uniqueWordCount.toString()
            )
        )
        cells.add(
            AbsAnalysisCell.Parameter(
                resourceManager.getString(R.string.word_count),
                allWordCount.toString()
            )
        )
        cells.add(
            AbsAnalysisCell.Parameter(
                resourceManager.getString(R.string.character_count),
                allCharsCount.toString()
            )
        )
        cells.add(
            AbsAnalysisCell.Parameter(
                resourceManager.getString(R.string.average_sentence_length_wrd),
                avgSentenceLenInWrd.toString()
            )
        )
        cells.add(
            AbsAnalysisCell.Parameter(
                resourceManager.getString(R.string.average_sentence_length_chr),
                avgSentenceLenInChr.toString()
            )
        )
        cells.add(
            AbsAnalysisCell.Parameter(
                resourceManager.getString(R.string.average_word_length),
                avgWordLen.toString()
            )
        )
        cells.add(
            AbsAnalysisCell.WordListButton(
                resourceManager.getString(R.string.list_button_text)
            )
        )
        return cells
    }
}