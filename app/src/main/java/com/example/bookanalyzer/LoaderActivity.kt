package com.example.bookanalyzer

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.launch



class LoaderActivity : AppCompatActivity() {
    private lateinit var analysis:BookAnalysis
    private lateinit var job:Job
    private val scope = MainScope()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        val arguments = intent.extras
        val uri = Uri.parse(arguments?.getString("uri"))
        val inStream = contentResolver.openInputStream(uri)
        val path = getFileName(uri)
        val ind= arguments?.getInt("ind")?:0

        analysis = BookAnalysis(inStream!!, path!!, this)

        job = scope.launch(Dispatchers.IO){
            val time1 = System.currentTimeMillis()
            analysis.doit()
            yield()

            val intent1 = Intent(this@LoaderActivity, MainActivity::class.java)

            val infoFileName = "info$ind"
            val listFileName = "list$ind"
            val imgFileName = "img$ind"

            intent1.putExtra("imgPath", imgFileName)
            intent1.putExtra("listPath", listFileName)
            intent1.putExtra("infoPath", infoFileName)

            analysis.img?.let {
                val imgOut = openFileOutput(imgFileName, 0)
                imgOut.write(it)
            }

            val lstOut = openFileOutput(listFileName, 0)
            lstOut.write(analysis.finalMap.toString().toByteArray())
            val infoOut  = openFileOutput(infoFileName, 0)
            val info = path + "\n" + analysis.wordsCount.toString() + "\n" + analysis.finalMap.size.toString() + "\n" +
                    analysis.avgSentenceLen.toString() + "\n" + analysis.avgWordLen.toString()
            infoOut.write(info.toByteArray())
            val inAll = openFileOutput("all", 0)
            inAll.write((ind+1).toString().toByteArray())

            val time2 = System.currentTimeMillis()
            println("t algo=" + ((time2- time1).toDouble() / 1000 ).toString())

            startActivity(intent1)
        }
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
    }

    @SuppressLint("Recycle")
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')?:return (null)
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }
}