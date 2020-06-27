package com.example.bookanalyzer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.*


class StartActivity : AppCompatActivity() {
    var slectButton:Button? = null
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val tableLayout:TableLayout = findViewById(R.id.tableLayout)
        try{
            val scanner = Scanner(openFileInput("all"))
            count = scanner.nextInt()
            println("f$count")
        }catch (e: IOException){
            println("file not created nor found")
        }

        val width = 3
        val height = count / 3 + 1
        var ind = 0
        for (i in 0 until height){
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )

            for (j in 0 until width){
                if (ind == count)
                    break
                val button = Button(this)
                val bitmapDrawable = BitmapDrawable(resources, getImg("img${i * 3 + j}"))
                button.background = bitmapDrawable
                val scale: Float = this.resources.displayMetrics.density
                val h = (150* scale + 0.5f).toInt()
                val w = (100* scale + 0.5f).toInt()
                button.layoutParams = TableRow.LayoutParams(w, h)
                button.id = ind
                button.setOnClickListener {
                    val button:Button = it as Button
                    val ind1 = button.id
                    val intent1 = Intent(this, MainActivity::class.java)
                    intent1.putExtra("listPath", "list$ind1")
                    intent1.putExtra("imgPath", "img$ind1")
                    intent1.putExtra("infoPath", "info$ind1")
                    println("img$ind1")
                    startActivity(intent1)
                }
                tableRow.addView(button)
                ind++
            }
            tableLayout.addView(tableRow)
        }

        slectButton = findViewById(R.id.selectButton)
        slectButton?.setOnClickListener {
            val intent:Intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 123)
        }
    }

    private fun getImg(path:String) : Bitmap{
        val f = openFileInput(path)
        val byteArray = f.readBytes()
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size?:0)
        return bmp
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            var strUri = data!!.data.toString()
            val intent = Intent(this, LoaderActivity::class.java)
            intent.putExtra("uri",strUri)
            intent.putExtra("ind", count)
            startActivity(intent)
        }
    }
}