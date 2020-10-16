package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.adapters.WordListItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEnd
interface WordListView : MvpView {
    fun finishActivity()

    @AddToEndSingle
    fun scrollToPosition(position: Int)

    @AddToEndSingle
    fun setPositionViewText(text: String)
    fun setSeekBarMaxValue(maxValue: Int)
    fun setupWordListItems(linesList: ArrayList<WordListItem>)
}