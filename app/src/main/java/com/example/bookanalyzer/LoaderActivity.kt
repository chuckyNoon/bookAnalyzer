package com.example.bookanalyzer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.OpenableColumns
import android.renderscript.ScriptGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import java.io.*


class LoaderActivity : AppCompatActivity() {
    private lateinit var analysis:BookAnalysis
    private lateinit var job:Job
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        val arguments = intent.extras
        val path = arguments?.getString("path")
        val inStream = FileInputStream(path)

        if (path == null){
            finish()
        }else {
            analysis = BookAnalysis(inStream, path, this)
        }
        val bookInd = arguments?.getInt("ind")?:0
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

            saveAnalyzedInfo(path!!, bookInd, imgFileName, listFileName, infoFileName, false)
            startActivity(intent1)
        }
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
    }

    private fun findBookInAnalyzed(path:String) : Int{
        try{
            val inputStreamReader = InputStreamReader(openFileInput("all"))
            val bufferedReader = BufferedReader(inputStreamReader)
            val lines = bufferedReader.readText().split("\n")
            lines.forEach {
                val parts = it.split(" ",  limit = 2)
                if (parts.size == 2 && parts[1] == path){
                    return (parts[0].toInt())
                }
            }
            return (-1)
        }catch (e: IOException){
            return (-1)
        }
    }

    private fun saveAnalyzedInfo(sourceFilePath:String, ind:Int, imgFileName:String, listFileName:String, infoFileName:String, redo:Boolean){
        try {
            analysis.img?.let {
                val imgOut = openFileOutput(imgFileName, 0)
                imgOut.write(it)
            }
            val lstOut = openFileOutput(listFileName, 0)
            lstOut.write(analysis.normalizedWordMap.toString().toByteArray())

            println("wqw $infoFileName")
            val infoOut = openFileOutput(infoFileName, 0)
            val info = "$sourceFilePath\n${analysis.wordCount}\n${analysis.uniqWordCount}\n${analysis.avgSentenceLen}\n${analysis.avgWordLen}\n"
            infoOut.write(info.toByteArray())

            if (!redo) {
                val inAll = openFileOutput("all", Context.MODE_APPEND)
                inAll.write("$ind\n$sourceFilePath\n".toByteArray())
                inAll.close()
            }
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