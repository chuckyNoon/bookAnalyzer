package com.example.bookanalyzer.mvp.views

import com.example.bookanalyzer.ui.adapters.BookListItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

@Skip
interface StartView : MvpView {
    fun showLoadingStateView()
    fun hideLoadingStateView()
    fun moveLoadingStateViewUp(duration: Int)
    fun moveLoadingStateViewDown(duration: Int)
    fun setLoadingStateViewText(text: String)
    fun updateLoadingStateView(text: String, downDuration: Long, upDuration: Long)
    fun showSearchSettingsDialog()
    fun showSideMenu()

    @AddToEndSingle
    fun showBookList(itemList: ArrayList<BookListItem>)
    fun startLoaderScreenActivity(bookPath: String, newBookInd: Int)
    fun startBookInfoActivity(bookInd: Int)
}