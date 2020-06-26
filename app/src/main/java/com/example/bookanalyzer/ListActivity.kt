package com.example.bookanalyzer

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ListActivity : AppCompatActivity() {
    var listLayout:LinearLayout? = null
    val sm:Int = 15
    var currInd:Int = 0
    var strs:List<String>? = null
    var views:Array<Pair<TextView?,TextView?>>? = null

    var scrollView:ScrollView? = null
    var indicesView:TextView? = null
    var toNextButton:Button? = null
    var toPrevButton:Button? = null
    var toStartButton:Button? = null
    var toEndButton:Button? = null

    private fun createWordList(strs:List<String>){
        views = Array<Pair<TextView?,TextView?>>(sm) {null to null}
        var ind = 0
        for(i in 0 until sm){
            val str = strs[i]
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

            hLayout.addView(textView1)
            hLayout.addView(textView2)
            listLayout?.addView(hLayout)
            views!![i] = textView1 to textView2
        }
    }

    private fun updateList(strs:List<String>, ind:Int = currInd){
        for (i in 0 until sm){
            val str = strs[i + ind]
            if (str == "")
                continue
            val words = str.split("=")
            if (words.size != 2)
                continue

            views!![i].first?.text = words[0]
            views!![i].second?.text = words[1]
            val newText = (currInd+1).toString() + " - " + (currInd+sm).toString()
            indicesView!!.text = newText
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        listLayout = findViewById(R.id.listLayout)
        toNextButton = findViewById(R.id.toNextButton)
        toPrevButton = findViewById(R.id.toPrevButton)
        toStartButton = findViewById(R.id.toStartButton)
        toEndButton = findViewById(R.id.toEndButton)
        scrollView = findViewById(R.id.scroll)
        indicesView = findViewById(R.id.indicesView)

        val newText = "1 - ${sm + 1}"
        indicesView!!.text = newText

        val arguments = intent.extras
        val listPath = arguments?.getString("listPath")
        val listIn = openFileInput(listPath)
        val strMap = listIn.readBytes().toString(Charsets.UTF_8)
        strs = strMap.substring(1,strMap.length - 2).split(',')
        createWordList(strs!!)
        toPrevButton?.setOnClickListener {
            currInd-=sm
            if (currInd < 0)
                currInd = 0
            updateList(strs!!)
        }
        toNextButton?.setOnClickListener {
            currInd+=sm
            if (currInd + sm >= strs!!.size)
                currInd = strs!!.size - sm
            updateList(strs!!)
            //scrollView?.fullScroll(ScrollView.FOCUS_UP)
        }
        toStartButton?.setOnClickListener {
            currInd = 0
            updateList(strs!!)
            //scrollView?.fullScroll(ScrollView.FOCUS_UP)
        }
        toEndButton?.setOnClickListener {
            currInd = strs!!.size - sm
            updateList(strs!!)
            //scrollView?.fullScroll(ScrollView.FOCUS_UP)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }
}