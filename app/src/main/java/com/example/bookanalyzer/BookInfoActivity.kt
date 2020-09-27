package com.example.bookanalyzer

import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Layout
import android.view.MenuItem
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_book_info.*
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt

interface IBookInfoContract{
    interface View{
        fun finishActivity()
        fun setViewsText(path:String,
                         uniqWordCount:String,
                         allWordCount:String,
                         allCharsCount:String,
                         avgSentenceLenInWrd:String,
                         avgSentenceLenInChr: String,
                         avgWordLen:String)


    }
    interface Presenter{
        fun onOptionsItemSelected(item: MenuItem)
        fun onWordListButtonClicked(listPath: String)
        fun fillViews(path:String)
    }
    interface Repository{
        fun readInfo(path:String) : BookInfoModel
    }
}

class BookInfoActivity : AppCompatActivity(),
                        IBookInfoContract.View{
    private lateinit var bookNameView:TextView
    private lateinit var allWordView:TextView
    private lateinit var textLengthView:TextView
    private lateinit var uniqueWordView:TextView
    private lateinit var avgSentenceViewWrd:TextView
    private lateinit var avgSentenceViewChr:TextView
    private lateinit var avgWordView:TextView

    private lateinit var presenter: IBookInfoContract.Presenter
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

        findViewById<Button>(R.id.toWordListButton).setOnClickListener{
            presenter.onWordListButtonClicked(listPath?:"")
        }
        infoPath?.let {
            presenter.fillViews(infoPath)
        }
    }

   private fun setToolBar(){
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
        uniqWordView.text = uniqWordCount
        allWordView.text = allWordCount
        allCharsCountView.text = allCharsCount
        avgSentenceViewWrd.text = avgSentenceLenInWrd
        avgSentenceViewChr.text = avgSentenceLenInChr
        avgWordView.text = avgWordLen
    }
}
