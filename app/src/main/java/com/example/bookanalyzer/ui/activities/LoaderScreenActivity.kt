package com.example.bookanalyzer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ActivityLoaderScreenBinding
import com.example.bookanalyzer.mvp.presenters.LoaderScreenPresenter
import com.example.bookanalyzer.domain.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject


class LoaderScreenActivity : MvpAppCompatActivity(), LoaderScreenView {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoaderScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolBar()
        selectLaunchOption(savedInstanceState != null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            presenter.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun goToAnalysisActivity(analysisId: Int) {
        val intent = Intent(this@LoaderScreenActivity, BookAnalysisActivity::class.java).apply {
            putExtra(EXTRA_ANALYSIS_ID, analysisId)
        }
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun finishActivity() {
        finish()
    }

    private fun getBookPathFromIntent(): String? {
        return intent.extras?.getString(EXTRA_BOOK_PATH)
    }

    private fun selectLaunchOption(isActivityRecreated: Boolean) {
        val path = getBookPathFromIntent()
        if (path != null && !isActivityRecreated) {
            presenter.onViewCreated(path)
        }
    }

    private fun setToolBar() {
        binding.toolbar.title = resources.getString(R.string.loader_activity_title)
        binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(binding.toolbar)
    }
}