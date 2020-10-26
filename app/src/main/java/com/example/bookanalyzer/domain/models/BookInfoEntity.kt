package com.example.bookanalyzer.domain.models

data class BookInfoEntity(
    var path: String,
    var uniqueWordCount: Int,
    var allWordCount: Int,
    var allCharsCount: Int,
    var avgSentenceLenInWrd: Double,
    var avgSentenceLenInChr: Double,
    var avgWordLen: Double,
    var wordListPath: String
)