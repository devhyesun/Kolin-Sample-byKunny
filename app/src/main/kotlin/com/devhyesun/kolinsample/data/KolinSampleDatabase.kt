package com.devhyesun.kolinsample.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.devhyesun.kolinsample.api.model.GithubRepo

@Database(entities = [GithubRepo::class], version = 1)
abstract class KolinSampleDatabase: RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}