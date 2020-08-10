package com.example.bookanalyzer

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.book_list_elem.view.*
import java.io.IOException
import kotlin.collections.ArrayList

class MyAdapter(val ctx:Context, val ar:ArrayList<ABookInfo>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    private val defBitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.book)

    private fun isAnalyzed(ind:Int) : Boolean{
        return try{
            val input = ctx.openFileInput("all")
            val strs = input.readBytes().toString(Charsets.UTF_8).split("\n")
            (strs.any{it == ind.toString()})
        }catch (e:IOException){
            println("no")
            (false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_list_elem, parent, false)

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

        view.setOnClickListener {
            if (isAnalyzed(position)){
                val intentToBook = Intent(ctx, MainActivity::class.java)
                intentToBook.putExtra("listPath", "list$position")
                intentToBook.putExtra("imgPath", "img$position")
                intentToBook.putExtra("infoPath", "info$position")
                ctx.startActivity(intentToBook)
            }else{
                val intent = Intent(ctx, LoaderActivity::class.java)
                intent.putExtra("path", book.path )
                intent.putExtra("ind", position)
                ctx.startActivity(intent)
            }
        }
        view.bookNameView.text = if (!book.name.isNullOrEmpty()) book.name else (relPath?:"")
        view.bookAuthorView.text = if (!book.author.isNullOrEmpty()) book.author else "Unknown"
        view.wordCountView.text = (if (book.wordCount != 0) book.wordCount.toString() else "?") + " words"
        view.bookFormatView.text = bookFormat
        view.bookImage.setImageBitmap(book.bitmap?:defBitmap)
        view.progressBar2.apply {
            max = 20000
            progress = book.wordCount
        }
    }

}