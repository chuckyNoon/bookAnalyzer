package com.example.bookanalyzer.ui.adapters.analysis_params_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ItemAnalysisParamBinding
import com.example.bookanalyzer.databinding.ItemListButtonBinding

class AnalysisParamsAdapter(private val buttonInteraction: WordListButtonInteraction) :
    RecyclerView.Adapter<AnalysisParamsAdapter.AbsViewHolder>() {

    enum class ViewType {
        Parameter,
        WordListButton
    }

    private val AbsAnalysisCell.viewType: ViewType
        get() = when (this) {
            is AbsAnalysisCell.Parameter -> ViewType.Parameter
            is AbsAnalysisCell.WordListButton -> ViewType.WordListButton
        }

    private val viewTypeValues = ViewType.values()

    private var cells: ArrayList<AbsAnalysisCell> = ArrayList()

    fun setupCells(cells: ArrayList<AbsAnalysisCell>) {
        this.cells = cells
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = cells[position].viewType.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewTypeOrdinal: Int) =
        when (viewTypeValues[viewTypeOrdinal]) {
            ViewType.Parameter -> ParameterHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_analysis_param, parent, false)
            )
            ViewType.WordListButton -> WordListButtonHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_button, parent, false),
                buttonInteraction
            )
        }


    override fun getItemCount() = cells.size

    override fun onBindViewHolder(holderAbs: AbsViewHolder, position: Int) {
        holderAbs.bind(cells[position])
    }

    abstract class AbsViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        abstract fun bind(cell: AbsAnalysisCell)
    }

    class ParameterHolder(private val v: View) : AbsViewHolder(v) {
        private val binding = ItemAnalysisParamBinding.bind(v)

        override fun bind(cell: AbsAnalysisCell) {
            if (cell is AbsAnalysisCell.Parameter) {
                binding.paramNameTextView.text = cell.name
                binding.paramValueTextView.text = cell.value
            }
        }
    }

    class WordListButtonHolder(
        private val v: View,
        private val buttonInteraction: WordListButtonInteraction
    ) : AbsViewHolder(v) {
        private val binding = ItemListButtonBinding.bind(v)

        init {
            binding.wordListButton.setOnClickListener {
                buttonInteraction.onButtonClicked()
            }
        }

        override fun bind(cell: AbsAnalysisCell) {
            if (cell is AbsAnalysisCell.WordListButton) {
                binding.wordListButton.text = cell.buttonText
            }
        }
    }

    interface WordListButtonInteraction {
        fun onButtonClicked()
    }
}