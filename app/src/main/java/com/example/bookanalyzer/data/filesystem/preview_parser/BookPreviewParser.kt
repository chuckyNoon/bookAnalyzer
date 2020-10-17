package com.example.bookanalyzer.data.filesystem.preview_parser

import android.content.Context

class ParsedPreviewData(
    var path: String,
    var title: String?,
    var author: String?,
    var imgByteArray: ByteArray?,
)

abstract class BookPreviewParser(val ctx: Context) {
    abstract fun getParsedData(path: String): ParsedPreviewData
}
