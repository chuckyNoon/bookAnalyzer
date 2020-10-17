package com.example.bookanalyzer.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.example.bookanalyzer.R
import java.io.File

class SearchSettingsDialog : DialogFragment() {
    private var callback: OnSelectedSearchSettings? = null
    private lateinit var epubCheckBox: CheckBox
    private lateinit var fb2CheckBox: CheckBox
    private lateinit var txtCheckBox: CheckBox
    private lateinit var radioGroup: RadioGroup
    private lateinit var startSearchButton: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_search_settings, null)
        initFields(view)
        setStartSearchButton()

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setView(view)
        return builder.create()
    }

    private fun setStartSearchButton() {
        startSearchButton.setOnClickListener {
            val selectedFormats = getSelectedFormats()
            val selectedSearchRootDir = getSelectedSearchRootDir()

            callback?.onSelectedSearchSettings(selectedFormats, selectedSearchRootDir)
            dismiss()
        }
    }

    private fun getSelectedFormats(): ArrayList<String> {
        val isEpubFormatSelected = epubCheckBox.isChecked
        val isFb2FormatSelected = fb2CheckBox.isChecked
        val isTxtFormatSelected = txtCheckBox.isChecked

        val selectedFormats = ArrayList<String>()
        if (isEpubFormatSelected) {
            selectedFormats.add(resources.getString(R.string.epubFormat))
        }
        if (isFb2FormatSelected) {
            selectedFormats.add(resources.getString(R.string.fb2Format))
        }
        if (isTxtFormatSelected) {
            selectedFormats.add(resources.getString(R.string.txtFormat))
        }
        return (selectedFormats)
    }

    private fun getSelectedSearchRootDir(): File {
        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        return if (checkedRadioButtonId == R.id.allDirsRadioButton) {
            (Environment.getExternalStorageDirectory())
        } else {
            (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
        }
    }

    private fun initFields(view: View) {
        epubCheckBox = view.findViewById(R.id.epubBox)
        fb2CheckBox = view.findViewById(R.id.fb2Box)
        txtCheckBox = view.findViewById(R.id.txtBox)
        radioGroup = view.findViewById(R.id.radioGroup)
        startSearchButton = view.findViewById(R.id.startSearchButton)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is OnSelectedSearchSettings) {
            callback = activity as OnSelectedSearchSettings
        }
    }

    interface OnSelectedSearchSettings {
        fun onSelectedSearchSettings(bookFormats: ArrayList<String>, searchRootDir: File)
    }
}

