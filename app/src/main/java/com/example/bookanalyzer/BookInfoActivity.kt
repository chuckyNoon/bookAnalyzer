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
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt


class BookInfoActivity : AppCompatActivity() {
    private lateinit var bookNameView:TextView
    private lateinit var allWordView:TextView
    private lateinit var textLengthView:TextView
    private lateinit var uniqueWordView:TextView
    private lateinit var avgSentenceViewWrd:TextView
    private lateinit var avgSentenceViewChr:TextView
    private lateinit var avgWordView:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)
        setToolBar()

        /*imageView = findViewById(R.id.imageView)
        bookView = findViewById(R.id.bookView)
        allWordView = findViewById(R.id.allWordView)
        uniqueWordView = findViewById(R.id.uniqueWordView)
        avgSentenceView = findViewById(R.id.avgSentenceView)
        avgWordView = findViewById(R.id.avgWordView)
        listButton = when(resources.configuration.orientation){
            Configuration.ORIENTATION_LANDSCAPE -> findViewById(R.id.listButton)
            else -> findViewById(R.id.listButton)
        }*/

        bookNameView = findViewById(R.id.bookNameView)
        allWordView = findViewById(R.id.allWordCountView)
        uniqueWordView = findViewById(R.id.uniqWordView)
        textLengthView = findViewById(R.id.allCharsCountView)
        avgSentenceViewWrd = findViewById(R.id.avgSentenceLenView1)
        avgSentenceViewChr = findViewById(R.id.avgSentenceLenView2)
        avgWordView = findViewById(R.id.avgWordLenView)


        val arguments = intent.extras
        val listPath = arguments?.getString("listPath")
        val infoPath = arguments?.getString("infoPath")

        infoPath?.let { readInfo(it) }
        findViewById<Button>(R.id.toWordListButton).setOnClickListener{
            val intent = Intent(this, WordListActivity::class.java)
            intent.putExtra("listPath", listPath)
            startActivity(intent)
        }

    }

   private fun setToolBar(){
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolBar?.title = "Info"
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home ){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun readInfo(path:String){
        try {
            val fileInput = openFileInput(path)
            val scanner = Scanner(fileInput)

            bookNameView.text = scanner.nextLine().split("/").last()
            allWordView.text = scanner.nextLine()
            uniqueWordView.text = scanner.nextLine()
            avgSentenceViewWrd.text = scanner.nextLine()
            avgSentenceViewChr.text =  avgSentenceViewWrd.text
            avgWordView.text = scanner.nextLine()
        }catch (e:IOException){
            println("reading info error")
        }
    }
}
