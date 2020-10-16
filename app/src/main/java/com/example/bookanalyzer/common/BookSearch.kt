package com.example.bookanalyzer.common

import java.io.File

class BookSearch {
    companion object {
        fun findBookPaths(dir: File, namePatterns: ArrayList<String>? = null): ArrayList<String> {
            val foundPaths = ArrayList<String>()
            val patterns = namePatterns ?: arrayListOf("fb2", "epub", "txt")
            search(dir, foundPaths, patterns)
            return (foundPaths)
        }

        private fun fitsPattern(fileName: String, namePatterns: ArrayList<String>): Boolean {
            if (namePatterns.isNullOrEmpty()) {
                return false
            }
            for (pattern in namePatterns) {
                if (fileName.endsWith(pattern)) {
                    return true
                }
            }
            return false
        }

        private fun search(
            dir: File,
            foundPaths: ArrayList<String>,
            namePatterns: ArrayList<String>
        ) {
            val fileList: Array<File> = dir.listFiles() ?: return
            for (i in fileList.indices) {
                if (fileList[i].isDirectory) {
                    search(fileList[i], foundPaths, namePatterns)
                } else {
                    if (fitsPattern(fileList[i].name, namePatterns)) {
                        foundPaths.add(fileList[i].path)
                    }
                }
            }
        }
    }
}