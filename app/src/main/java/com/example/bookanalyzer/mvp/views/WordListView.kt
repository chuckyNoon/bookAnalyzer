package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.adapters.WordListElemModel
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEnd
interface WordListView : MvpView{
    fun finishActivity()
    @AddToEndSingle
    fun scrollToPosition(position: Int)
    @AddToEndSingle
    fun setPositionText(text:String)
    fun setSeekBarMaxValue(maxVal:Int)
    fun setupWordLines(linesList:ArrayList<WordListElemModel>)
}