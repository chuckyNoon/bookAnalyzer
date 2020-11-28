package com.example.bookanalyzer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ActivityWordListBinding
import com.example.bookanalyzer.domain.repositories.WordListRepository
import com.example.bookanalyzer.view_models.WordsViewModel
import com.example.bookanalyzer.view_models.WordsViewModelFactory
import com.example.bookanalyzer.ui.EXTRA_ANALYSIS_ID
import com.example.bookanalyzer.ui.adapters.word_list_adapter.WordsAdapter
import javax.inject.Inject

class WordsFragment() : Fragment(){

    companion object {
        fun newInstance(id: Int) = WordsFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_ANALYSIS_ID, id)
            }
        }
    }

    @Inject
    lateinit var repository: WordListRepository

    private lateinit var viewModel: WordsViewModel

    private val analysisId: Int? by lazy {
        arguments?.getInt(EXTRA_ANALYSIS_ID)
    }

    private var binding: ActivityWordListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        MyApp.appComponent.inject(this)
        val viewModelFactory = WordsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(
            WordsViewModel::class.java)
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
        setupObservers()
        analysisId?.let{
            viewModel.onViewCreated(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            viewModel.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
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
                viewModel.onProgressChanged(seekBar?.progress ?: 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun setupObservers() {
        viewModel.wordCells.observe(viewLifecycleOwner, Observer { cells ->
            if (cells == null) {
                return@Observer
            }
            val adapter = binding?.wordsRecycler?.adapter as? WordsAdapter
            if (adapter == null) {
                val newAdapter = WordsAdapter(wordInteraction)
                newAdapter.setupCells(cells)
                binding?.wordsRecycler?.adapter = newAdapter
            } else {
                adapter.setupCells(cells)
            }
        })

        viewModel.cursorPosition.observe(viewLifecycleOwner, Observer { position ->
            if (position == null) {
                return@Observer
            }
            binding?.wordsRecycler?.scrollToPosition(position - 1)
        })

        viewModel.bottomPanelViewState.observe(viewLifecycleOwner, Observer { panelState ->
            if (panelState == null) {
                return@Observer
            }
            binding?.bottomPanel?.apply {
                seekBar.max = panelState.seekBarMaxValue
                positionTextView.text = panelState.text
                root.isVisible = panelState.isVisible
            }
        })

        viewModel.isFragmentFinishRequired.observe(viewLifecycleOwner, Observer { isRequired->
            if (isRequired == null){
                return@Observer
            }
            if (isRequired){
                activity?.onBackPressed()
            }
        })

    }

    private val wordInteraction = object : WordsAdapter.WordInteraction {
        override fun onClick() {
            viewModel.onWordClicked()
        }
    }
}