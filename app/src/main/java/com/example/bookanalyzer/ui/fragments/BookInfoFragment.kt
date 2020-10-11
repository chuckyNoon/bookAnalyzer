package com.example.bookanalyzer.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.presenters.BookInfoPresenter
import com.example.bookanalyzer.mvp.repositories.BookInfoRepository
import com.example.bookanalyzer.mvp.views.BookInfoView
import com.example.bookanalyzer.ui.activities.WordListActivity
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

private const val IND_TAG = "ind"

class BookInfoFragment : MvpAppCompatFragment(), BookInfoView {
    private lateinit var bookNameView: TextView
    private lateinit var allWordView: TextView
    private lateinit var textLengthView: TextView
    private lateinit var uniqueWordView: TextView
    private lateinit var avgSentenceViewWrd: TextView
    private lateinit var avgSentenceViewChr: TextView
    private lateinit var avgWordView: TextView

    private lateinit var repository: BookInfoRepository
    private val presenter by moxyPresenter{ BookInfoPresenter() }
    private var ind:Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            ind = it.getInt(IND_TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields(view)
        repository = BookInfoRepository(requireActivity())
        presenter.setRepository(repository)
        view.findViewById<Button>(R.id.toWordListButton)?.setOnClickListener {
            presenter.onWordListButtonClicked(ind?:-1)
        }
        ind?.let {
            presenter.onViewCreated(it)
        }
    }


    private fun initFields(view:View){
        bookNameView = view.findViewById(R.id.bookNameView)
        allWordView = view.findViewById(R.id.allWordCountView)
        uniqueWordView = view.findViewById(R.id.uniqWordView)
        textLengthView = view.findViewById(R.id.allCharsCountView)
        avgSentenceViewWrd = view.findViewById(R.id.avgSentenceLenView1)
        avgSentenceViewChr = view.findViewById(R.id.avgSentenceLenView2)
        avgWordView = view.findViewById(R.id.avgWordLenView)
    }


    override fun setViewsText(
        path: String,
        uniqWordCount: String,
        allWordCount: String,
        allCharsCount: String,
        avgSentenceLenInWrd: String,
        avgSentenceLenInChr: String,
        avgWordLen: String
    ) {
        bookNameView.text = path
        uniqueWordView.text = uniqWordCount
        allWordView.text = allWordCount
        textLengthView.text = allCharsCount
        avgSentenceViewWrd.text = avgSentenceLenInWrd
        avgSentenceViewChr.text = avgSentenceLenInChr
        avgWordView.text = avgWordLen
    }

    override fun startWordListActivity(ind: Int) {
        val newIntent = Intent(activity, WordListActivity::class.java)
        newIntent.putExtra("ind", ind)
        requireActivity().startActivity(newIntent)
    }


    companion object {
        @JvmStatic
        fun newInstance(ind:Int) =
            BookInfoFragment().apply {
                arguments = Bundle().apply {
                    putInt(IND_TAG, ind)
                }
            }
    }
}