package com.example.bookanalyzer.data.filesystem.preview_parser

import android.content.Context

class TxtPreviewParser(ctx: Context) : BookPreviewParser(ctx) {
    override fun getParsedData(path: String): ParsedPreviewData {
        return ParsedPreviewData(path, null, null, null)
    }
}