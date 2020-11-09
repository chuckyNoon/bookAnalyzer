package com.example.bookanalyzer.ui.adapters.book_items_adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ItemBookBinding
import com.squareup.picasso.Picasso
import java.io.File

class BooksAdapter(
    private val appFilesDir: File,
    private val defaultBookImage: Drawable?,
    private val interaction: BookInteraction
) : RecyclerView.Adapter<BooksAdapter.BookHolder>() {

    private val diffUtilCallback = object : DiffUtil.ItemCallback<BookCell>() {
        override fun areItemsTheSame(oldItem: BookCell, newItem: BookCell): Boolean {
            return oldItem.filePath == newItem.filePath
        }

        override fun areContentsTheSame(oldItem: BookCell, newItem: BookCell): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffUtilCallback)

    fun setupBooks(bookListCells: ArrayList<BookCell>) {
        differ.submitList(bookListCells)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BookHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false),
            defaultBookImage,
            appFilesDir,
            interaction
        )

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

    class BookHolder(
        private val view: View,
        private val defaultBookImage: Drawable?,
        private val appFilesDir: File,
        private val bookInteraction: BookInteraction
    ) :
        RecyclerView.ViewHolder(view),
        ItemTouchHelperViewHolder {

        val binding = ItemBookBinding.bind(view)

        init {
            setupOnTouchListener()
        }

        fun bind(cell: BookCell) {
            bindTextViews(cell)
            bindProgressBar(cell)
            bindImage(cell)
        }

        override fun onItemSelected() {
            val backgroundTransition = binding.foregroundView.background as TransitionDrawable
            backgroundTransition.startTransition(200)
        }

        override fun onItemClear() {
            val backgroundTransition = binding.foregroundView.background as TransitionDrawable
            backgroundTransition.reverseTransition(100)
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun setupOnTouchListener() {
            binding.foregroundView.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        (true)
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        (true)
                    }
                    MotionEvent.ACTION_UP -> {
                        bookInteraction.onBookClicked(view, adapterPosition)
                        (true)
                    }
                    else -> {
                        (false)
                    }
                }
            }
        }

        private fun bindTextViews(cell: BookCell) {
            binding.bookNameView.text = cell.title
            binding.wordCountView.text = cell.uniqueWordCount
            binding.bookFormatView.text = cell.format
            binding.bookAuthorView.text = cell.author
        }

        private fun bindProgressBar(book: BookCell) {
            binding.progressBar.apply {
                max = 20000
                progress = book.barProgress
            }
        }

        private fun bindImage(book: BookCell) {
            if (defaultBookImage != null) {
                book.imgPath?.let { imgPath ->
                    Picasso.get()
                        .load(appFilesDir.resolve(imgPath))
                        .into(binding.bookImage)
                } ?: run {
                    binding.bookImage.setImageDrawable(defaultBookImage)
                }
            }
        }
    }

    interface BookInteraction {
        fun onBookClicked(view: View, position: Int)
    }
}
