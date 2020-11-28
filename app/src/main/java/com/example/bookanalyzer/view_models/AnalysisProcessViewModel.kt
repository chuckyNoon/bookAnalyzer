package com.example.bookanalyzer.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookanalyzer.domain.repositories.LoaderScreenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AnalysisProcessViewModelFactory(
    private val repository: LoaderScreenRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AnalysisProcessViewModel(repository) as T
    }
}

class AnalysisProcessViewModel(private val repository: LoaderScreenRepository) : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private var firstLaunch = true

    private val _analysisToShow = MutableLiveData<Int>()
    private val _isFragmentFinishRequired = MutableLiveData<Boolean>()

    val analysisToShow : LiveData<Int> = _analysisToShow
    val isFragmentFinishRequired : LiveData<Boolean> = _isFragmentFinishRequired

    fun onOptionsItemBackSelected() {
        _isFragmentFinishRequired.value = true
        _isFragmentFinishRequired.value = false
    }

    fun onViewCreated(bookPath: String) {
        if (!firstLaunch) {
            return
        }
        firstLaunch = false
        scope.launch {
            val sourceAnalysisEntity = repository.analyzeBook(bookPath)
            repository.saveAnalysis(sourceAnalysisEntity)
            val analysisId = repository.getAnalysisIdByPath(bookPath)
            analysisId?.let {
                _analysisToShow.value = analysisId
                _analysisToShow.value = null
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}