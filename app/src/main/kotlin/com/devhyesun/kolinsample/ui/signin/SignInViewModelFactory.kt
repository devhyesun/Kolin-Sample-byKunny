package com.devhyesun.kolinsample.ui.signin

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.devhyesun.kolinsample.api.AuthApi
import com.devhyesun.kolinsample.data.AuthTokenProvider

class SignInViewModelFactory(val api: AuthApi, val authTokenProvider: AuthTokenProvider) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CASE")
        return SignInViewModel(api, authTokenProvider) as T
    }
}