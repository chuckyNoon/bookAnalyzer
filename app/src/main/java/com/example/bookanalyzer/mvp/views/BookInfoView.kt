package com.example.bookanalyzer.mvp.views

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd

@AddToEnd
interface BookInfoView : MvpView {
    fun finishActivity()
    fun setViewsText(path:String,
                     uniqWordCount:String,
                     allWordCount:String,
                     allCharsCount:String,
                     avgSentenceLenInWrd:String,
                     avgSentenceLenInChr: String,
                     avgWordLen:String)
    fun startWordListActivity(ind:Int)
}