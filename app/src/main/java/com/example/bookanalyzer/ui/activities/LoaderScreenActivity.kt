package com.example.bookanalyzer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.example.bookanalyzer.MyApp
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.presenters.LoaderScreenPresenter
import com.example.bookanalyzer.domain.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject


class LoaderScreenActivity : MvpAppCompatActivity(), LoaderScreenView {
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar

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
        setContentView(R.layout.activity_loader_screen)

        initFields()
        setToolBar()
        selectLaunchOption(savedInstanceState != null)
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

    private fun initFields() {
        toolBar = findViewById(R.id.toolbar)
    }

    private fun setToolBar() {
        toolBar.title = resources.getString(R.string.loader_activity_title)
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            presenter.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun goToInfoActivity(analysisId: Int) {
        val intent = Intent(this@LoaderScreenActivity, BookInfoActivity::class.java).apply {
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
}