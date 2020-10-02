package com.example.bookanalyzer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.R
import kotlinx.android.synthetic.main.word_list_elem.view.*

class WordListElemModel(var word:String, var frequency:String, var pos:String){
}

class WordListAdapter : RecyclerView.Adapter<WordListAdapter.MyViewHolder>() {
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    private lateinit var ar:ArrayList<WordListElemModel>
    private var onItemClickListener:View.OnClickListener? = null

    fun setupData(rowsList:ArrayList<WordListElemModel>){
        ar = rowsList
    }

    fun setOnItemClickListener(listener:View.OnClickListener){
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_list_elem, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return (ar.size)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val view = holder.view
        val data = ar[position]
        view.wordView.text = data.word
        view.frequencyView.text = data.frequency
        view.indView.text = data.pos
        onItemClickListener?.let {
            view.setOnClickListener(it)
        }
    }
}