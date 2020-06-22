package com.example.bookanalyzer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class StartActivity : AppCompatActivity() {
    var slectButton:Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        slectButton = findViewById(R.id.selectButton)
        slectButton?.setOnClickListener {
            val intent:Intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 123)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            var strUri = data!!.data.toString()
            val intent = Intent(this, LoaderActivity::class.java)
            intent.putExtra("uri",strUri)
            startActivity(intent)
        }
    }
}