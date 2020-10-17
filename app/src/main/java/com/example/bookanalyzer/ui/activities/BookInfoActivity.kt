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
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar
    private lateinit var bookNameView: TextView
    private lateinit var wordCountView: TextView
    private lateinit var charCountView: TextView
    private lateinit var uniqueWordView: TextView
    private lateinit var avgSentenceLenViewWrd: TextView
    private lateinit var avgSentenceLenViewChr: TextView
    private lateinit var avgWordLenView: TextView
    private lateinit var toWordListButton: Button

    private var repository = BookInfoRepository(this)
    private val presenter by moxyPresenter { BookInfoPresenter(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)

        initFields()
        setToolBar()
        setToWordListButton()
        selectLaunchOption(savedInstanceState != null)
    }

    private fun selectLaunchOption(isActivityRecreated: Boolean) {
        val analysisId = getAnalysisIdFromIntent()
        analysisId?.let {
            if (!isActivityRecreated) {
                presenter.onViewCreated(analysisId)
            }
        }
    }

    private fun getAnalysisIdFromIntent(): Int? {
        return intent.extras?.getInt(EXTRA_ANALYSIS_ID)
    }

    private fun initFields() {
        toolBar = findViewById(R.id.toolbar)
        bookNameView = findViewById(R.id.bookNameView)
        wordCountView = findViewById(R.id.allWordCountView)
        uniqueWordView = findViewById(R.id.uniqWordView)
        charCountView = findViewById(R.id.allCharsCountView)
        avgSentenceLenViewWrd = findViewById(R.id.avgSentenceLenView1)
        avgSentenceLenViewChr = findViewById(R.id.avgSentenceLenView2)
        avgWordLenView = findViewById(R.id.avgWordLenView)
        toWordListButton = findViewById(R.id.toWordListButton)
    }

    private fun setToolBar() {
        toolBar.title = resources.getString(R.string.info_activity_title)
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    private fun setToWordListButton() {
        val analysisId = getAnalysisIdFromIntent()
        analysisId?.let {
            toWordListButton.setOnClickListener {
                presenter.onWordListButtonClicked(analysisId)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            presenter.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setViewsText(bookInfoModel: BookInfoModel) {
        bookNameView.text = bookInfoModel.path
        uniqueWordView.text = bookInfoModel.uniqueWordCount
        wordCountView.text = bookInfoModel.allWordCount
        charCountView.text = bookInfoModel.allCharsCount
        avgSentenceLenViewWrd.text = bookInfoModel.avgSentenceLenInWrd
        avgSentenceLenViewChr.text = bookInfoModel.avgSentenceLenInChr
        avgWordLenView.text = bookInfoModel.avgWordLen
    }

    override fun startWordListActivity(analysisId: Int) {
        val intent = Intent(this, WordListActivity::class.java).apply {
            putExtra(EXTRA_ANALYSIS_ID, analysisId)
        }
        startActivity(intent)
    }

    override fun finishActivity() {
        finish()
    }
}

data class BookInfoModel(
    var path: String,
    var uniqueWordCount: String,
    var allWordCount: String,
    var allCharsCount: String,
    var avgSentenceLenInWrd: String,
    var avgSentenceLenInChr: String,
    var avgWordLen: String
)