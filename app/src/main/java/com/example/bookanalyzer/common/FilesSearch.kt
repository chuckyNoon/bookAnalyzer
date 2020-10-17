package com.example.bookanalyzer.common

import java.io.File

class FilesSearch {
    companion object {
        fun findFiles(rootDir: File, namePatterns: ArrayList<String>?): ArrayList<String> {
            val foundPaths = ArrayList<String>()
            val patterns = namePatterns ?: arrayListOf()
            search(rootDir, foundPaths, patterns)
            return foundPaths
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
            val fileList = dir.listFiles() ?: return
            for (file in fileList) {
                if (file.isDirectory) {
                    search(file, foundPaths, namePatterns)
                } else {
                    if (fitsPattern(file.name, namePatterns)) {
                        foundPaths.add(file.path)
                    }
                }
            }
        }
    }
}