package com.example.bookanalyzer.ui.adapters.book_items_adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ItemBookBinding
import com.example.bookanalyzer.mvp.presenters.StartScreenPresenter
import com.squareup.picasso.Picasso
import java.io.File

class BookItemsAdapter(
    private val ctx: Context,
    private val presenter: StartScreenPresenter,
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val book = differ.currentList[position]

        bindTextViews(holder, book)
        bindProgressBar(holder, book)
        bindImage(holder, book)
        bindOnTouchListener(holder)
    }

    override fun getItemCount() = differ.currentList.size

    private fun bindTextViews(holder: ItemViewHolder, book: BookItem) {
        holder.binding.bookNameView.text = book.title
        holder.binding.bookAuthorView.text = book.author
        holder.binding.wordCountView.text = book.uniqueWordCount
        holder.binding.bookFormatView.text = book.format
    }

    private fun bindProgressBar(holder: ItemViewHolder, book: BookItem) {
        holder.binding.progressBar.apply {
            max = 20000
            progress = book.barProgress
        }
    }

    private fun bindImage(holder: ItemViewHolder, book: BookItem) {
        if (defaultBookImage != null) {
            book.imgPath?.let { imgPath ->
                Picasso.get()
                    .load(File(ctx.filesDir, imgPath))
                    .into(holder.binding.bookImage)
            } ?: run {
                holder.binding.bookImage.setImageDrawable(defaultBookImage)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindOnTouchListener(holder: ItemViewHolder) {
        holder.binding.foregroundView.setOnTouchListener { view, event ->
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

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        ItemTouchHelperViewHolder {

        val binding = ItemBookBinding.bind(view)

        override fun onItemSelected() {
            val backgroundTransition = binding.foregroundView.background as TransitionDrawable
            backgroundTransition.startTransition(200)
        }

        override fun onItemClear() {
            val backgroundTransition = binding.foregroundView.background as TransitionDrawable
            backgroundTransition.reverseTransition(100)
        }
    }
}
