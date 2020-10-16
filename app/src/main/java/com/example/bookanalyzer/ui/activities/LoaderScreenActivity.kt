package com.example.bookanalyzer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.presenters.LoaderScreenPresenter
import com.example.bookanalyzer.mvp.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter


class LoaderScreenActivity : MvpAppCompatActivity(), LoaderScreenView {
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar

    private val repository = LoaderScreenRepository(this)
    private val presenter by moxyPresenter { LoaderScreenPresenter(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader_screen)

        initFields()
        setToolBar()
        selectLaunchOption(savedInstanceState != null)
    }

    private fun getPathFromIntent(): String? {
        return intent.extras?.getString("path")
    }

    private fun getBookIndFromIntent(): Int? {
        return intent.extras?.getInt("ind")
    }

    private fun selectLaunchOption(isActivityRecreated: Boolean) {
        val path = getPathFromIntent()
        val bookInd = getBookIndFromIntent()
        if (path != null && bookInd != null && !isActivityRecreated) {
            presenter.onViewCreated(bookInd, path)
        }
    }

    private fun initFields() {
        toolBar = findViewById(R.id.toolbar)
    }

    private fun setToolBar() {
        toolBar.title = ""
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            presenter.onOptionsItemBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun goToInfoActivity(bookInd: Int) {
        val intent = Intent(this@LoaderScreenActivity, BookInfoActivity::class.java).apply {
            putExtra("ind", bookInd)
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