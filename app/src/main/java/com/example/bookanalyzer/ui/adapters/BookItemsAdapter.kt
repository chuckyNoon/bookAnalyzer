package com.example.bookanalyzer.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_book.view.*
import java.io.File

data class BookItem(
    var path: String,
    var title: String,
    var author: String,
    var format: String,
    var imgPath: String?,
    var uniqueWordCount: String,
    var barProgress: Int,
    var analysisId: Int,
)

class BookItemsAdapter(
    private val ctx: Context,
    private val presenter: StartScreenPresenter
) : RecyclerView.Adapter<BookItemsAdapter.ItemViewHolder>() {

    private val defaultBookImage = ResourcesCompat.getDrawable(ctx.resources, R.drawable.book, null)

    private val diffUtilCallback = object : DiffUtil.ItemCallback<BookItem>() {
        override fun areItemsTheSame(oldItem: BookItem, newItem: BookItem): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: BookItem, newItem: BookItem): Boolean {
            return when {
                oldItem.path != newItem.path -> false
                oldItem.author != newItem.author -> false
                oldItem.title != newItem.title -> false
                oldItem.imgPath != newItem.imgPath -> false
                oldItem.uniqueWordCount != newItem.uniqueWordCount -> false
                oldItem.format != newItem.format -> false
                else -> true
            }
        }
    }

    private val differ = AsyncListDiffer(this, diffUtilCallback)

    fun setupBooks(bookItems: ArrayList<BookItem>) {
        differ.submitList(bookItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val book = differ.currentList[position]

        bindTextViews(holder, book)
        bindProgressBar(holder, book)
        bindImage(holder, book)
        bindOnTouchListener(holder, book)
    }

    private fun bindTextViews(holder: ItemViewHolder, book: BookItem) {
        holder.bookTitleView.text = book.title
        holder.bookAuthorView.text = book.author
        holder.wordCountView.text = book.uniqueWordCount
        holder.bookFormatView.text = book.format
    }

    private fun bindProgressBar(holder: ItemViewHolder, book: BookItem) {
        holder.progressBar.apply {
            max = 20000
            progress = book.barProgress
        }
    }

    private fun bindImage(holder: ItemViewHolder, book: BookItem) {
        if (defaultBookImage != null) {
            book.imgPath?.let { imgPath ->
                Picasso.get()
                    .load(File(ctx.filesDir, imgPath))
                    .into(holder.bookImage)
            } ?: run {
                holder.bookImage.setImageDrawable(defaultBookImage)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindOnTouchListener(holder: ItemViewHolder, book: BookItem) {
        holder.foregroundView.setOnTouchListener { view, event ->
            val itemBackgroundTransition = view.background as TransitionDrawable
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    (true)
                }
                MotionEvent.ACTION_CANCEL -> {
                    (true)
                }
                MotionEvent.ACTION_UP -> {
                    itemBackgroundTransition.startTransition(200)
                    presenter.onBookClicked(holder.adapterPosition)
                    itemBackgroundTransition.reverseTransition(200)
                    (true)
                }
                else -> {
                    (false)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        ItemTouchHelperViewHolder {

        val foregroundView: View = view.view_foreground
        val bookTitleView: TextView = view.bookNameView
        val bookAuthorView: TextView = view.bookAuthorView
        val wordCountView: TextView = view.wordCountView
        val bookFormatView: TextView = view.bookFormatView
        val progressBar: ProgressBar = view.progressBar2
        val bookImage: ImageView = view.bookImage

        override fun onItemSelected() {
            val backgroundTransition = foregroundView.background as TransitionDrawable
            backgroundTransition.startTransition(200)
        }

        override fun onItemClear() {
            val backgroundTransition = foregroundView.background as TransitionDrawable
            backgroundTransition.reverseTransition(100)
        }
    }
}
