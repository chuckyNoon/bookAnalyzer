package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.adapters.WordListAdapter

interface WordListView {
    fun finishActivity()
    fun scrollToPosition(position: Int)
    fun setPositionText(text:String)
    fun initRecyclerView(adapter: WordListAdapter)
    fun initSeekBar(maxVal:Int)
}