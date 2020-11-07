package com.example.bookanalyzer.ui.adapters.word_list_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ItemWordBinding

class WordsAdapter(private val interaction: WordInteraction) :
    RecyclerView.Adapter<WordsAdapter.WordHolder>() {

    private var cells: ArrayList<WordCell> = ArrayList()

    fun setupCells(cells: ArrayList<WordCell>) {
        this.cells = cells
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WordHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false),
            interaction
        )

    override fun getItemCount() = cells.size

    override fun onBindViewHolder(holder: WordHolder, position: Int) {
        holder.bind(cells[position])
    }

    class WordHolder(private val view: View, private val interaction: WordInteraction) :
        RecyclerView.ViewHolder(view) {

        val binding = ItemWordBinding.bind(view)

        init {
            view.setOnClickListener {
                interaction.onClick()
            }
        }

        fun bind(cell: WordCell) {
            binding.wordView.text = cell.word
            binding.frequencyView.text = cell.frequency
            binding.indView.text = cell.pos
        }
    }

    interface WordInteraction {
        fun onClick()
    }
}