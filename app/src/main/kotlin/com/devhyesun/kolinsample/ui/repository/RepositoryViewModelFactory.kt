package com.devhyesun.kolinsample.ui.repository

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.devhyesun.kolinsample.api.GithubApi

class RepositoryViewModelFactory(private val api: GithubApi): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RepositoryViewModel(api) as T
    }
}