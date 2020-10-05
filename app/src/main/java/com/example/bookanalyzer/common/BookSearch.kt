package com.example.bookanalyzer.common

import java.io.File

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
            println(fileList.isEmpty())
            for (j in fileList.indices) {

                println(fileList[j].path)

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