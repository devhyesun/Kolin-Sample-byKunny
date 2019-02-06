package com.devhyesun.kolinsample.di.ui

import com.devhyesun.kolinsample.api.GithubApi
import com.devhyesun.kolinsample.ui.repository.RepositoryViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideViewModelFactory(githubApi: GithubApi): RepositoryViewModelFactory =
        RepositoryViewModelFactory(githubApi)
}