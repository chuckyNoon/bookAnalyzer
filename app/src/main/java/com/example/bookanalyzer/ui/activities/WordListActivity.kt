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
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar
    private lateinit var wordList: RecyclerView
    private lateinit var seekBar: SeekBar
    private lateinit var positionTextView: TextView
    private lateinit var bottomPanel: View
    private lateinit var wordListAdapter: WordListAdapter

    private var repository = WordListRepository(this)
    private val presenter by moxyPresenter { WordListPresenter(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)

        initFields()
        setToolBar()
        setRecyclerView()
        setSeekBar()

        selectLaunchOption(savedInstanceState != null)
    }

    private fun selectLaunchOption(isActivityRecreated: Boolean) {
        val bookInd = getBookIndFromIntent()
        if (bookInd != null && !isActivityRecreated) {
            presenter.onViewCreated(bookInd)
        }
    }

    private fun getBookIndFromIntent(): Int? {
        return intent.extras?.getInt("ind")
    }

    private fun initFields() {
        toolBar = findViewById(R.id.toolbar)
        wordList = findViewById(R.id.word_list)
        bottomPanel = findViewById(R.id.bottomPanel)
        seekBar = findViewById(R.id.seek_bar)
        positionTextView = findViewById(R.id.text_view_position)
    }

    private fun setRecyclerView() {
        wordListAdapter = WordListAdapter()
        wordListAdapter.setOnItemClickListener {
            bottomPanel.visibility = if (bottomPanel.visibility == View.VISIBLE) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
        }
        wordList.adapter = wordListAdapter
        wordList.layoutManager = LinearLayoutManager(this)
    }

    private fun setToolBar() {
        toolBar.title = "Word list"
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    private fun setSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter.onProgressChanged(seekBar?.progress ?: 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            presenter.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun scrollToPosition(position: Int) {
        wordList.scrollToPosition(position - 1)
    }

    override fun setPositionViewText(text: String) {
        positionTextView.text = text
    }

    override fun setSeekBarMaxValue(maxValue: Int) {
        seekBar.max = maxValue
    }

    override fun setupWordListItems(linesList: ArrayList<WordListItem>) {
        wordListAdapter.setupData(linesList)
    }

    override fun finishActivity() {
        finish()
    }

}
