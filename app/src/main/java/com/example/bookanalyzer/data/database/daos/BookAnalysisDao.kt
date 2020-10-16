package com.example.bookanalyzer.data.database.daos

import androidx.room.*
import com.example.bookanalyzer.data.database.models.DbBookAnalysisData
import com.example.bookanalyzer.data.database.models.DbBookPreviewData

@Dao
interface BookAnalysisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookAnalysis(dbBookAnalysisData: DbBookAnalysisData)

    @Update
    fun updateBookAnalysis(dbBookAnalysisData: DbBookAnalysisData)

    @Delete
    fun deleteBookAnalysis(dbBookAnalysisData: DbBookAnalysisData)

    @Query("SELECT * FROM info_table WHERE path == :path LIMIT 1")
    fun getBookAnalysisByPath(path: String): DbBookAnalysisData?

    @Query("SELECT * FROM info_table WHERE id == :id LIMIT 1")
    fun getBookAnalysisById(id:Int): DbBookAnalysisData?

    @Query("DELETE FROM info_table")
    fun nukeTable()

    @Query("SELECT * FROM info_table")
    fun getBookAnalyses(): List<DbBookPreviewData>
}