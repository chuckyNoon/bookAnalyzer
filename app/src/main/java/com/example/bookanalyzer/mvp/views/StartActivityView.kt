package com.example.bookanalyzer.mvp.views

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.Skip

@AddToEnd
interface StartActivityView: MvpView {
    @Skip
    fun showSideMenu()
}