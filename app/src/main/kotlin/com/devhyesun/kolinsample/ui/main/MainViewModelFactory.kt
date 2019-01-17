package com.devhyesun.kolinsample.ui.main

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.devhyesun.kolinsample.data.SearchHistoryDao

class MainViewModelFactory(val searchHistoryDao: SearchHistoryDao):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(searchHistoryDao) as T
    }
}