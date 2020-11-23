package com.example.bookanalyzer.mvp.presenters

import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.domain.models.ShowedAnalysisEntity
import com.example.bookanalyzer.domain.repositories.BookAnalysisRepository
import com.example.bookanalyzer.mvp.views.BookAnalysisView
import com.example.bookanalyzer.ui.adapters.analysis_params_adapter.AbsAnalysisCell
import kotlinx.coroutines.*
import moxy.MvpPresenter
import kotlin.coroutines.CoroutineContext


class BookAnalysisPresenter(
    private val repository: BookAnalysisRepository,
    private val resourceManager: ResourceManager
) :
    MvpPresenter<BookAnalysisView>(),
    CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val job = SupervisorJob()
    private var isFirstLaunch = true

    fun onViewCreated(analysisId: Int) {
        if (!isFirstLaunch) {
            return
        }
        launch {
            val bookAnalysis = repository.getAnalysis(analysisId)
            viewState.setupCells(bookAnalysis.toParamCells())
            isFirstLaunch = true
        }
    }

    fun onOptionsItemBackSelected() {
        viewState.finishActivity()
    }

    fun onWordListButtonClicked(analysisId: Int) {
        viewState.startWordListActivity(analysisId)
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