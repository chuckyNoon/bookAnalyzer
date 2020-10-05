package com.example.bookanalyzer.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.MenuBookModel
import com.example.bookanalyzer.interfaces.ItemTouchHelperViewHolder
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import kotlinx.android.synthetic.main.book_list_elem.view.*


class BookListAdapter(private val defBitmap:Bitmap,private val presenter:StartScreenPresenter) :
    RecyclerView.Adapter<BookListAdapter.ItemViewHolder>(){

    private val diffUtilCallback = object : DiffUtil.ItemCallback<MenuBookModel>(){
        override fun areItemsTheSame(oldItem: MenuBookModel, newItem: MenuBookModel): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: MenuBookModel, newItem: MenuBookModel): Boolean {
           return when{
                oldItem.path != newItem.path -> false
                oldItem.author != newItem.author ->false
                oldItem.name != newItem.name->false
                oldItem.bitmap != newItem.bitmap->false
                oldItem.wordCount != newItem.wordCount->false
                oldItem.selected != newItem.selected ->false
                else -> true
           }
        }
    }

    private val differ = AsyncListDiffer(this, diffUtilCallback)

    fun setupBooks(arrayList: ArrayList<MenuBookModel>){
        differ.submitList(arrayList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.book_list_elem, parent, false)
        return ItemViewHolder(view)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val book = differ.currentList[position]

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
                    presenter.onBookClicked(holder.adapterPosition)
                    tr.reverseTransition(200)
                    true
                }
                else ->{
                    false
                }
            }
        }
        view.bookNameView.text = if (!book.name.isNullOrEmpty()) book.name else relPath
        view.bookAuthorView.text = if (!book.author.isNullOrEmpty()) book.author else "Unknown"
        view.wordCountView.text = (if (book.wordCount != 0) book.wordCount.toString() else "?") + " words"
        view.bookFormatView.text = bookFormat
        view.bookImage.setImageBitmap(book.bitmap ?: defBitmap)
        view.progressBar2.apply {
            max = 20000
            progress = book.wordCount
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
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

