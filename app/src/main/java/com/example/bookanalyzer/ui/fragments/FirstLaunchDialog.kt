package com.example.bookanalyzer.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.bookanalyzer.ui.activities.ISelectedLaunch

class FirstLaunchDialog : DialogFragment() {
    private var mCallback: ISelectedLaunch? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

        builder.setMessage("Scan all books on device?")
            .setPositiveButton("Yes") { dialog, id ->
                mCallback?.onSelectedLaunch(true)
            }
            .setNegativeButton("No") { dialog, id ->
                mCallback?.onSelectedLaunch(false)
            }
        // Create the AlertDialog object and return it
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mCallback = activity as ISelectedLaunch
        } catch (e: ClassCastException) {
            println("doesnt support")
        }
    }
}
