package com.example.bookanalyzer.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class FirstLaunchDialog : DialogFragment() {
    private var mCallback: IOnSelectedLaunchType? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        builder.setMessage("Scan all books on device?")
            .setPositiveButton("Yes") { dialog, id ->
                mCallback?.onSelectedLaunchType(true)
            }
            .setNegativeButton("No") { dialog, id ->
                mCallback?.onSelectedLaunchType(false)
            }
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mCallback = activity as IOnSelectedLaunchType
        } catch (e: ClassCastException) {
            println("doesnt support")
        }
    }

    interface IOnSelectedLaunchType{
        fun onSelectedLaunchType(ifScan: Boolean)
    }
}

