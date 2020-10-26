package com.example.bookanalyzer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.daos.BookPreviewDao
import com.example.bookanalyzer.data.database.models.DbBookAnalysisData
import com.example.bookanalyzer.data.database.models.DbBookPreviewData

@Database(entities = [DbBookPreviewData::class, DbBookAnalysisData::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun bookPreviewDao(): BookPreviewDao
    abstract fun bookAnalysisDao(): BookAnalysisDao
}