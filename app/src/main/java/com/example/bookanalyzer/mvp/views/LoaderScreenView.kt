package com.example.bookanalyzer.mvp.views

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd

@AddToEnd
interface LoaderScreenView : MvpView{
    fun finishActivity()
    fun goToInfoActivity(bookInd:Int)
}