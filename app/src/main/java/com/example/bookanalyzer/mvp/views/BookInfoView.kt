package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.activities.BookInfoModel
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.Skip

@AddToEnd
interface BookInfoView : MvpView {
    fun finishActivity()
    fun setViewsText(bookInfoModel: BookInfoModel)

    @Skip
    fun startWordListActivity(analysisId: Int)
}