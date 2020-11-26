package com.example.bookanalyzer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ActivityWordListBinding
import com.example.bookanalyzer.domain.repositories.WordListRepository
import com.example.bookanalyzer.mvp.presenters.WordListPresenter
import com.example.bookanalyzer.mvp.views.WordListView
import com.example.bookanalyzer.ui.EXTRA_ANALYSIS_ID
import com.example.bookanalyzer.ui.adapters.word_list_adapter.WordCell
import com.example.bookanalyzer.ui.adapters.word_list_adapter.WordsAdapter
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class WordsFragment() : MvpAppCompatFragment(), WordListView {

    companion object {
        fun newInstance(id: Int) = WordsFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_ANALYSIS_ID, id)
            }
        }
    }

    private val analysisId: Int? by lazy {
        arguments?.getInt(EXTRA_ANALYSIS_ID)
    }

    private var binding: ActivityWordListBinding? = null

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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityWordListBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()
        setupRecyclerView()
        setupSeekBar()
        analysisId?.let {
            presenter.onViewCreated(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            presenter.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun scrollToPosition(position: Int) {
        binding?.wordsRecycler?.scrollToPosition(position - 1)
    }

    override fun setPositionViewText(text: String) {
        binding?.bottomPanel?.positionTextView?.text = text
    }

    override fun setSeekBarMaxValue(maxValue: Int) {
        binding?.bottomPanel?.seekBar?.max = maxValue
    }

    override fun setupCells(wordCells: ArrayList<WordCell>) {
        val adapter = binding?.wordsRecycler?.adapter as WordsAdapter
        adapter.setupCells(wordCells)
    }

    override fun finishActivity() {
        activity?.onBackPressed()
    }

    private fun setupRecyclerView() {
        val adapter = WordsAdapter(wordInteraction)
        binding?.wordsRecycler?.adapter = adapter
        binding?.wordsRecycler?.layoutManager = LinearLayoutManager(context)
        binding?.wordsRecycler?.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    private fun setupToolBar() {
        binding?.toolbar?.title = resources.getString(R.string.word_list_activity_title)
        binding?.toolbar?.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding?.toolbar)
    }

    private fun setupSeekBar() {
        binding?.bottomPanel?.seekBar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter.onProgressChanged(seekBar?.progress ?: 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private val wordInteraction = object : WordsAdapter.WordInteraction {
        override fun onClick() {
            binding?.bottomPanel?.root?.visibility =
                if (binding?.bottomPanel?.root?.visibility == View.VISIBLE) {
                    View.INVISIBLE
                } else {
                    View.VISIBLE
                }
        }
    }
}