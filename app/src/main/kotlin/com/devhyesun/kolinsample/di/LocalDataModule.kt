package com.devhyesun.kolinsample.di

import android.arch.persistence.room.Room
import android.content.Context
import com.devhyesun.kolinsample.data.AuthTokenProvider
import com.devhyesun.kolinsample.data.KolinSampleDatabase
import com.devhyesun.kolinsample.data.SearchHistoryDao
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class LocalDataModule {

    @Provides
    @Singleton
    fun provideAuthTokenProvider(@Named("appContext") context: Context): AuthTokenProvider = AuthTokenProvider(context)


    @Provides
    @Singleton
    fun provideSearchHistoryDao(db: KolinSampleDatabase): SearchHistoryDao = db.searchHistoryDao()

    @Provides
    @Singleton
    fun provideDatabase(@Named("appContext") context: Context): KolinSampleDatabase =
        Room.databaseBuilder(context, KolinSampleDatabase::class.java, "kotlin_sample.db").build()
}