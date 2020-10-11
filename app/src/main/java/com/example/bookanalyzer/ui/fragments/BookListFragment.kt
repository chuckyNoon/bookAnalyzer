package com.example.bookanalyzer.ui.fragments

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.MenuBookModel
import com.example.bookanalyzer.R
import com.example.bookanalyzer.common.FileUtils
import com.example.bookanalyzer.interfaces.SimpleItemTouchHelperCallback
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import com.example.bookanalyzer.mvp.repositories.StartScreenRepository
import com.example.bookanalyzer.mvp.views.StartView
import com.example.bookanalyzer.ui.activities.*
import com.example.bookanalyzer.ui.adapters.BookListAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File
import java.lang.ClassCastException



class BookListFragment : MvpAppCompatFragment(), StartView {
    private lateinit var listView: RecyclerView
    private lateinit var loadingStateTextView: TextView
    private lateinit var adapter: BookListAdapter

    private lateinit var repository:StartScreenRepository
    private val presenter by moxyPresenter { StartScreenPresenter() }
    private var mCallback:IOnBookClicked?=null
    private var mode = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            mCallback = activity as IOnBookClicked
        }catch (e:ClassCastException){
            println("doesnt support")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFields(view)
        setRecyclerView()
        repository = StartScreenRepository(requireContext())
        presenter.setRepository(repository)
    }


    private fun initFields(view: View){
        listView = view.findViewById(R.id.list_view)
        loadingStateTextView =  view.findViewById(R.id.textview_loading_state)
    }

    private fun setRecyclerView() {
        val defaultBookImage = BitmapFactory.decodeResource(activity?.resources, R.drawable.book)
        listView.setHasFixedSize(true)
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(presenter)
        val mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(listView)

        adapter = BookListAdapter(defaultBookImage, presenter)
        listView.adapter = adapter
        listView.layoutManager = LinearLayoutManager(context)
    }

    fun loadMode(mode:Int){
        this.mode = mode
    }

    fun loadContentInFragment(){
        presenter.onViewCreated()
    }

    override fun showList(bookList: ArrayList<MenuBookModel>) {
        adapter.setupBooks(bookList)
    }

    override fun startLoadingActivity(bookPath: String, newBookInd: Int) {
        if (mode == 0){
            val intent = Intent(context, LoaderScreenActivity::class.java)
            intent.putExtra("path", bookPath)
            intent.putExtra("ind", newBookInd)
            activity?.startActivity(intent)
        }else{
            mCallback?.onNewBookClicked(bookPath, newBookInd)
        }
    }

    override fun startInfoActivity(bookInd: Int) {
        if (mode == 0){
            val intentToBook = Intent(context, BookInfoActivity::class.java)
            intentToBook.putExtra("ind", bookInd)
            activity?.startActivity(intentToBook)
        }else{
            mCallback?.onAnalyzedBookClicked(bookInd)
        }
    }

    fun updateWordCount(){
        presenter.onResume()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun hideLoadingStateView(){
        loadingStateTextView.visibility = View.INVISIBLE
    }

    override fun moveLoadingStateViewUp(dur:Int){
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", 0f).apply{
            duration = dur.toLong()
            start()
        }
    }

    override fun moveLoadingStateViewDown(dur:Int){
        val height = loadingStateTextView.height.toFloat()
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", height).apply{
            duration = dur.toLong()
            start()
        }
    }

    override fun showLoadingStateView(){
        loadingStateTextView.visibility = View.VISIBLE
    }

    override fun setLoadingStateViewText(text: String) {
        loadingStateTextView.text = text
    }

    override fun updateLoadingStateView(str: String, downDuration: Long, upDuration: Long) {
        val height = loadingStateTextView.height.toFloat()
        ObjectAnimator.ofFloat(loadingStateTextView, "translationY", height).apply {
            duration = downDuration
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    loadingStateTextView.text = str
                    ObjectAnimator.ofFloat(loadingStateTextView, "translationY", 0f).apply {
                        duration = upDuration
                        start()
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            start()
        }
    }

    fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File){
        presenter.onSelectedSearchSettings(formats,dir)
    }

    fun onResult(bookPath: String) {
        presenter.onActivityResult(bookPath)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BookListFragment().apply {
            }
    }
}