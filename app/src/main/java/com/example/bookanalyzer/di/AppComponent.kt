package com.example.bookanalyzer.di

import com.example.bookanalyzer.di.modules.*
import com.example.bookanalyzer.domain.repositories.BookAnalysisRepository
import com.example.bookanalyzer.domain.repositories.LoaderScreenRepository
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.domain.repositories.WordListRepository
import com.example.bookanalyzer.ui.fragments.AnalysisProcessFragment
import com.example.bookanalyzer.ui.fragments.AnalysisResultFragment
import com.example.bookanalyzer.ui.fragments.BooksFragment
import com.example.bookanalyzer.ui.fragments.WordsFragment
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [RepositoryModule::class, RoomModule::class,
        ContextModule::class, FileSystemModule::class]
)
@Singleton
interface AppComponent {
    fun inject(fragment: BooksFragment)
    fun inject(fragment: AnalysisResultFragment)
    fun inject(fragment: AnalysisProcessFragment)
    fun inject(fragment: WordsFragment)

    fun inject(repository: StartScreenRepository)
    fun inject(repository: LoaderScreenRepository)
    fun inject(repository: BookAnalysisRepository)
    fun inject(repository: WordListRepository)
}