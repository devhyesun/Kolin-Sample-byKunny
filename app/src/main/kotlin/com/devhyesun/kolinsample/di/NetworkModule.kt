package com.devhyesun.kolinsample.di

import com.devhyesun.kolinsample.api.AuthInterceptor
import com.devhyesun.kolinsample.data.AuthTokenProvider
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.IllegalStateException
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Named("unauthorized")
    @Singleton
    fun provideUnauthorizedOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Named("authorized")
    @Singleton
    fun provideAuthorizedOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(provider: AuthTokenProvider): AuthInterceptor {
        val token = provider.token
            ?: throw IllegalStateException("authToken cannot be null")

        return AuthInterceptor(token)
    }
}