package com.example.bookanalyzer

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.Button

class BookInfoPresenter(val view:IBookInfoContract.View, val ctx:Context) : IBookInfoContract.Presenter {
    val repository:IBookInfoContract.Repository = BookInfoRepository(ctx)
    override fun onOptionsItemSelected(item: MenuItem) {
        if(item.itemId == android.R.id.home ){
            view.finishActivity()
        }
    }

    override fun onWordListButtonClicked(listPath: String) {
        val newIntent = Intent(ctx, WordListActivity::class.java)
        newIntent.putExtra("listPath", listPath)
        ctx.startActivity(newIntent)
    }

    override fun fillViews(path:String) {
        val model = repository.readInfo(path)
        view.setViewsText(model.path, model.uniqueWordCount, model.allWordCount,
            model.allCharsCount, model.avgSentenceLenInWrd, model.avgSentenceLenInChr, model.avgWordLen)
    }
}