package com.example.bookanalyzer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File
import kotlin.concurrent.thread

class StartActivityPresenter(private val view:IStartContract.View, private val context:Context) : IStartContract.Presenter {
    private var isListCreating = false
    private lateinit var bookList:ArrayList<ABookInfo>
    private var newAdapter:RecyclerListAdapter?= null

    private var repository: IStartContract.Repository = StartActivityRepository(context)
    private var handler = Handler()

    override fun createBookList(){
        isListCreating = true
        thread {
            buildList()
            addDetailsToList()
            isListCreating = false
        }
    }

    private fun buildList(){
        val bookList = repository.getPrimaryList()
        if (newAdapter == null) {
            this.bookList = bookList
            newAdapter = RecyclerListAdapter(context, bookList)
            handler.post {
                view.initRecyclerView(newAdapter!!)
            }
        }
        else {
            val size = this.bookList.size
            this.bookList.clear()
            handler.post {
                newAdapter!!.notifyItemRangeRemoved(0, size)
            }
            this.bookList.addAll(bookList)
            handler.post{
                newAdapter!!.notifyItemRangeInserted(0, bookList.size)
            }
        }
    }

    private fun addDetailsToList(){
        val detailedBookList = repository.getDetailedList()
        if (bookList.size == detailedBookList.size){
            for (i in bookList.indices){
                bookList[i] = detailedBookList[i]
            }
        }
        handler.post {
            newAdapter!!.notifyDataSetChanged()
        }
    }

    private fun addBookToList(data: Intent) {
        val filePath: String? = FileUtils().getPath(context, data.data!!)
        filePath?.let{
            bookList.add(repository.getNewDetailedModel(filePath))
            newAdapter?.notifyItemInserted((bookList.size ?: 0) - 1)
            newAdapter?.notifyDataSetChanged()
            repository.saveBookPath(filePath)
        }
    }

    override fun onSelectedSearchSettings(formats: ArrayList<String>, dir: File) {
        repository.saveAllBookPaths(BookSearch.findAll(dir, formats))
        createBookList()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            view.showSearchSettingsDialog()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) {
        if(item.itemId == android.R.id.home ){
            view.showSideMenu()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            addBookToList(data!!)
        }
    }

    override fun onRestart() {
        /*val menuContentLoader = MenuContentLoader(this)
        for (book in bookList) {
            val newWordCount = menuContentLoader.searchSavedWordCount(book.path)
            if (book.wordCount != newWordCount) {
                book.wordCount = newWordCount
                newAdapter?.notifyDataSetChanged()

            }
        }*/
    }

    private fun createPrimaryList(menuContentLoader:MenuContentLoader){
        /*if (newAdapter != null){
            val oldSize = bookList.size
            bookList.clear()
            newAdapter?.notifyItemRangeRemoved(0, oldSize)
            bookList.addAll(menuContentLoader.firstStage())
            newAdapter?.notifyItemRangeInserted(0, bookList.size)
        }else{
            bookList = menuContentLoader.firstStage()
            newAdapter = RecyclerListAdapter(context,bookList)
            handler.post{
                listView.setHasFixedSize(true)
                val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(newAdapter!!)
                mItemTouchHelper = ItemTouchHelper(callback)
                mItemTouchHelper?.attachToRecyclerView(listView)

                listView.adapter = newAdapter
                listView.layoutManager = LinearLayoutManager(this)

            }
        }*/
        bookList = repository.getPrimaryList()
        newAdapter = RecyclerListAdapter(context, bookList)

        handler.post {
            view.showLoadingStateView()
            view.setLoadingStateViewText("Loading content")
        }

    }

}