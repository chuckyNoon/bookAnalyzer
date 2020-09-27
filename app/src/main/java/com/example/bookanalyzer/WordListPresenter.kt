package com.example.bookanalyzer

import android.content.Context
import android.view.MenuItem
import android.widget.SeekBar

class WordListPresenter(val view:IWordListContract.View, val ctx: Context) : IWordListContract.Presenter {
    private val repository:IWordListContract.Repository = WordListRepository(ctx)
    private var wordListSize = 0
    override fun onOptionsItemSelected(item: MenuItem) {
        if(item.itemId == android.R.id.home ){
            view.finishActivity()
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        var progress = seekBar?.progress
        if (progress == 0)
            progress++
        view.setPositionText("${progress.toString()} from $wordListSize")
        if (progress != null)
            view.scrollToPosition(progress - 1)
    }

    override fun createWordList(listPath:String) {
        val linesList = repository.readWordList(listPath) ?: return
        val wordListAdapter = WordListAdapter(ctx, linesList)
        wordListSize = linesList.size
        view.initRecyclerView(wordListAdapter)
        view.initSeekBar(wordListSize)
        view.setPositionText("1 from $wordListSize")
    }
}