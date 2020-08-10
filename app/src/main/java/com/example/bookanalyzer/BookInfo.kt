package com.example.bookanalyzer

import android.graphics.Bitmap

class BookInfo(var path:String,
               var name:String?,
               var author:String?,
               var bitmap: ByteArray?,
               var wordCount:Int = 0
){
    fun toByteArray() : ByteArray{
        val out = path + "\n" + name + "\n" +  author + "\n"
        return out.toByteArray()
    }
}

class ABookInfo(var path:String,
                var name:String?,
                var author:String?,
                var bitmap: Bitmap?,
                var wordCount:Int = 0
){
    constructor(b:BookInfo):this(b.path, b.name, b.author,  Utils.byteArrayToBitmap(b.bitmap), b.wordCount){
    }

    fun toByteArray() : ByteArray{
        val out = path + "\n" + name + "\n" +  author + "\n"
        return out.toByteArray()
    }
}