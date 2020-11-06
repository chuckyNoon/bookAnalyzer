package com.example.bookanalyzer.ui.adapters.word_list_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ItemWordListRowBinding

class WordListAdapter : RecyclerView.Adapter<WordListAdapter.MyViewHolder>() {

    private var wordList: ArrayList<WordListItem> = ArrayList()
    private var onItemClickListener: View.OnClickListener? = null

    fun setupData(wordList: ArrayList<WordListItem>) {
        this.wordList = wordList
    }

    fun setOnItemClickListener(listener: View.OnClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_word_list_row, parent, false)
        )

    override fun getItemCount() = wordList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = wordList[position]

        holder.binding.wordView.text = data.word
        holder.binding.frequencyView.text = data.frequency
        holder.binding.indView.text = data.pos
        onItemClickListener?.let {
            holder.binding.root.setOnClickListener(it)
        }
    }

    class MyViewHolder(val view:View) :
        RecyclerView.ViewHolder(view) {
        val binding = ItemWordListRowBinding.bind(view)
    }
}