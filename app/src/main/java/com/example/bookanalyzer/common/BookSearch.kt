package com.example.bookanalyzer.common

import java.io.File

class BookSearch {
    companion object {
        fun findAll(dir: File, specPatterns:ArrayList<String>? = null): ArrayList<String> {
            val foundFiles = ArrayList<String>()
            val patterns = specPatterns ?: arrayListOf("fb2", "epub", "txt")
            search(dir, foundFiles, patterns)
            return (foundFiles)
        }

        private fun fitsPattern(fileName: String, patterns: ArrayList<String>): Boolean {
            if (patterns.isNullOrEmpty())
                return false
            for (pattern in patterns) {
                if (fileName.endsWith(pattern)) {
                    return true
                }
            }
            return false
        }

        private fun search(dir: File,foundFiles:ArrayList<String>, patterns: ArrayList<String>) {
            val fileList: Array<File> = dir.listFiles() ?: return
            for (j in fileList.indices) {
                if (fileList[j].isDirectory) {
                    search(fileList[j], foundFiles, patterns)
                } else {
                    if (fitsPattern(fileList[j].name, patterns)) {
                        foundFiles.add(fileList[j].path)
                    }
                }
            }
        }
    }
}