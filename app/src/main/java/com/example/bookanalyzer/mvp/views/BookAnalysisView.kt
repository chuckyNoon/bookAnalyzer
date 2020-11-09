package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.adapters.analysis_params_adapter.AbsAnalysisCell
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.Skip

@AddToEnd
interface BookAnalysisView : MvpView {
    fun finishActivity()
    fun setupCells(cells: ArrayList<AbsAnalysisCell>)

    @Skip
    fun startWordListActivity(analysisId: Int)
}