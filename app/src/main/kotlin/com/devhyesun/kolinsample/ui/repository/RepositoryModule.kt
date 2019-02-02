package com.devhyesun.kolinsample.ui.repository

import com.devhyesun.kolinsample.api.GithubApi
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideViewModelFactory(githubApi: GithubApi): RepositoryViewModelFactory =
        RepositoryViewModelFactory(githubApi)
}