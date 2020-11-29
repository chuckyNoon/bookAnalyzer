package com.example.bookanalyzer.ui.adapters.book_items_adapter

import java.io.Serializable

data class BookCell(
    val filePath: String,
    val title: String,
    val author: String,
    val format: String,
    val imgPath: String?,
    val uniqueWordCount: String,
    val barProgress: Int,
    val analysisId: Int,
)