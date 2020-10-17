package com.example.bookanalyzer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.bookanalyzer.R
import kotlinx.android.synthetic.main.item_side_menu_row.view.*

class SideMenuItem(
    var text: String,
    var iconRes: Int?,
    var onTouchListener: View.OnTouchListener
)

//to remake
class SideMenuAdapter(
    ctx: Context,
    private val sideMenuItemList: ArrayList<SideMenuItem>
) : BaseAdapter() {

    private val layoutInflater =
        ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.item_side_menu_row, parent, false)
        val sideMenuItem = sideMenuItemList[position]
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
        return sideMenuItemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return sideMenuItemList.size
    }
}