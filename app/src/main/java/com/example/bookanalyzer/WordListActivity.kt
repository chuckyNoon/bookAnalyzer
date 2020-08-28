package com.example.bookanalyzer

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.word_list_elem.view.*
import java.io.IOException

class WordListActivity : AppCompatActivity() {
    private lateinit var wordList: RecyclerView
    private lateinit var linesList:ArrayList<WordListElemModel>
    private lateinit var seekBar: SeekBar
    private lateinit var seekTextView: TextView
    private var wordListAdapter:WordListAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)
        setToolBar()

        wordList = findViewById(R.id.word_list)
        seekBar = findViewById(R.id.seekBar)
        seekTextView = findViewById(R.id.seekTextView)

        val arguments = intent.extras
        val listPath = arguments?.getString("listPath") ?: return
        linesList = readWordList(listPath) ?: return
        wordListAdapter = WordListAdapter(this, linesList)
        wordList.adapter = wordListAdapter
        wordList.layoutManager = LinearLayoutManager(this)

        seekTextView.text = "1 from ${linesList.size}"
        seekBar.max = linesList.size
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                var progress = seekBar?.progress
                if (progress == 0)
                    progress++
                seekTextView.text = "${progress.toString()} from ${linesList.size}"
                if (progress != null) {
                    wordList.scrollToPosition(progress - 1)
                }
            }
        })
    }

    private fun setToolBar(){
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolBar?.title = "Word list"
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home ){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun readWordList(listPath:String):ArrayList<WordListElemModel>?{
        return try {
            val list = ArrayList<WordListElemModel>()
            val listIn = openFileInput(listPath)
            val strMap = listIn.readBytes().toString(Charsets.UTF_8)
            val lines = (strMap.substring(1, strMap.length - 1).split(','))
            for (i in lines.indices){
                val line = lines[i]
                val parts = line.split("=")
                if (parts.size == 2){
                    list.add(WordListElemModel(parts[0], parts[1], (i+1).toString()))
                }
            }
            return (list)
        }catch (e:IOException){
            println("reading list error")
            (null)
        }
    }

}

class WordListElemModel(var word:String, var frequency:String, var pos:String){
}

class WordListAdapter(val ctx: Context, val ar:ArrayList<WordListElemModel>) : RecyclerView.Adapter<WordListAdapter.MyViewHolder>() {
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    private val bottomPanel = (ctx as Activity).findViewById<View>(R.id.botomPanel)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_list_elem, parent, false)
        view.setOnClickListener{
            if (bottomPanel.visibility == View.VISIBLE)
                bottomPanel.visibility = View.INVISIBLE
            else
                bottomPanel.visibility = View.VISIBLE
        }
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return (ar.size)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val view = holder.view
        val data = ar[position]
        view.wordView.text = data.word
        view.frequencyView.text = data.frequency
        view.indView.text = data.pos
    }
}