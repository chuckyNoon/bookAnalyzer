package com.example.bookanalyzer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.presenters.BookInfoPresenter
import com.example.bookanalyzer.mvp.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class BookInfoActivity : MvpAppCompatActivity(),
    BookInfoView {
    private lateinit var bookNameView: TextView
    private lateinit var allWordView: TextView
    private lateinit var textLengthView: TextView
    private lateinit var uniqueWordView: TextView
    private lateinit var avgSentenceViewWrd: TextView
    private lateinit var avgSentenceViewChr: TextView
    private lateinit var avgWordView: TextView

    private var repository = BookInfoRepository(this)
    private val presenter by moxyPresenter{BookInfoPresenter(repository)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)

        bookNameView = findViewById(R.id.bookNameView)
        allWordView = findViewById(R.id.allWordCountView)
        uniqueWordView = findViewById(R.id.uniqWordView)
        textLengthView = findViewById(R.id.allCharsCountView)
        avgSentenceViewWrd = findViewById(R.id.avgSentenceLenView1)
        avgSentenceViewChr = findViewById(R.id.avgSentenceLenView2)
        avgWordView = findViewById(R.id.avgWordLenView)

        setToolBar()
        val arguments = intent.extras
        val ind = arguments?.getInt("ind")

        if (ind == null)
            finish()
        findViewById<Button>(R.id.toWordListButton).setOnClickListener {
            presenter.onWordListButtonClicked(ind!!)
        }
        if (savedInstanceState == null)
            presenter.onViewCreated(ind!!)

    }

    private fun setToolBar() {
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolBar?.title = "Info"
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home ){
            presenter.onOptionsItemSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finishActivity() {
        finish()
    }

    override fun setViewsText(
        path: String,
        uniqWordCount: String,
        allWordCount: String,
        allCharsCount: String,
        avgSentenceLenInWrd: String,
        avgSentenceLenInChr: String,
        avgWordLen: String
    ) {
        bookNameView.text = path
        uniqueWordView.text = uniqWordCount
        allWordView.text = allWordCount
        textLengthView.text = allCharsCount
        avgSentenceViewWrd.text = avgSentenceLenInWrd
        avgSentenceViewChr.text = avgSentenceLenInChr
        avgWordView.text = avgWordLen
    }

    override fun startWordListActivity(ind: Int) {
        val newIntent = Intent(this, WordListActivity::class.java)
        newIntent.putExtra("ind", ind)
        startActivity(newIntent)
    }
}