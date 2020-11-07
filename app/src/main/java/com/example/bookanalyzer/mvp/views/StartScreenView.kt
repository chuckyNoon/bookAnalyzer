package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.adapters.book_items_adapter.BookCell
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

@AddToEnd
interface StartScreenView : MvpView {
    fun showLoadingStateView()
    fun hideLoadingStateView()
    fun moveLoadingStateViewUp(animDuration: Int)
    fun moveLoadingStateViewDown(animDuration: Int)
    fun setLoadingStateViewText(text: String)
    fun updateLoadingStateView(text: String, animDownDuration: Long, animUpDuration: Long)
    fun showSearchSettingsDialog()

    @Skip
    fun showSideMenu()

    @AddToEndSingle
    fun showBookList(bookCells: ArrayList<BookCell>)

    @Skip
    fun startLoaderScreenActivity(bookPath: String)

    @Skip
    fun startBookInfoActivity(analysisId: Int)
}