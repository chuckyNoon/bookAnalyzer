package com.example.bookanalyzer.domain.models

class SourceAnalysisEntity(
    var path: String,
    var uniqueWordCount: Int,
    var allWordCount: Int,
    var allCharCount: Int,
    var avgSentenceLenInWrd: Double,
    var avgSentenceLenInChr: Double,
    var avgWordLen: Double,
    var wordMap: Map<String, Int>
)