package com.example.bookanalyzer.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ContextModule(val ctx: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return ctx
    }
}