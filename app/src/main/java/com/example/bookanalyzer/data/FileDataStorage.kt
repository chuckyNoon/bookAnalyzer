package com.example.bookanalyzer.data

abstract class FileDataStorage {
    protected val FOUND_BOOKS_LIST:String = "main"
    protected val ANALYZED_BOOKS_LIST = "all"
    protected fun savedInfoPath(bookInd:Int) = "info$bookInd"
    protected fun savedImgPath(bookInd: Int) = "img$bookInd"
    protected fun savedWordListPath(bookInd: Int) = "list$bookInd"
}