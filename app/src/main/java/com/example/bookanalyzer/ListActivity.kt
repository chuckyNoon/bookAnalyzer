package com.example.bookanalyzer

import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ListActivity : AppCompatActivity() {
    var listLayout:LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        listLayout = findViewById(R.id.listLayout)
        val arguments = intent.extras
        val listPath = arguments?.getString("listPath")
        val listIn = openFileInput(listPath)
        val strMap = listIn.readBytes().toString(Charsets.UTF_8)
        val strs = strMap.split(',', ' ', '{')
        var i = 0
        for(str in strs){
                if (str == "")
                    continue
                val words = str.split("=")
                if (words.size != 2)
                    continue
                val hLayout = LinearLayout(this)
                hLayout.orientation = LinearLayout.HORIZONTAL
                val lparams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                hLayout.layoutParams = lparams

                val textView1 = TextView(this)
                val textView2 = TextView(this)
                val tparams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    0,135)//LinearLayout.LayoutParams.WRAP_CONTENT)
                tparams.weight = 1F

                textView1.text = words[0]
                textView2.text = words[1]
                textView1.layoutParams= tparams
                textView2.layoutParams= tparams
                textView1.gravity = Gravity.CENTER
                textView2.gravity = Gravity.CENTER
                textView1.textSize = 18F
                textView2.textSize = 18F


                //textView2.setBackgroundColor(Color.parseColor("#ff0000"))

                hLayout.addView(textView1)
                hLayout.addView(textView2)
                listLayout?.addView(hLayout)
                i++
                if (i == 40)
                    break
        }
    }
}