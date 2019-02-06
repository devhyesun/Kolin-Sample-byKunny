package com.devhyesun.kolinsample.di.ui

import com.devhyesun.kolinsample.api.GithubApi
import com.devhyesun.kolinsample.data.SearchHistoryDao
import com.devhyesun.kolinsample.ui.search.SearchActivity
import com.devhyesun.kolinsample.ui.search.SearchAdapter
import com.devhyesun.kolinsample.ui.search.SearchViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class SearchModule {
    @Provides
    fun provideAdapter(activity: SearchActivity): SearchAdapter =
        SearchAdapter().apply { setItemClickListener(activity) }

    @Provides
    fun provideViewModuleFactory(githubApi: GithubApi, searchHistoryDao: SearchHistoryDao): SearchViewModelFactory =
        SearchViewModelFactory(githubApi, searchHistoryDao)
}