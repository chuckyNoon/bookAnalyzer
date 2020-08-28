package com.example.bookanalyzer

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.book_list_elem.view.*
import java.io.IOException


class MyAdapter(val ctx: Context, val ar: ArrayList<ABookInfo>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    private val defBitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.book)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.book_list_elem,
            parent,
            false
        )

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ar.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val book = ar[position]

        val relPath = book.path.split("/").last()
        val bookFormat = book.path.split(".").last().toUpperCase()?:""
        val view = holder.view

        view.setOnTouchListener(MyTouchListener(position))
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

    inner class MyTouchListener(private val position: Int) : View.OnTouchListener{
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val tr = v?.background as TransitionDrawable
            when(event?.action){
                MotionEvent.ACTION_DOWN->{
                    tr.startTransition(500)
                    println("1")
                    return true   
                }
                MotionEvent.ACTION_CANCEL->{
                    println("2")
                    tr.reverseTransition(500)
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    println("3")
                    tr.startTransition(200)
                    goToBook()
                    tr.reverseTransition(200)
                    return true
                }
            }
            return false
        }

        private fun goToBook(){
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


    }
}