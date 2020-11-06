package com.example.bookanalyzer.ui.adapters.side_menu_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.bookanalyzer.R
import kotlinx.android.synthetic.main.item_side_menu_row.view.*

//to remake
class SideMenuAdapter(
    ctx: Context,
    private val sideMenuItems: ArrayList<SideMenuItem>
) : BaseAdapter() {

    private val layoutInflater =
        ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.item_side_menu_row, parent, false)
        val sideMenuItem = sideMenuItems[position]
        val iconView = view.findViewById<ImageView>(R.id.iconView)

        iconView.visibility = ImageView.INVISIBLE
        sideMenuItem.iconRes?.let {
            iconView.visibility = ImageView.VISIBLE
            iconView.setImageResource(it)
        }
        view.actionTextView.text = sideMenuItem.text
        view.setOnTouchListener(sideMenuItem.onTouchListener)
        return view
    }

    override fun getItem(position: Int): Any {
        return sideMenuItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return sideMenuItems.size
    }
}