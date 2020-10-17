package com.example.bookanalyzer.data.filesystem.preview_parser

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

    protected fun saveImage(bitmap: Bitmap?, saveImgPath: String?) {
        if (bitmap == null || saveImgPath == null) {
            return
        }
        try {
            val out = ctx.openFileOutput(saveImgPath, 0)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
        } catch (e: IOException) {
            println(e)
        }
    }
}
