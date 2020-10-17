package com.example.bookanalyzer.analyzer

import java.util.*

class WordsParser {
    fun getWordMap(text: String): MutableMap<String, Int> {
        val words = splitTextInWords(text)
        val wordMap = mutableMapOf<String, Int>()
        for (word in words) {
            insertWordInMap(wordMap, word)
        }
        return (wordMap)
    }

    private fun splitTextInWords(text: String): Array<String> {
        val regex = "\\s*\\s|,|>|<|“|—|\\[|]|!|;|:|”|/|\\*|-|…|\\)|\\(|\\?|\\.|\"\\s*".toRegex()
        return text.split(regex).toTypedArray()
    }

    private fun insertWordInMap(map: MutableMap<String, Int>, word: String) {
        if (!isNumber(word)) {
            val lowerWord = word.toLowerCase(Locale.ROOT)
            map[lowerWord] = 1 + (map[lowerWord] ?: 0)
        }
    }

    private fun isNumber(str: String): Boolean {
        str.forEach {
            if (it.isDigit()) {
                return true
            }
        }
        return false
    }
}