package com.example.bookanalyzer.di.modules

import android.content.Context
import com.example.bookanalyzer.data.filesystem.data_extractors.analyzer.BookAnalyzer
import com.example.bookanalyzer.data.filesystem.storage.ImageStorage
import com.example.bookanalyzer.data.filesystem.storage.WordListStorage
import com.example.bookanalyzer.data.filesystem.data_extractors.preview_parser.BookPreviewListParser
import dagger.Module
import dagger.Provides

@Module
class FileSystemModule {
    @Provides
    fun provideImageStorage(ctx: Context) = ImageStorage(ctx)

    @Provides
    fun provideWordListStorage(ctx: Context) = WordListStorage(ctx)

    @Provides
    fun provideBookPreviewListParser(ctx: Context) = BookPreviewListParser(ctx)

    @Provides
    fun provideBookAnalyzer(ctx: Context) = BookAnalyzer(ctx)
}