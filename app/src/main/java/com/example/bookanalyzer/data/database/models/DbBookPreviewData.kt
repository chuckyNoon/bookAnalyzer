package com.example.bookanalyzer.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_table")
data class DbBookPreviewData(
    @ColumnInfo(name = "path")
    var path:String,
    @ColumnInfo(name = "title")
    var title:String?,
    @ColumnInfo(name = "author")
    var author:String?,
    @ColumnInfo(name = "img_path")
    var imgPath: String?,
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
){
}