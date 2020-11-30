package com.example.bookanalyzer.ui.adapters.side_menu_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.ItemSideMenuRowBinding
import kotlinx.android.synthetic.main.item_side_menu_row.view.*

class SideMenuRowsAdapter(
    private val interaction: SideMenuRowInteraction
) : RecyclerView.Adapter<SideMenuRowsAdapter.SideMenuRowHolder>() {

    private var cells: ArrayList<SideMenuRowCell> = ArrayList()

    fun setupCells(cells:ArrayList<SideMenuRowCell>){
        this.cells = cells
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SideMenuRowHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_side_menu_row, parent, false),
        interaction
    )

    override fun onBindViewHolder(holder: SideMenuRowHolder, position: Int) {
        holder.bind(cells[position])
    }

    override fun getItemCount() = cells.size

    class SideMenuRowHolder(
        private val view: View,
        private val interaction: SideMenuRowInteraction
    ) : RecyclerView.ViewHolder(view) {

        val binding = ItemSideMenuRowBinding.bind(view)

        init {
            view.setOnTouchListener(object : OnSideMenuItemTouchListener() {
                override fun onClick() {
                    interaction.onRowClicked(adapterPosition)
                }
            })
        }

        fun bind(rowCell: SideMenuRowCell) {
            binding.iconView.visibility = ImageView.INVISIBLE
            rowCell.iconRes?.let {
                binding.iconView.visibility = ImageView.VISIBLE
                binding.iconView.setImageResource(it)
            }
            view.actionTextView.text = rowCell.text
        }
    }

    interface SideMenuRowInteraction {
        fun onRowClicked(position: Int)
    }
}