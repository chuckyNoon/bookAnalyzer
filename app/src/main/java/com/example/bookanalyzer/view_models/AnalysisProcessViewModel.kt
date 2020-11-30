package com.example.bookanalyzer.view_models

import androidx.lifecycle.*
import com.example.bookanalyzer.MyNavigation
import com.example.bookanalyzer.SingleEventLiveData
import com.example.bookanalyzer.domain.repositories.LoaderScreenRepository
import com.example.bookanalyzer.ui.fragments.ProcessFragmentExtra
import com.example.bookanalyzer.ui.fragments.ResultFragmentExtra
import kotlinx.coroutines.launch

class AnalysisProcessViewModelFactory(
    private val repository: LoaderScreenRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AnalysisProcessViewModel(repository) as T
    }
}

class AnalysisProcessViewModel(private val repository: LoaderScreenRepository) : ViewModel() {

    private var firstLaunch = true

    private val _navigation = SingleEventLiveData<MyNavigation>()

    val navigation : LiveData<MyNavigation> = _navigation

    fun onOptionsItemBackSelected() {
        _navigation.value = MyNavigation.Exit()
    }

    fun onViewCreated(extra: ProcessFragmentExtra) {
        if (!firstLaunch) {
            return
        }
        viewModelScope.launch {
            val bookPath = extra.bookPath
            val sourceAnalysisEntity = repository.analyzeBook(bookPath)
            repository.saveAnalysis(sourceAnalysisEntity)
            val analysisId = repository.getAnalysisIdByPath(bookPath)
            analysisId?.let {
                _navigation.value = MyNavigation.ToResultFragment(ResultFragmentExtra(extra.cell, it))
                firstLaunch = false
            }
        }
    }
}