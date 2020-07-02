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
import java.io.IOException


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
        val bookInd= arguments?.getInt("ind")?:0

        if (inStream == null || path == null){
            finish()
        }else {
            analysis = BookAnalysis(inStream, path, this)
        }

        job = scope.launch(Dispatchers.IO){
            analysis.doit()
            yield()
            val intent1 = Intent(this@LoaderActivity, MainActivity::class.java)

            val infoFileName = "info$bookInd"
            val listFileName = "list$bookInd"
            val imgFileName = "img$bookInd"

            intent1.putExtra("imgPath", imgFileName)
            intent1.putExtra("listPath", listFileName)
            intent1.putExtra("infoPath", infoFileName)

            saveAnalyzedInfo(path!!, bookInd, imgFileName, listFileName, infoFileName)
            startActivity(intent1)
        }
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
    }

    private fun saveAnalyzedInfo(sourceFilePath:String, ind:Int, imgFileName:String, listFileName:String, infoFileName:String){
        try {
            analysis.img?.let {
                val imgOut = openFileOutput(imgFileName, 0)
                imgOut.write(it)
            }
            val lstOut = openFileOutput(listFileName, 0)
            lstOut.write(analysis.normalizedWordMap.toString().toByteArray())

            val infoOut = openFileOutput(infoFileName, 0)
            val info = "$sourceFilePath\n${analysis.wordCount}\n${analysis.uniqWordCount}\n${analysis.avgSentenceLen}\n${analysis.avgWordLen}"
            infoOut.write(info.toByteArray())

            val inAll = openFileOutput("all", 0)
            inAll.write((ind + 1).toString().toByteArray())
        }catch (e:IOException){
            println("saving error")
        }
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
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}