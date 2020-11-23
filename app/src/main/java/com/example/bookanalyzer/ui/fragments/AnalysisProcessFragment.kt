package com.example.bookanalyzer.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ActivityLoaderScreenBinding
import com.example.bookanalyzer.domain.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.presenters.LoaderScreenPresenter
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import com.example.bookanalyzer.ui.EXTRA_BOOK_PATH
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class AnalysisProcessFragment() : MvpAppCompatFragment(), LoaderScreenView {

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

    private lateinit var binding: ActivityLoaderScreenBinding

    @Inject
    lateinit var repository: LoaderScreenRepository

    @InjectPresenter
    lateinit var presenter: LoaderScreenPresenter

    @ProvidePresenter
    fun provideStartScreenPresenter(): LoaderScreenPresenter {
        MyApp.appComponent.inject(this)
        return LoaderScreenPresenter(repository)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interaction = context as? ProcessFragmentInteraction
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
        binding = ActivityLoaderScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()
        bookPath?.let {
            presenter.onViewCreated(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            presenter.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun goToAnalysisActivity(analysisId: Int) {
        interaction?.onAnalysisFinished(analysisId)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun finishActivity() {
        activity?.onBackPressed()
    }

    private fun setupToolBar() {
        binding.toolbar.title = resources.getString(R.string.loader_activity_title)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    interface ProcessFragmentInteraction {
        fun onAnalysisFinished(analysisId: Int)
    }
}