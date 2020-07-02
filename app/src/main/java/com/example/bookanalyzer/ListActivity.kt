package com.example.bookanalyzer

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class ListActivity : AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var scrollView: ScrollView
    private lateinit var indicesView: TextView
    private lateinit var toNextButton: Button
    private lateinit var toPrevButton: Button
    private lateinit var toStartButton: Button
    private lateinit var toEndButton: Button

    private lateinit var linesList:List<String>
    private lateinit var tableFields: Array<Pair<TextView?, TextView?>>
    private val elementsOnPage: Int = 15
    private var currInd: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        tableLayout = findViewById(R.id.listLayout)
        toNextButton = findViewById(R.id.toNextButton)
        toPrevButton = findViewById(R.id.toPrevButton)
        toStartButton = findViewById(R.id.toStartButton)
        toEndButton = findViewById(R.id.toEndButton)
        scrollView = findViewById(R.id.scroll)
        indicesView = findViewById(R.id.indicesView)
        indicesView.text = "1 - ${elementsOnPage + 1}"

        val arguments = intent.extras
        val listPath = arguments?.getString("listPath")?:return
        linesList = readLinesList(listPath)?:return
        createWordTable()

        toPrevButton.setOnClickListener {
            currInd -= elementsOnPage
            if (currInd < 0)
                currInd = 0
            updateText()
        }
        toNextButton.setOnClickListener {
            currInd += elementsOnPage
            if (currInd + elementsOnPage >= linesList.size)
                currInd =  linesList.size - elementsOnPage
            updateText()
            //scrollView?.fullScroll(ScrollView.FOCUS_UP)
        }
        toStartButton.setOnClickListener {
            currInd = 0
            updateText()
        }
        toEndButton.setOnClickListener {
            currInd = linesList.size - elementsOnPage
            updateText()
        }
    }

    private fun readLinesList(listPath:String):List<String>?{
        return try {
            val listIn = openFileInput(listPath)
            val strMap = listIn.readBytes().toString(Charsets.UTF_8)
            (strMap.substring(1, strMap.length - 1).split(','))
        }catch (e:IOException){
            println("reading list error")
            (null)
        }
    }

    private fun createWordTable() {
        tableFields = Array(elementsOnPage) { null to null }
        for (i in 0 until elementsOnPage) {
            val row = TableRow(this).apply {
                val lytParams: TableLayout.LayoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams = lytParams
            }
            val wordView = TextView(this).apply{
                val lytParams = TableRow.LayoutParams(0, dpToPx(135))
                lytParams.weight = 2F
                layoutParams = lytParams
                textSize = 24F
                setPadding(dpToPx(0), 0, 0, 0)
                gravity = Gravity.CENTER
            }
            val numberView = TextView(this).apply {
                val lytParams = TableRow.LayoutParams(0, dpToPx(135))
                lytParams.leftMargin = dpToPx(30)
                lytParams.weight = 1F
                layoutParams = lytParams
                textSize = 24F
                gravity = Gravity.START
            }
            row.addView(wordView)
            row.addView(numberView)
            tableLayout.addView(row)
            tableFields[i] = wordView to numberView
        }
        updateText()
    }

    private fun updateText(ind: Int = currInd) {
        for (i in 0 until elementsOnPage) {
            val str = linesList[i + ind]
            if (str == "")
                continue
            val words = str.split("=")
            if (words.size != 2)
                continue
            tableFields[i].first?.text = words[0]
            tableFields[i].second?.text = words[1]
        }
        val newIndices = "${currInd+1} - ${currInd + elementsOnPage}"
        indicesView.text = newIndices
    }

    private fun dpToPx(dp: Int): Int {
        val scale: Float = this.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}