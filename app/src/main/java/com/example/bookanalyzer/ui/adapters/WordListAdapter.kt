package com.example.bookanalyzer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.R
import kotlinx.android.synthetic.main.item_word_list_row.view.*

class WordListItem(var word:String, var frequency:String, var pos:String){
}

class WordListAdapter : RecyclerView.Adapter<WordListAdapter.MyViewHolder>() {
    private var ar:ArrayList<WordListItem> = ArrayList()
    private var onItemClickListener:View.OnClickListener? = null

    fun setupData(rowsList:ArrayList<WordListItem>){
        ar = rowsList
    }

    fun setOnItemClickListener(listener:View.OnClickListener){
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word_list_row, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return (ar.size)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = ar[position]

        holder.wordView.text = data.word
        holder.frequencyView.text = data.frequency
        holder.indView.text = data.pos
        onItemClickListener?.let {
            holder.view.setOnClickListener(it)
        }
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val wordView: TextView = view.wordView
        val frequencyView:TextView = view.frequencyView
        val indView:TextView = view.indView
    }
}