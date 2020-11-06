package com.example.bookanalyzer.ui.adapters.book_items_adapter

data class BookItem(
    var path: String,
    var title: String,
    var author: String,
    var format: String,
    var imgPath: String?,
    var uniqueWordCount: String,
    var barProgress: Int,
    var analysisId: Int,
)