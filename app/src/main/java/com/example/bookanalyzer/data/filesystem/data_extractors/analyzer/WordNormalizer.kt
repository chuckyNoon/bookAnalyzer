package com.example.bookanalyzer.data.filesystem.data_extractors.analyzer

import android.content.Context
import com.example.bookanalyzer.R
import java.io.ByteArrayOutputStream
import java.io.InputStream

class WordNormalizer(private val ctx: Context) {

    private val nounMap: MutableMap<String, Int> = mutableMapOf()
    private val verbMap: MutableMap<String, Int> = mutableMapOf()
    private val adjMap: MutableMap<String, Int> = mutableMapOf()
    private val advMap: MutableMap<String, Int> = mutableMapOf()

    private val nounExMap: MutableMap<String, String> = mutableMapOf()
    private val verbExMap: MutableMap<String, String> = mutableMapOf()
    private val adjExMap: MutableMap<String, String> = mutableMapOf()

    private lateinit var nounRules: Array<Pair<String, String>>
    private lateinit var verbRules: Array<Pair<String, String>>
    private lateinit var adjRules: Array<Pair<String, String>>

    init {
        parseDictionaryFileInMap(R.raw.inoun, nounMap, ::insertInUsualWordMap)
        parseDictionaryFileInMap(R.raw.iverb, verbMap, ::insertInUsualWordMap)
        parseDictionaryFileInMap(R.raw.iadj, adjMap, ::insertInUsualWordMap)
        parseDictionaryFileInMap(R.raw.iadv, advMap, ::insertInUsualWordMap)

        parseDictionaryFileInMap(R.raw.noun, nounExMap, ::insertInExceptionWordMap)
        parseDictionaryFileInMap(R.raw.verb, verbExMap, ::insertInExceptionWordMap)
        parseDictionaryFileInMap(R.raw.adj, adjExMap, ::insertInExceptionWordMap)

        initRules()
    }

    fun getWordBase(word: String): String? {
        return checkWordBaseInExceptionWordDictionaries(word)
            ?: tryNormalizeWordByAnyRules(word)
            ?: checkWordBaseInUsualWordDictionaries(word)
    }

    private fun initRules() {
        verbRules = arrayOf(
            ("s" to ""),
            ("ies" to "y"),
            ("es" to "e"),
            ("es" to ""),
            ("ed" to "e"),
            ("ed" to ""),
            ("ing" to "e"),
            ("ing" to "")
        )

        nounRules = arrayOf(
            ("'" to ""),
            ("'s" to ""),
            ("s" to ""),
            ("’s" to ""),
            ("’" to ""),
            ("ses" to "s"),
            ("xes" to "x"),
            ("zes" to "z"),
            ("ches" to "ch"),
            ("shes" to "sh"),
            ("men" to "man"),
            ("ies" to "y")
        )

        adjRules = arrayOf(
            ("er" to ""),
            ("er" to "e"),
            ("est" to ""),
            ("est" to "e")
        )
    }

    private fun <T> parseDictionaryFileInMap(
        resId: Int,
        map: MutableMap<String, T>,
        insertInMap: (String, MutableMap<String, T>) -> Unit
    ) {
        val text = readRawTextFile(resId)
        val lines = text?.split("\n") ?: return
        for (line in lines) {
            insertInMap(line, map)
        }
    }

    private fun readRawTextFile(resId: Int): String? {
        val inputStream = getRawResourceInputStream(resId)
        inputStream?.let {
            return readTextFromInputStream(inputStream)
        }
        return null
    }

    private fun getRawResourceInputStream(resId: Int): InputStream? {
        return try {
            val inputStream = ctx.resources.openRawResource(resId)
            (inputStream)
        } catch (e: android.content.res.Resources.NotFoundException) {
            (null)
        }
    }

    private fun readTextFromInputStream(inputStream: InputStream): String {
        ByteArrayOutputStream().use { result ->
            val buffer = ByteArray(1024 * 8)
            var length: Int
            while (inputStream.read(buffer).also { length = it } != -1) {
                result.write(buffer, 0, length)
            }
            return result.toString("UTF-8")
        }
    }

    private fun insertInUsualWordMap(line: String, map: MutableMap<String, Int>) {
        if (line.isNotEmpty()) {
            val firstWord = getFirstWordFromDictionaryLine(line)
            if (firstWord != null) {
                map[firstWord] = 1
            }
        }
    }

    private fun insertInExceptionWordMap(line: String, map: MutableMap<String, String>) {
        if (line.isNotEmpty()) {
            val words = line.split(" ")
            if (words.size >= 2) {
                map[words[0]] = words[1]
            }
        }
    }

    private fun getFirstWordFromDictionaryLine(line: String): String? {
        var wordStart = -1
        var wordEnd = -1
        for (i in line.indices) {
            if (line[i].isLetter()) {
                wordStart = i
                for (j in i + 1 until line.length) {
                    if (!line[j].isLetter()) {
                        wordEnd = j - 1
                        break
                    }
                }
                break
            }
        }
        var firstWord: String? = null
        if (wordEnd >= 0 && wordStart in 0..wordEnd) {
            firstWord = line.substring(wordStart..wordEnd)
        }
        return firstWord
    }

    private fun tryNormalizeWordBySelectedRules(
        word: String,
        wordMap: MutableMap<String, Int>,
        rules: Array<Pair<String, String>>
    ): String? {
        for ((oldEnding, newEnding) in rules) {
            if (word.endsWith(oldEnding)) {
                val normalizedWord = word.substring(0, word.length - oldEnding.length) + newEnding
                if (wordMap.contains(normalizedWord)) {
                    return normalizedWord
                }
            }
        }
        return null
    }


    private fun checkWordBaseInExceptionWordDictionaries(word: String): String? {
        return when {
            nounExMap.contains(word) -> (nounExMap[word])
            verbExMap.contains(word) -> (verbExMap[word])
            adjExMap.contains(word) -> (adjExMap[word])
            else -> (null)
        }
    }

    private fun checkWordBaseInUsualWordDictionaries(word: String): String? {
        return when {
            nounMap.contains(word) -> (word)
            verbMap.contains(word) -> (word)
            adjMap.contains(word) -> (word)
            else -> (null)
        }
    }

    private fun tryNormalizeWordByAnyRules(word: String): String? {
        return tryNormalizeWordBySelectedRules(word, verbMap, verbRules)
            ?: tryNormalizeWordBySelectedRules(word, adjMap, adjRules)
            ?: tryNormalizeWordBySelectedRules(word, nounMap, nounRules)
    }
}