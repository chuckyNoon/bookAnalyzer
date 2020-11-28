package com.example.bookanalyzer.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.databinding.ActivityBookAnalysisBinding
import com.example.bookanalyzer.domain.repositories.BookAnalysisRepository
import com.example.bookanalyzer.view_models.AnalysisResultViewModel
import com.example.bookanalyzer.view_models.AnalysisResultViewModelFactory
import com.example.bookanalyzer.ui.EXTRA_ANALYSIS_ID
import com.example.bookanalyzer.ui.adapters.analysis_params_adapter.AnalysisParamsAdapter
import javax.inject.Inject

class AnalysisResultFragment : Fragment() {

    companion object {
        fun newInstance(id: Int) = AnalysisResultFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_ANALYSIS_ID, id)
            }
        }
    }

    val analysisId: Int? by lazy {
        arguments?.getInt(EXTRA_ANALYSIS_ID)
    }

    private var interaction: ResultFragmentInteraction? = null

    private var binding: ActivityBookAnalysisBinding? = null

    @Inject
    lateinit var resourceManager: ResourceManager

    @Inject
    lateinit var repository: BookAnalysisRepository

    private lateinit var viewModel:AnalysisResultViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interaction = context as? ResultFragmentInteraction
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        MyApp.appComponent.inject(this)
        val viewModelFactory = AnalysisResultViewModelFactory(repository, resourceManager)
        viewModel = ViewModelProvider(this, viewModelFactory).get(
            AnalysisResultViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityBookAnalysisBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()
        setupRecyclerView()
        setupObservers()
        analysisId?.let {
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

    private fun setupToolBar() {
        binding?.toolbar?.title = resources.getString(R.string.analysis_activity_title)
        binding?.toolbar?.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding?.toolbar)
    }

    private fun setupRecyclerView() {
        binding?.analysisParamsRecycler?.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun setupObservers(){
        viewModel.cells.observe(viewLifecycleOwner, Observer {cells->
            if(cells == null){
                return@Observer
            }
            val adapter = binding?.analysisParamsRecycler?.adapter as? AnalysisParamsAdapter
            if (adapter == null){
                val newAdapter = AnalysisParamsAdapter(wordListButtonInteraction)
                newAdapter.setupCells(cells)
                binding?.analysisParamsRecycler?.adapter = newAdapter
            }else{
                adapter.setupCells(cells)
            }
        })

        viewModel.isFragmentFinishRequired.observe(viewLifecycleOwner, Observer { isRequired->
            if (isRequired == null){
                return@Observer
            }
            if(isRequired){
                activity?.onBackPressed()
            }
        })

        viewModel.wordListToShow.observe(viewLifecycleOwner, Observer { analysisId->
            if (analysisId == null){
                return@Observer
            }
            interaction?.onWordListButtonClicked(analysisId)
        })
    }

    private val wordListButtonInteraction =
        object : AnalysisParamsAdapter.WordListButtonInteraction {
            override fun onButtonClicked() {
                analysisId?.let {
                    viewModel.onWordListButtonClicked(it)
                }
            }
        }

    interface ResultFragmentInteraction {
        fun onWordListButtonClicked(analysisId: Int)
    }
}