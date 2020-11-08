package com.example.bookanalyzer.domain.models

data class WordEntity(
    var word: String,
    var frequency: Int,
    var pos: Int
)