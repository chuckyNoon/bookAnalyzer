package com.example.bookanalyzer

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*


class StartActivity : AppCompatActivity() {
    private lateinit var linLayout:LinearLayout
    private lateinit var tableLayout:TableLayout
    private  var bookCount = 0
    private var tableWidth = 0
    private var tableHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        linLayout = findViewById(R.id.linLayout)
        bookCount = getBookCount()
        createTable()
    }

    override fun onStart() {
        super.onStart()
        val newBookCount = getBookCount()
        if (newBookCount != bookCount) {
            linLayout.removeView(tableLayout)
            bookCount = newBookCount
            createTable()
        }
    }

    private fun createTable(){
        tableWidth = when(resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> 3
            else -> 6
        }
        tableHeight = (bookCount) / tableWidth + 1
        tableLayout = TableLayout(this)
        tableLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        var lastRow:TableRow? = null
        for (i in 0 until tableHeight){
            lastRow = createRow(i)
            tableLayout.addView(lastRow)
        }
        val addButton = createAddButton()
        lastRow?.addView(addButton)
        linLayout.addView(tableLayout)
    }

    private fun createRow(rowNumber:Int) : TableRow{
        val tableRow = TableRow(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0,0,0,0)
            }
            gravity = Gravity.TOP
        }
        var currBookInd = rowNumber * tableWidth
        for (j in 0 until tableWidth){
            if (currBookInd == bookCount)
                break
            tableRow.addView(createBookButton(currBookInd))
            currBookInd++
        }
        return (tableRow)
    }

    private fun createBookButton(currBookInd:Int) :Button{
        val button = Button(this).apply{
            val img =  getBookImg("img$currBookInd")
            if (img != null) {
                val bitmapDrawable = BitmapDrawable(resources, img)
                background = bitmapDrawable
            }else {
                val name = getBookName("info$currBookInd")
                text = if (name != null && name.length > 20)
                    name.substring(0..20) + "..." else name
            }
            val size = Point()
            windowManager.defaultDisplay.getSize(size)
            val margin = dpToPx(16)
            val btnWidth = size.x / tableWidth - margin
            val btnHeight = (btnWidth * 1.5).toInt()
            val lytParams = TableRow.LayoutParams(btnWidth, btnHeight)
            lytParams.setMargins(margin / 2, margin, margin / 2, margin/2)
            layoutParams = lytParams
            elevation = 8f
            stateListAnimator = null
            id = currBookInd
        }

        button.setOnClickListener {
            val btn:Button = it as Button
            val bookInd = btn.id
            val intentToBook = Intent(this, MainActivity::class.java)
            intentToBook.putExtra("listPath", "list$bookInd")
            intentToBook.putExtra("imgPath", "img$bookInd")
            intentToBook.putExtra("infoPath", "info$bookInd")
            startActivity(intentToBook)
        }
        return (button)
    }

    private fun createAddButton() : Button{
        val addButton = Button(this).apply {
            val lytParams = TableRow.LayoutParams(dpToPx(70),dpToPx(70))//TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            lytParams.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10))
            gravity = Gravity.CENTER
            layoutParams = lytParams
            text = "+"
        }
        addButton.setOnClickListener {
            val intent:Intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 123)
        }
        return (addButton)
    }

    private fun getBookCount() : Int{
        var count = 0
        try{
            val scanner = Scanner(openFileInput("all"))
            count = scanner.nextInt()
            scanner.close()
        }catch (e: IOException){
            println("file not created nor found")
        }
        return (count)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBookImg(path:String) : Bitmap?{
        return try {
            val fileInput = openFileInput(path)
            val byteArray = fileInput.readBytes()
            val base64Array = String(byteArray,StandardCharsets.UTF_8)
            try {
                val decodedByteArray = Base64.getMimeDecoder().decode(base64Array)
                val bmp = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
                (bmp)
            }catch (e:java.lang.IllegalArgumentException){//if not in base64
                val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                (bmp)
            }
        }catch (e:IOException){
            (null)
        }
    }

    private fun getBookName(path: String) : String?{
        return try {
            val fileInput = openFileInput(path)
            val scanner = Scanner(fileInput)
            val name = scanner.nextLine()
            scanner.close()
            (name)
        }catch (e:IOException){
            null
        }
    }

    private fun dpToPx(dp:Int):Int {
        val scale: Float = this.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val strUri = data?.data.toString()
            val intent = Intent(this, LoaderActivity::class.java)
            val newBookInd = bookCount
            intent.putExtra("uri", strUri)
            intent.putExtra("ind", newBookInd)
            startActivity(intent)
        }
    }
}