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
        print("ddd")
        listLayout = findViewById(R.id.listLayout)
        val arguments = intent.extras
        val size =arguments?.getInt("size")
        print("ddd")
        size?.let{
            for(i in 0 until it){
                var hLayout = LinearLayout(this)
                hLayout.orientation = LinearLayout.HORIZONTAL
                val lparams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                hLayout.layoutParams = lparams

                val textView1 = TextView(this)
                val textView2 = TextView(this)
                val tparams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    0,135)//LinearLayout.LayoutParams.WRAP_CONTENT)
                tparams.weight = 1F

                textView1.text = arguments.getString("word$i")
                textView2.text = arguments.getInt("count$i").toString()
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
                if (i == 40)
                    break
                println(i)
            }
        }
    }
}