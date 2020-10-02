package com.example.bookanalyzer.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.bookanalyzer.R
import com.example.bookanalyzer.data.AnalyzedInfoSaver
import com.example.bookanalyzer.mvp.presenters.BookInfoPresenter
import com.example.bookanalyzer.mvp.presenters.LoaderScreenPresenter
import com.example.bookanalyzer.mvp.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.io.*


class LoaderScreenActivity : MvpAppCompatActivity(),LoaderScreenView {
    private val repository = LoaderScreenRepository(this)
    //private val presenter = LoaderScreenPresenter(this, repository)
    private val presenter by moxyPresenter{ LoaderScreenPresenter(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader_screen)
        setToolBar()

        val arguments = intent.extras
        val path = arguments?.getString("path")


        if (path == null){
            finish()
        }else {
            val bookInd = arguments.getInt("ind")
            try {
                val inStream = FileInputStream(path)
                presenter.onViewCreated(bookInd, inStream, path)
            }catch(e:IOException){
                println("here")
            }
        }
    }

    private fun setToolBar(){
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolBar?.title = ""
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home ){
            presenter.onOptionsItemSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finishActivity(){
        finish()
    }

    override fun goToInfoActivity(bookInd:Int) {
        val intent = Intent(this@LoaderScreenActivity, BookInfoActivity::class.java)
        intent.putExtra("ind", bookInd)

        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }
}