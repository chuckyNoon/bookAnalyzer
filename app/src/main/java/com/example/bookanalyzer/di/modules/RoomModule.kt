package com.example.bookanalyzer.di.modules

import android.content.Context
import androidx.room.Room
import com.example.bookanalyzer.data.database.AppDataBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Provides
    fun provideBookAnalysisDao(appDataBase: AppDataBase) = appDataBase.bookAnalysisDao()

    @Provides
    fun provideBookPreviewDao(appDataBase: AppDataBase) = appDataBase.bookPreviewDao()

    @Provides
    @Singleton
    fun provideAppDataBase(ctx: Context): AppDataBase {
        return Room.databaseBuilder(ctx, AppDataBase::class.java, DB_NAME).build()
    }

}

const val DB_NAME = "myDb"