package com.example.bookanalyzer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.daos.BookPreviewDao
import com.example.bookanalyzer.data.database.models.DbBookAnalysisData
import com.example.bookanalyzer.data.database.models.DbBookPreviewData

@Database(entities = [DbBookPreviewData::class, DbBookAnalysisData::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun bookPreviewDao() : BookPreviewDao
    abstract fun bookAnalysisDao() : BookAnalysisDao

    companion object{
        private var instance:AppDataBase?=null

        fun getDataBase(ctx:Context) : AppDataBase?{
            if (instance == null) {
                instance = Room.databaseBuilder(ctx,AppDataBase::class.java,"myDb").build()
            }
            return instance
        }

        fun destroyDataBase(){
            instance = null
        }
    }
}