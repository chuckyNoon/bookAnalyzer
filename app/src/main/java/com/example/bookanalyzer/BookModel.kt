package com.example.bookanalyzer

import android.graphics.Bitmap
import com.example.bookanalyzer.common.Utils

class AnalyzedBookModel(var path:String,
                        var uniqueWordCount:Int,
                        var allWordCount:Int,
                        var allCharCount:Int,
                        var avgSentenceLenInWrd:Double,
                        var avgSentenceLenInChr:Double,
                        var avgWordLen:Double,
                        var img:ByteArray?,
                        var wordMap:Map<String,Int>)
{

}

class MenuBookModel(var path:String,
                    var name:String?,
                    var author:String?,
                    var bitmap: Bitmap?,
                    var wordCount:Int = 0,
                    var selected:Boolean = false
){
}

class BookInfoModel(var path:String,
                    var uniqueWordCount:String,
                    var allWordCount:String,
                    var allCharsCount:String,
                    var avgSentenceLenInWrd:String,
                    var avgSentenceLenInChr:String,
                    var avgWordLen:String)
{

}