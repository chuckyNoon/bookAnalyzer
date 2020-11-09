package com.example.bookanalyzer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.ResourceManager
import com.example.bookanalyzer.databinding.ActivityBookAnalysisBinding
import com.example.bookanalyzer.mvp.presenters.BookAnalysisPresenter
import com.example.bookanalyzer.domain.repositories.BookAnalysisRepository
import com.example.bookanalyzer.mvp.views.BookAnalysisView
import com.example.bookanalyzer.ui.adapters.analysis_params_adapter.AbsAnalysisCell
import com.example.bookanalyzer.ui.adapters.analysis_params_adapter.AnalysisParamsAdapter
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class BookAnalysisActivity : MvpAppCompatActivity(),
    BookAnalysisView {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolBar()
        setupRecyclerView()
        selectLaunchOption(savedInstanceState != null)
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
        val intent = Intent(this, WordListActivity::class.java).apply {
            putExtra(EXTRA_ANALYSIS_ID, analysisId)
        }
        startActivity(intent)
    }

    override fun finishActivity() {
        finish()
    }

    private fun selectLaunchOption(isActivityRecreated: Boolean) {
        val analysisId = getAnalysisIdFromIntent()
        analysisId?.let {
            if (!isActivityRecreated) {
                presenter.onViewCreated(analysisId)
            }
        }
    }

    private fun getAnalysisIdFromIntent(): Int? {
        return intent.extras?.getInt(EXTRA_ANALYSIS_ID)
    }

    private fun setupToolBar() {
        binding.toolbar.title = resources.getString(R.string.analysis_activity_title)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        val adapter = AnalysisParamsAdapter(wordListButtonInteraction)
        val layoutManager = LinearLayoutManager(this)
        val recycler = binding.analysisParamsRecycler
        recycler.adapter = adapter
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                layoutManager.orientation
            )
        )
    }

    private val wordListButtonInteraction =
        object : AnalysisParamsAdapter.WordListButtonInteraction {
            override fun onButtonClicked() {
                val analysisId = getAnalysisIdFromIntent()
                analysisId?.let {
                    presenter.onWordListButtonClicked(analysisId)
                }
            }
        }
}