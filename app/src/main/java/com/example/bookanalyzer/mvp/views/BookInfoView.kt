package com.example.bookanalyzer.mvp.views

interface BookInfoView {
    fun finishActivity()
    fun setViewsText(path:String,
                     uniqWordCount:String,
                     allWordCount:String,
                     allCharsCount:String,
                     avgSentenceLenInWrd:String,
                     avgSentenceLenInChr: String,
                     avgWordLen:String)
}