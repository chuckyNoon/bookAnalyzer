package com.example.bookanalyzer.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class FirstLaunchDialog : DialogFragment() {
    private var callback: OnSelectedLaunchOption? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        builder.setMessage("Scan all books on device?")
            .setPositiveButton("Yes") { dialog, id ->
                callback?.onSelectedLaunchOption(true)
            }
            .setNegativeButton("No") { dialog, id ->
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

