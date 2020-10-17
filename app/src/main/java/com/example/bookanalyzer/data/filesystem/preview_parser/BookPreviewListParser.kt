package com.example.bookanalyzer.data.filesystem.preview_parser

import android.content.Context

class BookPreviewListParser(private val ctx: Context) {

    companion object {
        private const val EPUB_ENDING = ".epub"
        private const val FB2_ENDING = ".fb2"
    }

    fun getParsedDataList(paths: ArrayList<String>): ArrayList<ParsedBookData> {
        val parsedDataList = ArrayList<ParsedBookData>()
        for (path in paths) {
            val bookPreviewParser = getBookPreviewParser(path)
            parsedDataList.add(bookPreviewParser.getParsedData(path))
        }
        return parsedDataList
    }

    private fun getBookPreviewParser(path: String): BookPreviewParser {
        return when {
            path.endsWith(EPUB_ENDING) -> EpubPreviewParser(ctx)
            path.endsWith(FB2_ENDING) -> Fb2PreviewParser(ctx)
            else -> TxtPreviewParser(ctx)
        }
    }
}