package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.adapters.RecyclerListAdapter

interface StartView {
    fun setLoadingStateViewText(text:String)
    fun hideLoadingStateView()
    fun showLoadingStateView()
    fun updateLoadingStateView(str: String, downDuration: Long, upDuration: Long)
    fun showSideMenu()

    fun showSearchSettingsDialog()
    fun initRecyclerView(adapter: RecyclerListAdapter)
}