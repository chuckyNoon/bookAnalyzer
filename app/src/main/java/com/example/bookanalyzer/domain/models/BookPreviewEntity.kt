package com.example.bookanalyzer.domain.models

data class BookPreviewEntity(
    var path: String,
    var title: String?,
    var author: String?,
    var imgPath: String?,
    var uniqueWordCount: Int,
    var analysisId: Int,
)