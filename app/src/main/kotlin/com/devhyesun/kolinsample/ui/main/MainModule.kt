package com.devhyesun.kolinsample.ui.main

import com.devhyesun.kolinsample.data.SearchHistoryDao
import com.devhyesun.kolinsample.ui.search.SearchAdapter
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @Provides
    fun provideAdapter(activity: MainActivity): SearchAdapter = SearchAdapter().apply { setItemClickListener(activity) }

    @Provides
    fun provideViewModelFactory(searchHistoryDao: SearchHistoryDao): MainViewModelFactory = MainViewModelFactory(searchHistoryDao)
}