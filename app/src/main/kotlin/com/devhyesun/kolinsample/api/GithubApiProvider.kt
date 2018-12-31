package com.devhyesun.kolinsample.api

import android.content.Context
import com.devhyesun.kolinsample.data.AuthTokenProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.io.IOException

fun provideAuthApi() = Retrofit.Builder()
    .baseUrl("https://github.com/")
    .client(provideOkHttpClient(provideLoggingInterceptor(), null))
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(AuthApi::class.java)


fun provideGithubApi(context: Context) = Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .client(
        provideOkHttpClient(
            provideLoggingInterceptor(),
            provideAuthInterceptor(provideAuthTokenProvider(context))
        )
    )
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(GithubApi::class.java)

private fun provideOkHttpClient(
    interceptor: HttpLoggingInterceptor,
    authInterceptor: AuthInterceptor?
) = OkHttpClient.Builder()
    .run {
        if (authInterceptor != null) {
            addInterceptor(authInterceptor)
        }
        addInterceptor(interceptor)
        build()
    }

private fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private fun provideAuthInterceptor(provider: AuthTokenProvider): AuthInterceptor {
    val token = provider.token ?: throw IllegalStateException("authToken cannot be null")

    return AuthInterceptor(token)
}

private fun provideAuthTokenProvider(context: Context) =
    AuthTokenProvider(context.applicationContext)

internal class AuthInterceptor(private val token: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain) = with(chain) {
        val request = request().newBuilder().run {
            addHeader("Authorization", "token $token")
            build()
        }
        proceed(request)
    }
}
