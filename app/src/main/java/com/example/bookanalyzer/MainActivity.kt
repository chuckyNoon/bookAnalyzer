package com.example.bookanalyzer

import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
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
import kotlinx.android.synthetic.main.book_info.*
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    private lateinit var bookNameView:TextView
    private lateinit var allWordView:TextView
    private lateinit var textLengthView:TextView
    private lateinit var uniqueWordView:TextView
    private lateinit var avgSentenceViewWrd:TextView
    private lateinit var avgSentenceViewChr:TextView
    private lateinit var avgWordView:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.book_info)

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
        val imgPath = arguments?.getString("imgPath")
        val listPath = arguments?.getString("listPath")
        val infoPath = arguments?.getString("infoPath")

     //   imgPath?.let { readImg(it) }
         infoPath?.let { readInfo(it) }

        /*listButton.setOnClickListener {
            val listIntent = Intent(this,ListActivity::class.java)
            listIntent.putExtra("listPath", listPath)
            startActivity(listIntent)
        }*/
    }

    private fun readImg(path:String){
        val f = openFileInput(path)
        val byteArray = f.readBytes()
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size?:0)
       // imageView.setImageBitmap(bmp)
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
