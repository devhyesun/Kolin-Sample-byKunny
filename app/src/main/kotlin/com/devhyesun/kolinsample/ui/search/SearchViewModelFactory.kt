package com.devhyesun.kolinsample.ui.search

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.devhyesun.kolinsample.api.GithubApi
import com.devhyesun.kolinsample.data.SearchHistoryDao

class SearchViewModelFactory(private val api: GithubApi, val searchHistoryDao: SearchHistoryDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SearchViewModel(api, searchHistoryDao) as T
    }

}