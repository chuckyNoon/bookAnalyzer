package com.example.bookanalyzer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.book_list_elem.view.*
import java.io.IOException
import kotlin.concurrent.thread


class RecyclerListAdapter(val ctx: Context, val ar: ArrayList<ABookInfo>) :
    RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>(),
    ItemTouchHelperAdapter {
    private val defBitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.book)

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

        if (holder.isAnimating){
           // val tr = holder.view.background as TransitionDrawable
           // tr.resetTransition()
        }
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
        PathSaver(ctx).deletePath(ar[position].path)

        ar.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val prev = ar.removeAt(fromPosition)
        //ar.add(if (toPosition  > fromPosition) toPosition - 1 else toPosition, prev)
        ar.add(toPosition, prev)
        PathSaver(ctx).saveAll(ar)

        notifyItemMoved(fromPosition, toPosition)
        //notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return ar.size
    }

    private fun goToBook(position: Int){
        val book = ar[position]
        var ind = isAnalyzed(ar[position].path)
        if (ind != -1){
            val intentToBook = Intent(ctx, BookInfoActivity::class.java)
            intentToBook.putExtra("listPath", "list$ind")
            intentToBook.putExtra("imgPath", "img$ind")
            intentToBook.putExtra("infoPath", "info$ind")
            ctx.startActivity(intentToBook)
        }else{
            ind = countFiles()
            val intent = Intent(ctx, LoaderScreenActivity::class.java)
            intent.putExtra("path", book.path)
            intent.putExtra("ind", ind)
            ctx.startActivity(intent)
        }
    }

    private fun isAnalyzed(path: String) : Int{
        var ind = -1
        try{
            val inputStream = ctx.openFileInput(MenuContentLoader.ALL_ANALYZED_PATH)
            val strs = inputStream.readBytes().toString(Charsets.UTF_8).split("\n")
            for (i in strs.indices){
                if (strs[i] == path && i > 0){
                    ind = strs[i - 1].toInt()
                    break
                }
            }
            inputStream.close()
        }catch (e: IOException){
        }
        return ind
    }

    private fun countFiles():Int{
        return try{
            val inputStream = ctx.openFileInput(MenuContentLoader.ALL_ANALYZED_PATH)
            val strs = inputStream.readBytes().toString(Charsets.UTF_8).split("\n")
            inputStream.close()
            strs.size
        }catch (e: IOException){
            0
        }
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view), ItemTouchHelperViewHolder {
        var isAnimating = false

        override fun onItemSelected() {
           // val tr = view.view_foreground.background as TransitionDrawable
           // tr.startTransition(200)
        }

        override fun onItemClear() {
            //val tr = view.view_foreground.background as TransitionDrawable
          //  tr.reverseTransition(100)
        }
    }
}

