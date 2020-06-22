package com.example.bookanalyzer

import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Layout
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import kotlin.concurrent.thread
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    var imageView:ImageView? = null
    var bookView:TextView? = null
    var allWordView:TextView? = null
    var uniqueWordView:TextView? = null
    var avgSentenceView:TextView? = null
    var avgWordView:TextView? = null
    var listButton:Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        bookView = findViewById(R.id.bookView)
        allWordView = findViewById(R.id.allWordView)
        uniqueWordView = findViewById(R.id.uniqueWordView)
        avgSentenceView = findViewById(R.id.avgSentenceView)
        avgWordView = findViewById(R.id.avgWordView)
        listButton = findViewById(R.id.listButton)

        val arguments = intent.extras
        bookView?.text = arguments?.getString("bookName")
        allWordView?.text = arguments?.getString("wordCount")
        uniqueWordView?.text = arguments?.getString("uniqCount")
        avgSentenceView?.text = arguments?.getString("sentLen")
        avgWordView?.text = arguments?.getString("wordLen")
        var byteArray = arguments?.getByteArray("img")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size?:0)
        imageView?.setImageBitmap(bmp)

        val asyncTask = AsyncTask<V>
    }


}
