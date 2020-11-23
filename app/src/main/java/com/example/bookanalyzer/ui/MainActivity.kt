package com.example.bookanalyzer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ActivityMainBinding
import com.example.bookanalyzer.ui.fragments.AnalysisProcessFragment
import com.example.bookanalyzer.ui.fragments.AnalysisResultFragment
import com.example.bookanalyzer.ui.fragments.BooksFragment
import com.example.bookanalyzer.ui.fragments.WordsFragment
import moxy.MvpAppCompatActivity

const val EXTRA_BOOK_PATH = "BookPath"
const val EXTRA_ANALYSIS_ID = "AnalysisId"

class MainActivity() :
    MvpAppCompatActivity(),
    AnalysisProcessFragment.ProcessFragmentInteraction,
    AnalysisResultFragment.ResultFragmentInteraction,
    BooksFragment.BooksFragmentInteraction {

    companion object {
        const val TAG_BOOKS_FRAGMENT = "1"
        const val TAG_PROCESS_FRAGMENT = "2"
        const val TAG_RESULT_FRAGMENT = "3"
        const val TAG_WORD_LIST_FRAGMENT = "4"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            startBooksFragment()
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.popBackStack()
        if (getPrevFragment() is AnalysisProcessFragment) {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onAnalysisFinished(analysisId: Int) {
        startAnalysisResultFragment(analysisId)
    }

    override fun onWordListButtonClicked(analysisId: Int) {
        startWordsFragment(analysisId)
    }

    override fun onNotAnalyzedBookClicked(bookPath: String) {
        startAnalysisProcessFragment(bookPath)
    }

    override fun onAnalyzedBookClicked(analysisId: Int) {
        startAnalysisResultFragment(analysisId)
    }

    private fun startBooksFragment() {
        val fragment = BooksFragment()
        replaceFragment(fragment, TAG_BOOKS_FRAGMENT)
    }

    private fun startAnalysisProcessFragment(path: String) {
        val fragment = AnalysisProcessFragment.newInstance(path)
        replaceFragment(fragment, TAG_PROCESS_FRAGMENT)
    }

    private fun startAnalysisResultFragment(analysisId: Int) {
        val fragment = AnalysisResultFragment.newInstance(analysisId)
        replaceFragment(fragment, TAG_RESULT_FRAGMENT)
    }

    private fun startWordsFragment(analysisId: Int){
        val fragment = WordsFragment.newInstance(analysisId)
        replaceFragment(fragment, TAG_WORD_LIST_FRAGMENT)
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    private fun getPrevFragment(): Fragment? {
        supportFragmentManager.run {
            if (backStackEntryCount < 2) {
                return null
            }
            val fragmentTag = getBackStackEntryAt(backStackEntryCount - 2).name
            return findFragmentByTag(fragmentTag)
        }
    }
}

