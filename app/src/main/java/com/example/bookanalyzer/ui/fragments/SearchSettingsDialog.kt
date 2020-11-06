package com.example.bookanalyzer.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.bookanalyzer.R
import com.example.bookanalyzer.databinding.DialogSearchSettingsBinding
import java.io.File

class SearchSettingsDialog : DialogFragment() {

    private var callback: OnSelectedSearchSettings? = null
    private lateinit var binding: DialogSearchSettingsBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding =  DialogSearchSettingsBinding.inflate(LayoutInflater.from(context))
        setupStartSearchButton()

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setView(binding.root)
        return builder.create()
    }

    private fun setupStartSearchButton() {
        binding.startSearchButton.setOnClickListener {
            val selectedFormats = getSelectedFormats()
            val selectedSearchRootDir = getSelectedSearchRootDir()

            callback?.onSelectedSearchSettings(selectedFormats, selectedSearchRootDir)
            dismiss()
        }
    }

    private fun getSelectedFormats(): ArrayList<String> {
        val isEpubFormatSelected = binding.epubBox.isChecked
        val isFb2FormatSelected = binding.fb2Box.isChecked
        val isTxtFormatSelected = binding.txtBox.isChecked

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
        val checkedRadioButtonId = binding.radioGroup.checkedRadioButtonId
        return if (checkedRadioButtonId == R.id.allDirsRadioButton) {
            (Environment.getExternalStorageDirectory())
        } else {
            (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
        }
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

