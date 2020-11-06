package com.example.bookanalyzer.di

import com.example.bookanalyzer.di.modules.*
import com.example.bookanalyzer.domain.repositories.BookInfoRepository
import com.example.bookanalyzer.domain.repositories.LoaderScreenRepository
import com.example.bookanalyzer.domain.repositories.StartScreenRepository
import com.example.bookanalyzer.domain.repositories.WordListRepository
import com.example.bookanalyzer.ui.activities.BookInfoActivity
import com.example.bookanalyzer.ui.activities.LoaderScreenActivity
import com.example.bookanalyzer.ui.activities.StartScreenActivity
import com.example.bookanalyzer.ui.activities.WordListActivity
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [RepositoryModule::class, RoomModule::class,
        ContextModule::class, FileSystemModule::class]
)
@Singleton
interface AppComponent {
    fun inject(activity: StartScreenActivity)
    fun inject(activity: BookInfoActivity)
    fun inject(activity: LoaderScreenActivity)
    fun inject(activity: WordListActivity)

    fun inject(repository: StartScreenRepository)
    fun inject(repository: LoaderScreenRepository)
    fun inject(repository: BookInfoRepository)
    fun inject(repository: WordListRepository)
}