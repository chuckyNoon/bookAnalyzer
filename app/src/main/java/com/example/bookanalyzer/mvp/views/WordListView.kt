package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.adapters.WordListElemModel

interface WordListView {
    fun finishActivity()
    fun scrollToPosition(position: Int)
    fun setPositionText(text:String)
    fun setSeekBarMaxValue(maxVal:Int)
    fun setupWordLines(linesList:ArrayList<WordListElemModel>)
}