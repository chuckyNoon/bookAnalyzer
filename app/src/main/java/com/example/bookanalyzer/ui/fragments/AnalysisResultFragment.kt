package com.example.bookanalyzer.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.databinding.ActivityBookAnalysisBinding
import com.example.bookanalyzer.domain.repositories.BookAnalysisRepository
import com.example.bookanalyzer.mvp.presenters.BookAnalysisPresenter
import com.example.bookanalyzer.mvp.views.BookAnalysisView
import com.example.bookanalyzer.ui.EXTRA_ANALYSIS_ID
import com.example.bookanalyzer.ui.adapters.analysis_params_adapter.AbsAnalysisCell
import com.example.bookanalyzer.ui.adapters.analysis_params_adapter.AnalysisParamsAdapter
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class AnalysisResultFragment : MvpAppCompatFragment(), BookAnalysisView {

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

    private lateinit var binding: ActivityBookAnalysisBinding

    @Inject
    lateinit var resourceManager: ResourceManager

    @Inject
    lateinit var repository: BookAnalysisRepository

    @InjectPresenter
    lateinit var presenter: BookAnalysisPresenter

    @ProvidePresenter
    fun provideBookAnalysisPresenter(): BookAnalysisPresenter {
        MyApp.appComponent.inject(this)
        return BookAnalysisPresenter(repository, resourceManager)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interaction = context as? ResultFragmentInteraction
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
        binding = ActivityBookAnalysisBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()
        setupRecyclerView()
        analysisId?.let {
            presenter.onViewCreated(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            presenter.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setupCells(cells: ArrayList<AbsAnalysisCell>) {
        val adapter = binding.analysisParamsRecycler.adapter as AnalysisParamsAdapter
        adapter.setupCells(cells)
    }

    override fun startWordListActivity(analysisId: Int) {
        interaction?.onWordListButtonClicked(analysisId)
    }

    override fun finishActivity() {
        activity?.onBackPressed()
    }

    private fun setupToolBar() {
        binding.toolbar.title = resources.getString(R.string.analysis_activity_title)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        val adapter = AnalysisParamsAdapter(wordListButtonInteraction)
        val layoutManager = LinearLayoutManager(context)
        val recycler = binding.analysisParamsRecycler
        recycler.adapter = adapter
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(
            DividerItemDecoration(
                context,
                layoutManager.orientation
            )
        )
    }

    private val wordListButtonInteraction =
        object : AnalysisParamsAdapter.WordListButtonInteraction {
            override fun onButtonClicked() {
                analysisId?.let {
                    presenter.onWordListButtonClicked(it)
                }
            }
        }

    interface ResultFragmentInteraction {
        fun onWordListButtonClicked(analysisId: Int)
    }
}