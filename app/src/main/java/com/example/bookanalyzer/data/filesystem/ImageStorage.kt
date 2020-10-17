package com.example.bookanalyzer.data.filesystem

import android.content.Context
import android.graphics.Bitmap
import android.os.FileUtils
import com.example.bookanalyzer.common.Utils
import java.io.IOException

class ImageStorage() {
    companion object {
        fun saveImage(ctx: Context, imgByteArray: ByteArray, saveImgPath: String): Boolean {
            val bitmap = Utils.byteArrayToBitmap(imgByteArray) ?: return false
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
}