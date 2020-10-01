package com.example.bookanalyzer.mvp.presenters

import android.content.Context
import android.view.MenuItem
import android.widget.SeekBar
import com.example.bookanalyzer.ui.adapters.WordListAdapter
import com.example.bookanalyzer.mvp.repositories.WordListRepository
import com.example.bookanalyzer.mvp.views.WordListView

class WordListPresenter(val view: WordListView, val ctx: Context){
    private val repository = WordListRepository(ctx)
    private var wordListSize = 0
    fun onOptionsItemSelected(item: MenuItem) {
        if(item.itemId == android.R.id.home ){
            view.finishActivity()
        }
    }

    fun onStopTrackingTouch(seekBar: SeekBar?) {
        var progress = seekBar?.progress
        if (progress == 0)
            progress++
        view.setPositionText("${progress.toString()} from $wordListSize")
        if (progress != null)
            view.scrollToPosition(progress - 1)
    }

    fun createWordList(listPath:String) {
        val linesList = repository.readWordList(listPath) ?: return
        val wordListAdapter = WordListAdapter(ctx, linesList)
        wordListSize = linesList.size
        view.initRecyclerView(wordListAdapter)
        view.initSeekBar(wordListSize)
        view.setPositionText("1 from $wordListSize")
    }
}