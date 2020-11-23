package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.adapters.word_list_adapter.WordCell
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface WordListView : MvpView {
    fun finishActivity()
    fun scrollToPosition(position: Int)
    fun setPositionViewText(text: String)
    fun setSeekBarMaxValue(maxValue: Int)
    fun setupCells(wordCells: ArrayList<WordCell>)
}