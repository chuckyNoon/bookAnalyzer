package com.example.bookanalyzer.data.filesystem

import android.content.Context

class TxtPreviewParser(ctx: Context) : BookPreviewParser(ctx) {
    override fun getParsedData(path: String): ParsedBookData {
        return ParsedBookData(path, null, null, null)
    }
}