package com.devhyesun.kolinsample.data

import android.arch.persistence.room.Room
import android.content.Context

class DatabaseProvider {
    private var instance: KolinSampleDatabase? = null

    fun provideSearchHistoryDao(context: Context): SearchHistoryDao = providerDatabase(context).searchHistoryDao()

    private fun providerDatabase(context: Context): KolinSampleDatabase {
        if (instance == null) {
            instance =
                    Room.databaseBuilder(context.applicationContext, KolinSampleDatabase::class.java, "kolin_sample.db")
                        .build()
        }

        return instance!!
    }
}