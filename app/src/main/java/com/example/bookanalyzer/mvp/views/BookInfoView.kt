package com.example.bookanalyzer.mvp.views

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.Skip

@AddToEnd
interface BookInfoView : MvpView {
    //fun finishActivity()
    fun setViewsText(path:String,
                     uniqWordCount:String,
                     allWordCount:String,
                     allCharsCount:String,
                     avgSentenceLenInWrd:String,
                     avgSentenceLenInChr: String,
                     avgWordLen:String)
    @Skip
    fun startWordListActivity(ind:Int)
}