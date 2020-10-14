package com.example.bookanalyzer.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "info_table")
data class DbBookAnalysisData(
    @ColumnInfo(name = "path")
    var path:String,
    @ColumnInfo(name = "uniqueWordCount")
    var uniqueWordCount:Int,
    @ColumnInfo(name = "allWordCount")
    var allWordCount:Int,
    @ColumnInfo(name = "allCharsCount")
    var allCharsCount:Int,
    @ColumnInfo(name = "avgSentenceLenInWrd")
    var avgSentenceLenInWrd:Double,
    @ColumnInfo(name = "avgSentenceLenInChr")
    var avgSentenceLenInChr:Double,
    @ColumnInfo(name = "avgWordLen")
    var avgWordLen:Double,
    @ColumnInfo(name = "wordListPath")
    var wordListPath:String,
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0)
{
}