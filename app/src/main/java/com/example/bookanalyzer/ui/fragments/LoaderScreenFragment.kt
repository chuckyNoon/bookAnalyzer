package com.example.bookanalyzer.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.presenters.LoaderScreenPresenter
import com.example.bookanalyzer.mvp.repositories.LoaderScreenRepository
import com.example.bookanalyzer.mvp.views.LoaderScreenView
import com.example.bookanalyzer.ui.activities.BookInfoActivity
import com.example.bookanalyzer.ui.activities.IOnLoadingCompleted
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.FileInputStream
import java.io.IOException
import java.lang.ClassCastException

private const val ARG_PATH = "param1"
private const val ARG_IND = "param2"

class LoaderScreenFragment : MvpAppCompatFragment(),LoaderScreenView {
    private var path: String? = null
    private var newInd: Int? = null
    private val presenter by moxyPresenter{ LoaderScreenPresenter() }
    private lateinit var repository: LoaderScreenRepository
    private var mCallback:IOnLoadingCompleted?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mCallback = activity as IOnLoadingCompleted
        }catch (e:ClassCastException){
            println("doesnt support")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            path = it.getString(ARG_PATH)
            newInd = it.getInt(ARG_IND)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loader_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = LoaderScreenRepository(requireActivity())
        presenter.setRep(repository)
        if(savedInstanceState == null){
            try {
                val inStream = FileInputStream(path)
                presenter.onViewCreated(newInd!!, inStream, path!!)
            }catch(e: IOException){
                println("here")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(path: String, newInd: Int) =
            LoaderScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PATH, path)
                    putInt(ARG_IND, newInd)
                }
            }
    }

    override fun finishActivity() {

    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun goToInfoActivity(bookInd: Int) {
        if (mCallback == null){
            val intent = Intent(activity, BookInfoActivity::class.java)
            intent.putExtra("ind", bookInd)
            startActivity(intent)
        }else{
            mCallback?.onLoadingCompleted(bookInd)
        }
    }
}