package com.example.bookanalyzer.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.bookanalyzer.R

class FirstLaunchDialog : DialogFragment() {
    private var callback: OnSelectedLaunchOption? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setMessage(resources.getString(R.string.if_scan_message))
            .setPositiveButton(resources.getString(R.string.positive_response)) { dialog, id ->
                callback?.onSelectedLaunchOption(true)
            }
            .setNegativeButton(resources.getString(R.string.negative_response)) { dialog, id ->
                callback?.onSelectedLaunchOption(false)
            }
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is OnSelectedLaunchOption) {
            callback = activity as OnSelectedLaunchOption
        }
    }

    interface OnSelectedLaunchOption {
        fun onSelectedLaunchOption(isScanSelected: Boolean)
    }
}

