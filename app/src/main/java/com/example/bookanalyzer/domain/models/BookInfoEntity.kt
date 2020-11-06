package com.example.bookanalyzer.domain.models

data class BookInfoEntity(
    var path: String = "",
    var uniqueWordCount: Int = 0,
    var allWordCount: Int = 0,
    var allCharsCount: Int = 0,
    var avgSentenceLenInWrd: Double = 0.0,
    var avgSentenceLenInChr: Double = 0.0,
    var avgWordLen: Double = 0.0,
    var wordListPath: String = ""
)