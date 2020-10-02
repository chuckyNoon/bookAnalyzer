package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.adapters.WordListElemModel
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd

@AddToEnd
interface WordListView : MvpView{
    fun finishActivity()
    fun scrollToPosition(position: Int)
    fun setPositionText(text:String)
    fun setSeekBarMaxValue(maxVal:Int)
    fun setupWordLines(linesList:ArrayList<WordListElemModel>)
}