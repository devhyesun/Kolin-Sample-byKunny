package com.devhyesun.kolinsample.ui.signin

import android.arch.lifecycle.ViewModel
import com.devhyesun.kolinsample.api.AuthApi
import com.devhyesun.kolinsample.data.AuthTokenProvider
import com.devhyesun.kolinsample.util.SupportOptional
import com.devhyesun.kolinsample.util.optionalOf
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class SignInViewModel(private val api: AuthApi, private val authTokenProvider: AuthTokenProvider) : ViewModel() {

    val accessToken: BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()
    val message: PublishSubject<String> = PublishSubject.create()
    val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun loadAccessToken(): Disposable = Single.fromCallable { optionalOf(authTokenProvider.token) }
        .subscribeOn(Schedulers.io())
        .subscribe(Consumer<SupportOptional<String>> {
            accessToken.onNext(it)
        })

    fun requestAccessToken(clientId: String, clienSecret: String, code: String): Disposable =
        api.getAccessToken(clientId, clienSecret, code)
            .map { it.accessToken }
            .doOnSubscribe { isLoading.onNext(true) }
            .doOnTerminate { isLoading.onNext(false) }
            .subscribe({ token ->
                authTokenProvider.updateToken(token)
                accessToken.onNext(optionalOf(token))
            })
            { message.onNext(it.message ?: "Unexpected error") }
}