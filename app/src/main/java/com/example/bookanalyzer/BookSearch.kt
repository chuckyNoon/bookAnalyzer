package com.example.bookanalyzer

import java.io.File
import kotlin.concurrent.thread

class BookSearch {
    companion object {
        private lateinit var foundFiles:ArrayList<String>
        private var patterns: ArrayList<String>?=null
        fun findAll(dir: File, specPatterns:ArrayList<String>? = null): ArrayList<String> {
            foundFiles = ArrayList()
            patterns = specPatterns ?: arrayListOf("fb2", "epub", "txt")
            search(dir)
            return (foundFiles)
        }

        private fun fitsPattern(fileName: String): Boolean {
            if (patterns.isNullOrEmpty())
                return false
            for (pattern in patterns!!) {
                if (fileName.endsWith(pattern)) {
                    return true
                }
            }
            return false
        }

        private fun search(dir: File) {
            val fileList: Array<File> = dir.listFiles() ?: return

            for (j in fileList.indices) {
                if (fileList[j].isDirectory) {
                    search(fileList[j])
                } else {
                    if (fitsPattern(fileList[j].name)) {
                        foundFiles.add(fileList[j].path)
                    }
                }
            }
        }
    }
}