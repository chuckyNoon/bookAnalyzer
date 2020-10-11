package com.example.bookanalyzer.mvp.views
import com.example.bookanalyzer.MenuBookModel
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

@AddToEnd
interface StartView : MvpView{
    fun setLoadingStateViewText(text:String)
    fun hideLoadingStateView()
    fun showLoadingStateView()
    fun moveLoadingStateViewUp(dur:Int)
    fun moveLoadingStateViewDown(dur:Int)
    fun updateLoadingStateView(str: String, downDuration: Long, upDuration: Long)
   // @Skip
   // fun showSearchSettingsDialog()

    @AddToEndSingle
    fun showList(bookList: ArrayList<MenuBookModel>)
    @Skip
    fun startLoadingActivity(bookPath:String, newBookInd:Int)
    @Skip
    fun startInfoActivity(bookInd:Int)
}