package com.example.bookanalyzer.di.modules

import com.example.bookanalyzer.data.filesystem.data_extractors.analyzer.BookAnalyzer
import com.example.bookanalyzer.data.database.daos.BookAnalysisDao
import com.example.bookanalyzer.data.database.daos.BookPreviewDao
import com.example.bookanalyzer.data.filesystem.storage.ImageStorage
import com.example.bookanalyzer.data.filesystem.storage.WordListStorage
import com.example.bookanalyzer.data.filesystem.data_extractors.preview_parser.BookPreviewListParser
import com.example.bookanalyzer.domain.repositories.BookInfoRepository
import com.example.bookanalyzer.domain.repositories.LoaderScreenRepository
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.domain.repositories.WordListRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideStartScreenRepository(
        analysisDao: BookAnalysisDao,
        previewDao: BookPreviewDao,
        bookPreviewListParser: BookPreviewListParser,
        imageStorage: ImageStorage
    ) = StartScreenRepository(analysisDao, previewDao, bookPreviewListParser, imageStorage)

    @Provides
    fun provideBookInfoRepository(analysisDao: BookAnalysisDao) = BookInfoRepository(analysisDao)

    @Provides
    fun provideLoaderScreenRepository(
        wordListStorage: WordListStorage,
        analyzer: BookAnalyzer,
        analysisDao: BookAnalysisDao
    ) = LoaderScreenRepository(wordListStorage, analyzer, analysisDao)

    @Provides
    fun provideWordListRepository(wordListStorage: WordListStorage, analysisDao: BookAnalysisDao) =
        WordListRepository(wordListStorage, analysisDao)
}