package com.example.bookanalyzer.data.database.daos

import androidx.room.*
import com.example.bookanalyzer.data.database.models.DbBookPreviewData

@Dao
interface BookPreviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookPreview(dbBookPreviewData: DbBookPreviewData)

    @Update
    fun updateBookPreview(dbBookPreviewData: DbBookPreviewData)

    @Delete
    fun deleteBookPreview(dbBookPreviewData: DbBookPreviewData)

    @Query("SELECT * FROM menu_table")
    fun getBookPreviews(): List<DbBookPreviewData>

    @Query("SELECT * FROM menu_table WHERE path == :path LIMIT 1")
    fun getBookPreviewByPath(path: String): DbBookPreviewData

    @Query("DELETE FROM menu_table")
    fun nukeTable()
}