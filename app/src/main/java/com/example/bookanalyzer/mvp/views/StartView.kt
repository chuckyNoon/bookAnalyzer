package com.example.bookanalyzer.mvp.views
import com.example.bookanalyzer.MenuBookModel
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEnd
interface StartView : MvpView{
    fun setLoadingStateViewText(text:String)
    fun hideLoadingStateView()
    fun showLoadingStateView()
    fun moveLoadingStateViewUp(dur:Int)
    fun moveLoadingStateViewDown(dur:Int)
    fun updateLoadingStateView(str: String, downDuration: Long, upDuration: Long)
    fun showSideMenu()

    fun showSearchSettingsDialog()
    fun setupBooks(bookList:ArrayList<MenuBookModel>)
    fun addBook(book: MenuBookModel)
    fun updateBook(book: MenuBookModel)
}