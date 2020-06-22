package com.example.bookanalyzer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.webkit.WebView
import android.widget.ImageView
import nl.siegmann.epublib.epub.Main
import java.io.InputStream
import kotlin.math.roundToInt

class LoaderActivity : AppCompatActivity() {
    var analysis:BookAnalysis? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        val arguments = intent.extras
        val uri = Uri.parse(arguments?.getString("uri"))
        val inStream = contentResolver.openInputStream(uri)
        val path = getFileName(uri)

        analysis = BookAnalysis(inStream!!, path!!)
        analysis?.execute()
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
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun getc() : LoaderActivity{
        return (this)
    }

    inner class BookAnalysis(private val inStream: InputStream, val path:String)
        : AsyncTask<Void, Int, Void>()
    {
        private var parser:FileParser? = null
        private var simpleText:String? = null
        var finalMap:Map<String,Int>? = null
        var avgWordLen:Double = 0.0
        var avgSentenceLen:Double= 0.0
        var wordsPerTwo:Int = 0
        var wordsCount = 0
        var unWordsCount = 0
        var img:ByteArray? = null
        private var normalizer: WordNormalizer? = null
        var intent:Intent?= null

        fun doit() {
            parser = FileParser()
            var time1 = System.currentTimeMillis()
            normalizer = WordNormalizer(getc())
            var time2 = System.currentTimeMillis()
            println("e=" + ((time2- time1).toDouble() / 1000 ).toString())
            simpleText = when{
                path.contains(".txt") -> parser!!.parseTxt(inStream)
                path.contains(".epub") -> parser!!.epubToTxt(inStream)
                else -> parser!!.parseTxt(inStream)
            }
            img = parser?.img
            finalMap = normalizeWordMap(parser!!.parseWords(simpleText!!))
            calcWordCount()
            calcAvgWordLen()
            calcAvgSentenceLen()
            unWordsCount = finalMap?.size ?:0
            println(unWordsCount)
            /* for((a,b) in finalMap!!)
                 println("$a $b")*/
        }

        private fun calcWordCount(){
            var ans:Int = 0
            finalMap?.let {
                for ((a, b) in it) {
                    ans += b
                }
            }
            wordsCount = ans
        }

        fun roundDouble(d:Double):Double{
            return ((d * 100).roundToInt().toDouble() / 100)
        }


        private fun calcAvgWordLen(){
            var sumLen:Long = 0
            finalMap?.let {
                for ((a, b) in it) {
                    sumLen += a.length * b
                }
            }
            avgWordLen = if (wordsCount != 0)  sumLen.toDouble()/ wordsCount else 0.0
        }

        private fun calcAvgSentenceLen(){
            val strs = simpleText?.split(".")
            val sentencesCount = strs?.size?:0
            avgSentenceLen = if (sentencesCount != 0) wordsCount.toDouble() / sentencesCount else 0.0
        }

        private fun normalizeWordMap(sourceMap:MutableMap<String,Int>):Map<String,Int>{
            val ansMap = mutableMapOf<String,Int>()
            for ((a, b) in sourceMap){
                val newWord = normalizer!!.getLemma(a)
                if (newWord == null){
                    ansMap[a] = (ansMap[a]?:0) + b
                }else{
                    ansMap[newWord] = (ansMap[newWord]?:0) + b
                }
            }
            return (ansMap.toList().sortedBy { it.second }.reversed().toMap())
        }

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: Void?): Void? {
            val time1 = System.currentTimeMillis()
            doit()
            val time2 = System.currentTimeMillis()
            intent = Intent(getc(), MainActivity::class.java)

            intent?.putExtra("bookName", path)
            intent?.putExtra("wordCount", wordsCount.toString())
            intent?.putExtra("uniqCount", finalMap!!.size.toString())
            intent?.putExtra("sentLen",roundDouble(avgSentenceLen).toString())
            intent?.putExtra("wordLen",roundDouble(avgWordLen).toString())
            intent?.putExtra("img", img)
            var i = 0
            finalMap?.let {
                for ((a,b) in it){
                    intent?.putExtra("w$i",a)
                    intent?.putExtra("c$i",b)
                    i++
                }
            }
            val time3 = System.currentTimeMillis()
            println("t=" + ((time2- time1).toDouble() / 1000 ).toString())
            println("t=" + ((time3- time2).toDouble() / 1000 ).toString())
            return (null)
        }

        override fun onProgressUpdate(vararg progress: Int?) {
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            startActivity(intent)
        }
    }
}