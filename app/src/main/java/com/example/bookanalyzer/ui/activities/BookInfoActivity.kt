package com.example.bookanalyzer.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.presenters.BookInfoPresenter
import com.example.bookanalyzer.mvp.views.BookInfoView

class BookInfoActivity : AppCompatActivity(),
    BookInfoView {
    private lateinit var bookNameView: TextView
    private lateinit var allWordView: TextView
    private lateinit var textLengthView: TextView
    private lateinit var uniqueWordView: TextView
    private lateinit var avgSentenceViewWrd: TextView
    private lateinit var avgSentenceViewChr: TextView
    private lateinit var avgWordView: TextView

    private lateinit var presenter: BookInfoPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)
        setToolBar()

        bookNameView = findViewById(R.id.bookNameView)
        allWordView = findViewById(R.id.allWordCountView)
        uniqueWordView = findViewById(R.id.uniqWordView)
        textLengthView = findViewById(R.id.allCharsCountView)
        avgSentenceViewWrd = findViewById(R.id.avgSentenceLenView1)
        avgSentenceViewChr = findViewById(R.id.avgSentenceLenView2)
        avgWordView = findViewById(R.id.avgWordLenView)
        presenter = BookInfoPresenter(this, this.applicationContext)

        val arguments = intent.extras
        val listPath = arguments?.getString("listPath")
        val infoPath = arguments?.getString("infoPath")

        findViewById<Button>(R.id.toWordListButton).setOnClickListener {
            presenter.onWordListButtonClicked(listPath ?: "")
        }
        infoPath?.let {
            presenter.fillViews(infoPath)
        }
    }

    private fun setToolBar() {
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolBar?.title = "Info"
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter.onOptionsItemSelected(item)
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
}