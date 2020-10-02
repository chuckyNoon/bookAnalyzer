package com.example.bookanalyzer.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.MenuBookModel
import com.example.bookanalyzer.ui.activities.BookInfoActivity
import com.example.bookanalyzer.ui.activities.LoaderScreenActivity
import com.example.bookanalyzer.interfaces.ItemTouchHelperAdapter
import com.example.bookanalyzer.interfaces.ItemTouchHelperViewHolder
import com.example.bookanalyzer.R
import com.example.bookanalyzer.data.AnalyzedPathsSaver
import com.example.bookanalyzer.data.FoundPathsSaver
import kotlinx.android.synthetic.main.book_list_elem.view.*
import kotlin.concurrent.thread


class BookListAdapter(private val ctx:Context) :
    RecyclerView.Adapter<BookListAdapter.ItemViewHolder>(),
    ItemTouchHelperAdapter {
    private var ar:ArrayList<MenuBookModel> = ArrayList()
    private val defBitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.book)
    private var firstSetup = true

    fun setupBooks(arrayList: ArrayList<MenuBookModel>){
        if (firstSetup){
            ar = arrayList
            firstSetup = false
        }else{
            val oldSize = ar.size
            ar.clear()
            notifyItemRangeRemoved(0, oldSize)
            ar.addAll(arrayList)
            notifyItemRangeInserted(0, ar.size)
        }
    }

    fun addBook(bookModel: MenuBookModel){
        ar.add(bookModel)
        notifyItemInserted(ar.size  - 1)
    }

    fun updateBook(newBookModel:MenuBookModel ){
        val ind =ar.indexOfFirst { it.path == newBookModel.path }
        if (ind >= 0){
            ar[ind] = newBookModel
            notifyDataSetChanged()
        }else{
            addBook(newBookModel)
        }
    }

    fun deleteBook(position: Int){
        ar.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.book_list_elem, parent, false)
        return ItemViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val book = ar[position]

        val relPath = book.path.split("/").last()
        val bookFormat = book.path.split(".").last().toUpperCase()?:""
        val view = holder.view.view_foreground

        view.setOnTouchListener { v, event ->
            val tr = v?.background as TransitionDrawable
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    true
                }
                MotionEvent.ACTION_UP -> {
                    tr.startTransition(200)
                    goToBook(holder.adapterPosition)
                    tr.reverseTransition(200)
                    true
                }
                else ->{
                    false
                }
            }
        }
        view.bookNameView.text = if (!book.name.isNullOrEmpty()) book.name else (relPath?:"")
        view.bookAuthorView.text = if (!book.author.isNullOrEmpty()) book.author else "Unknown"
        view.wordCountView.text = (if (book.wordCount != 0) book.wordCount.toString() else "?") + " words"
        view.bookFormatView.text = bookFormat
        view.bookImage.setImageBitmap(book.bitmap ?: defBitmap)
        view.progressBar2.apply {
            max = 20000
            progress = book.wordCount
        }
    }

    override fun onItemDismiss(position: Int) {
        println(position.toString() + " " + ar[position].path)
        FoundPathsSaver(ctx).deletePath(ar[position].path)
        deleteBook(position)

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val prev = ar.removeAt(fromPosition)
        ar.add(toPosition, prev)
        FoundPathsSaver(ctx).saveAll(ar)

        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemCount(): Int {
        return ar.size
    }

    private fun goToBook(position: Int){
        val book = ar[position]
        val analyzedPathsSaver = AnalyzedPathsSaver(ctx)
        var ind = analyzedPathsSaver.getIndByPath(ar[position].path)
        if (ind != -1){
            val intentToBook = Intent(ctx, BookInfoActivity::class.java)
            intentToBook.putExtra("ind", ind)
            ctx.startActivity(intentToBook)
        }else{
            ind = analyzedPathsSaver.getAnalyzedCount()
            val intent = Intent(ctx, LoaderScreenActivity::class.java)
            intent.putExtra("path", book.path)
            intent.putExtra("ind", ind)
            ctx.startActivity(intent)
        }
    }


    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        ItemTouchHelperViewHolder {
        override fun onItemSelected() {
            val tr = view.view_foreground.background as TransitionDrawable
            tr.startTransition(200)
        }

        override fun onItemClear() {
            val tr = view.view_foreground.background as TransitionDrawable
            tr.reverseTransition(100)
        }
    }
}

