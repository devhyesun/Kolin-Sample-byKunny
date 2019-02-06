package com.devhyesun.kolinsample.di.ui

import com.devhyesun.kolinsample.api.AuthApi
import com.devhyesun.kolinsample.data.AuthTokenProvider
import com.devhyesun.kolinsample.ui.signin.SignInViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class SignInModule {

    @Provides
    fun provideViewModelFactory(authApi: AuthApi, authTokenProvider: AuthTokenProvider): SignInViewModelFactory =
        SignInViewModelFactory(authApi, authTokenProvider)
}