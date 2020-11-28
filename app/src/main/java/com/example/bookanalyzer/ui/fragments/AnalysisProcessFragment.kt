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
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ActivityLoaderScreenBinding
import com.example.bookanalyzer.domain.repositories.LoaderScreenRepository
import com.example.bookanalyzer.view_models.AnalysisProcessViewModel
import com.example.bookanalyzer.view_models.AnalysisProcessViewModelFactory
import com.example.bookanalyzer.ui.EXTRA_BOOK_PATH
import javax.inject.Inject

class AnalysisProcessFragment() : Fragment() {

    companion object {
        fun newInstance(path: String) = AnalysisProcessFragment().apply {
            arguments = Bundle().apply {
                putString(EXTRA_BOOK_PATH, path)
            }
        }
    }

    private val bookPath: String? by lazy {
        arguments?.getString(EXTRA_BOOK_PATH)
    }

    private var interaction: ProcessFragmentInteraction? = null

    private var binding: ActivityLoaderScreenBinding? = null

    @Inject
    lateinit var repository: LoaderScreenRepository

    private lateinit var viewModel: AnalysisProcessViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interaction = context as? ProcessFragmentInteraction
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        MyApp.appComponent.inject(this)
        val viewModelFactory = AnalysisProcessViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(
            AnalysisProcessViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityLoaderScreenBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()
        setupObservers()
        bookPath?.let {
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

    private fun setupObservers() {
        viewModel.analysisToShow.observe(viewLifecycleOwner, Observer { analysisId ->
            if (analysisId == null) {
                return@Observer
            }
            interaction?.onAnalysisFinished(analysisId)
        })

        viewModel.isFragmentFinishRequired.observe(viewLifecycleOwner, Observer { isRequired ->
            if (isRequired == null) {
                return@Observer
            }
            if (isRequired) {
                activity?.onBackPressed()
            }
        })
    }

    private fun setupToolBar() {
        binding?.toolbar?.title = resources.getString(R.string.loader_activity_title)
        binding?.toolbar?.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding?.toolbar)
    }

    interface ProcessFragmentInteraction {
        fun onAnalysisFinished(analysisId: Int)
    }
}