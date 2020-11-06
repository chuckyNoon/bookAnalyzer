package com.example.bookanalyzer.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ActivityWordListBinding
import com.example.bookanalyzer.mvp.presenters.WordListPresenter
import com.example.bookanalyzer.domain.repositories.WordListRepository
import com.example.bookanalyzer.mvp.views.WordListView
import com.example.bookanalyzer.ui.adapters.word_list_adapter.WordListAdapter
import com.example.bookanalyzer.ui.adapters.word_list_adapter.WordListItem
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject


class WordListActivity : MvpAppCompatActivity(), WordListView {

    private lateinit var binding: ActivityWordListBinding
    private lateinit var adapter: WordListAdapter

    @Inject
    lateinit var repository: WordListRepository

    @InjectPresenter
    lateinit var presenter: WordListPresenter

    @ProvidePresenter
    fun provideStartScreenPresenter(): WordListPresenter {
        MyApp.appComponent.inject(this)
        return WordListPresenter(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolBar()
        setupRecyclerView()
        setupSeekBar()
        selectLaunchOption(savedInstanceState != null)
    }

    private fun selectLaunchOption(isActivityRecreated: Boolean) {
        val analysisId = getAnalysisIdFromIntent()
        if (analysisId != null && !isActivityRecreated) {
            presenter.onViewCreated(analysisId)
        }
    }

    private fun getAnalysisIdFromIntent(): Int? {
        return intent.extras?.getInt(EXTRA_ANALYSIS_ID)
    }

    private fun setupRecyclerView() {
        adapter = WordListAdapter()
        adapter.setOnItemClickListener {
            binding.bottomPanel.root.visibility =
                if (binding.bottomPanel.root.visibility == View.VISIBLE) {
                    View.INVISIBLE
                } else {
                    View.VISIBLE
                }
        }
        binding.wordList.adapter = adapter
        binding.wordList.layoutManager = LinearLayoutManager(this)
    }

    private fun setupToolBar() {
        binding.toolbar.title = resources.getString(R.string.word_list_activity_title)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(binding.toolbar)
    }

    private fun setupSeekBar() {
        binding.bottomPanel.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
        binding.wordList.scrollToPosition(position - 1)
    }

    override fun setPositionViewText(text: String) {
        binding.bottomPanel.positionTextView.text = text
    }

    override fun setSeekBarMaxValue(maxValue: Int) {
        binding.bottomPanel.seekBar.max = maxValue
    }

    override fun setupWordItems(wordItems: ArrayList<WordListItem>) {
        adapter.setupData(wordItems)
    }

    override fun finishActivity() {
        finish()
    }
}
