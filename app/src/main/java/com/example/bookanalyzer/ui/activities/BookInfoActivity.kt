package com.example.bookanalyzer.ui.activities

import android.os.Bundle
import android.view.MenuItem
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.repositories.BookInfoRepository
import com.example.bookanalyzer.ui.fragments.BookInfoFragment
import com.example.bookanalyzer.ui.fragments.BookListFragment
import moxy.MvpAppCompatActivity

class BookInfoActivity : MvpAppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_info)

        setToolBar()
        val arguments = intent.extras
        val ind = arguments?.getInt("ind")

        println(ind)
        if (ind == null) {
            finish()
        } else{
            val f = supportFragmentManager.findFragmentById(R.id.fragment) as BookInfoFragment
            //f.setBookInd(ind)
        }
    }

    private fun setToolBar() {
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolBar?.title = "Info"
        toolBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(toolBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home ){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}