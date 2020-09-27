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



class WordListActivity : AppCompatActivity(), IWordListContract.View{
    private lateinit var wordList: RecyclerView
    private lateinit var seekBar: SeekBar
    private lateinit var seekTextView: TextView
    private lateinit var presenter: IWordListContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)
        setToolBar()

        wordList = findViewById(R.id.word_list)
        seekBar = findViewById(R.id.seekBar)
        seekTextView = findViewById(R.id.seekTextView)

        presenter = WordListPresenter(this, this)
        val arguments = intent.extras
        val listPath = arguments?.getString("listPath") ?: return

        presenter.createWordList(listPath)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter.onStopTrackingTouch(seekBar)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

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
        presenter.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    override fun finishActivity() {
        finish()
    }

    override fun scrollToPosition(position: Int) {
        wordList.scrollToPosition(position - 1)
    }

    override fun setPositionText(text: String) {
        seekTextView.text = text
    }

    override fun initRecyclerView(adapter: WordListAdapter) {
        wordList.adapter = adapter
        wordList.layoutManager = LinearLayoutManager(this)
    }

    override fun initSeekBar(maxVal: Int) {
        seekBar.max = maxVal
    }
}
