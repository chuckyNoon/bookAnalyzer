package com.example.bookanalyzer.data.filesystem

import android.content.Context

class BookPreviewListParser(private val ctx:Context) {
    fun getParsedDataList(paths: ArrayList<String>): ArrayList<ParsedBookData> {
        val parsedDataList = ArrayList<ParsedBookData>()

        for (path in paths) {
            val bookPreviewParser = getBookPreviewParser(path)
            parsedDataList.add(bookPreviewParser.getParsedData(path))
        }
        return (parsedDataList)
    }

    private fun getBookPreviewParser(path:String) : BookPreviewParser{
        return when{
            path.endsWith(".epub") -> EpubPreviewParser(ctx)
            path.endsWith(".fb2") -> Fb2PreviewParser(ctx)
            else -> TxtPreviewParser(ctx)
        }
    }
}