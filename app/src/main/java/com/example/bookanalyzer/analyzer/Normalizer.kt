package com.example.bookanalyzer.analyzer

import android.content.Context
import com.example.bookanalyzer.R
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.concurrent.thread

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
        parseFile(R.raw.inoun, nounMap, ::insertInMap)
        parseFile(R.raw.iverb, verbMap, ::insertInMap)
        parseFile(R.raw.iadj, adjMap, ::insertInMap)
        parseFile(R.raw.iadv, advMap, ::insertInMap)

        parseFile(R.raw.noun, nounExMap, ::insertInExMap)
        parseFile(R.raw.verb, verbExMap, ::insertInExMap)
        parseFile(R.raw.adj, adjExMap, ::insertInExMap)

        initRules()
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


    private fun <T> parseFile(
        resId: Int,
        map: MutableMap<String, T>,
        foo: (String, MutableMap<String, T>) -> Unit
    ) {
        val str = readRawTextFile(ctx, resId)
        val lines = str?.split("\n") ?: return
        for (line in lines) {
            foo(line, map)
        }
    }

    private fun readRawTextFile(ctx: Context, resId: Int): String? {
        val inputStream: InputStream
        try {
            inputStream = ctx.resources.openRawResource(resId)
        } catch (e: android.content.res.Resources.NotFoundException) {
            return null
        }
        ByteArrayOutputStream().use { result ->
            val buffer = ByteArray(1024 * 8)
            var length: Int
            while (inputStream.read(buffer).also { length = it } != -1) {
                result.write(buffer, 0, length)
            }
            return result.toString("UTF-8")
        }
    }

    private fun insertInMap(line: String, map: MutableMap<String, Int>) {
        if (line.isNotEmpty()) {
            val firstWord = getFirstWord(line)
            if (firstWord != null) {
                map[firstWord] = 1
            }
        }
    }

    private fun insertInExMap(line: String, map: MutableMap<String, String>) {
        if (line.isNotEmpty()) {
            val words = line.split(" ")
            if (words.size >= 2) {
                map[words[0]] = words[1]
            }
        }
    }

    private fun getFirstWord(str: String): String? {
        var wordStart = -1
        var wordEnd = -1
        for (i in str.indices) {
            if (str[i].isLetter()) {
                wordStart = i
                for (j in i + 1 until str.length) {
                    if (!str[j].isLetter()) {
                        wordEnd = j - 1
                        break
                    }
                }
                break
            }
        }
        return (if (wordEnd >= 0 && wordStart in 0..wordEnd) str.substring(wordStart..wordEnd) else null)
    }

    private fun tryNorm(
        word: String,
        map: MutableMap<String, Int>,
        rules: Array<Pair<String, String>>
    ): String? {
        for ((oldEnding, newEnding) in rules) {
            if (word.endsWith(oldEnding)) {
                val normalizedWord = word.substring(0, word.length - oldEnding.length) + newEnding
                if (map.contains(normalizedWord)) {
                    return (normalizedWord)
                }
            }
        }
        return (null)
    }

    fun getLemma(word: String): String? {
        when {
            nounExMap.contains(word) -> return (nounExMap[word])
            verbExMap.contains(word) -> return (verbExMap[word])
            adjExMap.contains(word) -> return (adjExMap[word])
        }
        val normalizedWord = tryNorm(word, verbMap, verbRules) ?: tryNorm(word, adjMap, adjRules)
        if (normalizedWord != null) {
            return (normalizedWord)
        }
        when {
            nounMap.contains(word) -> return (word)
            verbMap.contains(word) -> return (word)
            adjMap.contains(word) -> return (word)
        }
        return (tryNorm(word, nounMap, nounRules) ?: if (advMap.contains(word)) word else null)
    }
}