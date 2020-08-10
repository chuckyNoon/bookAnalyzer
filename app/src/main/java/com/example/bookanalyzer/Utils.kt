package com.example.bookanalyzer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.charset.StandardCharsets
import java.util.*

class Utils {
    companion object{
        fun byteArrayToBitmap(byteArray: ByteArray?) : Bitmap? {
            if (byteArray == null){
                return null
            }
            val base64Array = String(byteArray, StandardCharsets.UTF_8)
            return try {
                val decodedByteArray = android.util.Base64.decode(base64Array, base64Array.length)// Base64.getMimeDecoder().decode(base64Array)
                val bmp = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
                (bmp)
            }catch (e:java.lang.IllegalArgumentException){//if not in base64
                val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                (bmp)
            }
        }
    }
}