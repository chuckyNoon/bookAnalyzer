package com.example.bookanalyzer.mvp.presenters

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.example.bookanalyzer.mvp.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView
import com.example.bookanalyzer.ui.activities.WordListActivity

class BookInfoPresenter(val view: BookInfoView, val ctx:Context){
    val repository= BookInfoRepository(ctx)
    fun onOptionsItemSelected(item: MenuItem) {
        if(item.itemId == android.R.id.home ){
            view.finishActivity()
        }
    }

    fun onWordListButtonClicked(listPath: String) {
        val newIntent = Intent(ctx, WordListActivity::class.java)
        newIntent.putExtra("listPath", listPath)
        ctx.startActivity(newIntent)
    }

    fun fillViews(path:String) {
        val model = repository.readInfo(path)
        view.setViewsText(model.path, model.uniqueWordCount, model.allWordCount,
            model.allCharsCount, model.avgSentenceLenInWrd, model.avgSentenceLenInChr, model.avgWordLen)
    }
}