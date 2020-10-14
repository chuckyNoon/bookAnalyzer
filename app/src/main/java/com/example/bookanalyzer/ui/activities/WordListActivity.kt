package com.example.bookanalyzer.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.presenters.WordListPresenter
import com.example.bookanalyzer.mvp.repositories.WordListRepository
import com.example.bookanalyzer.mvp.views.WordListView
import com.example.bookanalyzer.ui.adapters.WordListAdapter
import com.example.bookanalyzer.ui.adapters.WordListItem
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter


class WordListActivity : MvpAppCompatActivity(), WordListView {
    private lateinit var wordList: RecyclerView
    private lateinit var seekBar: SeekBar
    private lateinit var seekTextView: TextView
    private lateinit var bottomPanel:View
    private lateinit var wordListAdapter: WordListAdapter

    private var repository = WordListRepository(this)
    private val presenter by moxyPresenter{ WordListPresenter(repository) }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)

        wordList = findViewById(R.id.word_list)
        bottomPanel = findViewById(R.id.bottomPanel)
        seekBar = findViewById(R.id.seekBar)
        seekTextView = findViewById(R.id.seekTextView)

        setToolBar()
        setRecyclerView()

        val arguments = intent.extras
        val ind = arguments?.getInt("ind")
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter.onProgressChanged(seekBar?.progress ?: 0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        if (ind != null && savedInstanceState == null)
            presenter.onViewCreated(ind)
    }

    private fun setRecyclerView() {
        wordListAdapter = WordListAdapter()
        wordListAdapter.setOnItemClickListener(View.OnClickListener {
            bottomPanel.visibility = if (bottomPanel.visibility == View.VISIBLE) {
                View.INVISIBLE
            }else{
                View.VISIBLE
            }
        })
        wordList.adapter = wordListAdapter
        wordList.layoutManager = LinearLayoutManager(this)

    }

    private fun setToolBar(){
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolBar?.title = "Word list"
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home ){
            presenter.onOptionsItemSelected()
        }
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

    override fun setSeekBarMaxValue(maxVal: Int) {
        seekBar.max = maxVal
    }

    override fun setupWordLines(linesList: ArrayList<WordListItem>) {
        wordListAdapter.setupData(linesList)
    }
}
