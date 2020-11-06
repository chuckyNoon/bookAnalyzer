package com.example.bookanalyzer.domain.models

data class BookPreviewEntity(
    var path: String = "",
    var title: String? = null,
    var author: String? = null,
    var imgPath: String? = null,
    var uniqueWordCount: Int = 0,
    var analysisId: Int = 0,
)