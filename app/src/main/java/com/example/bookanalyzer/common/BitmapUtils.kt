package com.example.bookanalyzer.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.nio.charset.StandardCharsets

class BitmapUtils {
    companion object {
        fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
            if (byteArray == null) {
                return null
            }
            return try {
                val base64Array = String(byteArray, StandardCharsets.UTF_8)
                (base64ArrayInBitmap(base64Array))
            } catch (e: java.lang.IllegalArgumentException) {
                (simpleByteArrayInBitmap(byteArray))
            }
        }

        private fun base64ArrayInBitmap(base64Array: String): Bitmap? {
            val decodedByteArray = android.util.Base64.decode(
                base64Array,
                base64Array.length
            )
            return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
        }

        private fun simpleByteArrayInBitmap(byteArray: ByteArray): Bitmap? {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }
}