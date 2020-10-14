package com.example.bookanalyzer.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.interfaces.ItemTouchHelperViewHolder
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_book.view.*
import java.io.File

data class BookListItem(var path:String,
                        var title:String,
                        var author:String,
                        var format:String,
                        var imgPath: String?,
                        var wordCount:String,
                        var barProgress:Int,
                        var id:Int)
{
}

class BookListAdapter(private val ctx:Context, private val defBitmap:Bitmap,private val presenter:StartScreenPresenter) :
    RecyclerView.Adapter<BookListAdapter.ItemViewHolder>(){

    private val diffUtilCallback = object : DiffUtil.ItemCallback<BookListItem>(){
        override fun areItemsTheSame(oldItem: BookListItem, newItem: BookListItem): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: BookListItem, newItem: BookListItem): Boolean {
           return when{
                oldItem.path != newItem.path -> false
                oldItem.author != newItem.author ->false
                oldItem.title != newItem.title->false
                oldItem.imgPath != newItem.imgPath->false
                oldItem.wordCount != newItem.wordCount->false
                oldItem.format != newItem.format->false
                else -> true
           }
        }
    }

    private val differ = AsyncListDiffer(this, diffUtilCallback)

    fun setupBooks(arrayList: ArrayList<BookListItem>){
        differ.submitList(arrayList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ItemViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val book = differ.currentList[position]

        holder.foregroundView.setOnTouchListener { v, event ->
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

        holder.bookTitleView.text = book.title
        holder.bookAuthorView.text = book.author
        holder.wordCountView.text = book.wordCount
        holder.bookFormatView.text = book.format
        if (book.imgPath != null) {
            Picasso.get().load(File(ctx.filesDir, book.imgPath)).into(holder.bookImage)
        }else{
            holder.bookImage.setImageBitmap(defBitmap)
        }

        holder.progressBar.apply {
            max = 20000
            progress = book.barProgress
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        ItemTouchHelperViewHolder {
        val foregroundView:View = view.view_foreground
        val bookTitleView:TextView = view.bookNameView
        val bookAuthorView:TextView = view.bookAuthorView
        val wordCountView:TextView = view.wordCountView
        val bookFormatView:TextView = view.bookFormatView
        val progressBar:ProgressBar = view.progressBar2
        val bookImage:ImageView = view.bookImage

        override fun onItemSelected() {
            val tr = foregroundView.background as TransitionDrawable
            tr.startTransition(200)
        }

        override fun onItemClear() {
            val tr = foregroundView.background as TransitionDrawable
            tr.reverseTransition(100)
        }
    }
}

