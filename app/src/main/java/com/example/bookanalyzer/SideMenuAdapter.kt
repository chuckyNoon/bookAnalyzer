package com.example.bookanalyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.getSystemService

class SideMenuElemModel(var action:String, var iconRes:Int?, var onClickListener: View.OnClickListener){

}

class SideMenuAdapter(private val ctx:Context,private val ar:ArrayList<SideMenuElemModel>) : BaseAdapter() {
    private val layoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.sidemenu_list_elem, parent, false)

        val model = ar[position]
        val imageView = view.findViewById<ImageView>(R.id.iconView)
        if (model.iconRes != null) {
            imageView.visibility = ImageView.VISIBLE
            imageView.setImageResource(model.iconRes!!)
        }
        else imageView.visibility = ImageView.INVISIBLE

        view.findViewById<TextView>(R.id.actionTextView).text = model.action
        view.setOnClickListener(model.onClickListener)
        return (view)
    }

    override fun getItem(position: Int): Any {
        return ar[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return (ar.size)
    }
}