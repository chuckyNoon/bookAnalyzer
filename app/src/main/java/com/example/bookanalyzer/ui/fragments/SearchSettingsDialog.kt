package com.example.bookanalyzer.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.example.bookanalyzer.ui.activities.ISelectedSearchSettings
import com.example.bookanalyzer.R


class SearchSettingsDialog : DialogFragment(){
    private var mCallback: ISelectedSearchSettings? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setTitle("Title!")
        val view = layoutInflater.inflate(R.layout.search_settings_dialog, null)
        view.findViewById<Button>(R.id.startSearchButton).setOnClickListener {
            val epubSelected = view.findViewById<CheckBox>(R.id.epubBox).isChecked
            val fb2Selected = view.findViewById<CheckBox>(R.id.fb2Box).isChecked
            val txtSelected = view.findViewById<CheckBox>(R.id.txtBox).isChecked
            val dirId = view.findViewById<RadioGroup>(R.id.radioGroup).checkedRadioButtonId

            val selectedFormats = ArrayList<String>()
            if (epubSelected)
                selectedFormats.add("epub")
            if (fb2Selected)
                selectedFormats.add("fb2")
            if (txtSelected)
                selectedFormats.add("txt")
            val dir = if (dirId == 0) Environment.getExternalStorageDirectory()
                else Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            mCallback?.onSelectedSearchSettings(selectedFormats, dir)
            dismiss()
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setView(view)
        return  builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mCallback = activity as ISelectedSearchSettings
        } catch (e: ClassCastException) {
            println("doesnt support")
        }
    }

}