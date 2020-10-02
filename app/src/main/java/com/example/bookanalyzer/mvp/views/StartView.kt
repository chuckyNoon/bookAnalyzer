package com.example.bookanalyzer.mvp.views
import com.example.bookanalyzer.MenuBookModel

interface StartView {
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