package com.example.bookanalyzer.data.filesystem

import android.content.Context
import android.graphics.Bitmap
import java.io.IOException

class ParsedBookData(
    var path: String,
    var title: String?,
    var author: String?,
    var imgPath: String?
)

abstract class BookPreviewParser(val ctx: Context) {
    abstract fun getParsedData(path: String): ParsedBookData

    protected fun saveImage(bitmap: Bitmap, imgPath: String) {
        try {
            val out = ctx.openFileOutput(imgPath, 0)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
        } catch (e: IOException) {
            println(e)
        }
    }
}
