package com.example.bookanalyzer.data.filesystem.storage

import android.content.Context
import android.graphics.Bitmap
import com.example.bookanalyzer.common.BitmapUtils
import java.io.IOException

class ImageStorage(private val ctx: Context) {
    fun saveImage(imgByteArray: ByteArray, saveImgPath: String): Boolean {
        val bitmap = BitmapUtils.byteArrayToBitmap(imgByteArray) ?: return false
        return try {
            val out = ctx.openFileOutput(saveImgPath, 0)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
            (true)
        } catch (e: IOException) {
            println(e)
            (false)
        }
    }

    fun getSaveImgPathByTitle(bookTitle: String?): String? {
        return bookTitle?.let { "${bookTitle}.jpg" }
    }
}