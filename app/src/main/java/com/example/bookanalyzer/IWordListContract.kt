package com.example.bookanalyzer

import android.view.MenuItem
import android.widget.SeekBar

interface IWordListContract {
    interface View{
        fun finishActivity()
        fun scrollToPosition(position: Int)
        fun setPositionText(text:String)
        fun initRecyclerView(adapter: WordListAdapter)
        fun initSeekBar(maxVal:Int)
    }
    interface Presenter{
        fun onOptionsItemSelected(item: MenuItem)
        fun onStopTrackingTouch(seekBar: SeekBar?)
        fun createWordList(listPath: String)
    }
    interface Repository{
        fun readWordList(listPath:String):ArrayList<WordListElemModel>?
    }
}