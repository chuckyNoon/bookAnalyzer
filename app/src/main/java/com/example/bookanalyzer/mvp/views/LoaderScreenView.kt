package com.example.bookanalyzer.mvp.views

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.Skip

@AddToEnd
interface LoaderScreenView : MvpView {
    fun finishActivity()
    fun goToAnalysisActivity(analysisId: Int)
}