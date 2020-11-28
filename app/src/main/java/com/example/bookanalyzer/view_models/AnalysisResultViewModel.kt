package com.example.bookanalyzer.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.domain.models.ShowedAnalysisEntity
import com.example.bookanalyzer.domain.repositories.BookAnalysisRepository
import com.example.bookanalyzer.ui.adapters.analysis_params_adapter.AbsAnalysisCell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

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
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val job = SupervisorJob()
    private var isFirstLaunch = true

    private lateinit var bookAnalysis: ShowedAnalysisEntity

    private val _cells = MutableLiveData<ArrayList<AbsAnalysisCell>>()
    private val _isFragmentFinishRequired = MutableLiveData<Boolean>()
    private val _wordListToShow = MutableLiveData<Int>()

    val cells: LiveData<ArrayList<AbsAnalysisCell>> = _cells
    val isFragmentFinishRequired: LiveData<Boolean> = _isFragmentFinishRequired
    val wordListToShow: LiveData<Int> = _wordListToShow

    fun onViewCreated(analysisId: Int) {
        if (!isFirstLaunch) {
            return
        }
        launch {
            bookAnalysis = repository.getAnalysis(analysisId)
            _cells.value = bookAnalysis.toParamCells()
            isFirstLaunch = true
        }
    }

    fun onOptionsItemBackSelected() {
        _isFragmentFinishRequired.value = true
        _isFragmentFinishRequired.value = false
    }

    fun onWordListButtonClicked(analysisId: Int) {
        _wordListToShow.value = analysisId
        _wordListToShow.value = null
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