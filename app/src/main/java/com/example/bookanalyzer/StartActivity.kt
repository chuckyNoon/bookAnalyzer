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
    private var currBookInd = 0
    private var tableWidth = 0
    private var tableHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        linLayout = findViewById(R.id.linLayout)
        bookCount = readCount()
        createTable()
    }

    override fun onStart() {
        super.onStart()
        val newBookCount = readCount()
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

        currBookInd = 0
        var lastRow:TableRow? = null
        for (i in 0 until tableHeight){
            lastRow = createRow()
            tableLayout.addView(lastRow)
        }
        val selectButton = createSelectButton()
        lastRow?.addView(selectButton)
        linLayout.addView(tableLayout)
    }

    private fun createRow() : TableRow{
        val tableRow = TableRow(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0,0,0,0)
            }
            gravity = Gravity.TOP

        }
        for (j in 0 until tableWidth){
            if (currBookInd == bookCount)
                break
            tableRow.addView(createField())
        }
        return (tableRow)
    }

    private fun dpToPx(dp:Int):Int{
        val scale: Float = this.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun createField() :Button{
        val button = Button(this).apply{
            val img =  getImg("img$currBookInd")
            if (img != null) {
                val bitmapDrawable = BitmapDrawable(resources, img)
                background = bitmapDrawable
            }else {
                    val name = getName("info$currBookInd")
                    text = if (name != null && name.length > 20)
                        name.substring(0..20) + "..." else name
                }
            val size = Point()
            windowManager.defaultDisplay.getSize(size)
            val margin= dpToPx(16)
            val w = size.x / tableWidth - margin
            val h = (w * 1.5).toInt()
            val lytParams = TableRow.LayoutParams(w, h)
            lytParams.setMargins(margin / 2, margin, margin / 2, margin/2)
            layoutParams = lytParams
            elevation = 8f
            stateListAnimator = null
            id = currBookInd
        }

        button.setOnClickListener {
            val btn:Button = it as Button
            val ind1 = btn.id
            val intent1 = Intent(this, MainActivity::class.java)
            intent1.putExtra("listPath", "list$ind1")
            intent1.putExtra("imgPath", "img$ind1")
            intent1.putExtra("infoPath", "info$ind1")
            startActivity(intent1)
        }
        currBookInd++
        return (button)
    }


    private fun createSelectButton() : Button{
        val selectButton = Button(this)
        selectButton.text = "+"

        val t = TableRow.LayoutParams(dpToPx(70),dpToPx(70))//TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        t.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10))
        t.gravity = Gravity.CENTER_VERTICAL
        selectButton.layoutParams = t
        selectButton.setOnClickListener {
            val intent:Intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 123)
        }
        return (selectButton)
    }

    private fun readCount() : Int{
        var count = 0
        try{
            val scanner = Scanner(openFileInput("all"))
            count = scanner.nextInt()
            scanner.close()
            println("f$count")
        }catch (e: IOException){
            println("file not created nor found")
        }
        return (count)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getImg(path:String) : Bitmap?{
        return try {
            val f = openFileInput(path)
            val byteArray = f.readBytes()
            val base64Array = String(byteArray,StandardCharsets.UTF_8)
            try {
                val newByteArray = Base64.getMimeDecoder().decode(base64Array)
                val bmp = BitmapFactory.decodeByteArray(newByteArray, 0, newByteArray.size)
                (bmp)
            }catch (e:java.lang.IllegalArgumentException){
                val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                (bmp)
            }
        }catch (e:IOException){
            (null)
        }
    }

    private fun getName(path: String) : String?{
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val strUri = data?.data.toString()
            val intent = Intent(this, LoaderActivity::class.java)
            intent.putExtra("uri",strUri)
            intent.putExtra("ind", bookCount)
            startActivity(intent)
        }
    }
}