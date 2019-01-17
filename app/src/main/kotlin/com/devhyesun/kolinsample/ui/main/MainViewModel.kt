package com.devhyesun.kolinsample.ui.main

import android.arch.lifecycle.ViewModel
import com.devhyesun.kolinsample.api.model.GithubRepo
import com.devhyesun.kolinsample.data.SearchHistoryDao
import com.devhyesun.kolinsample.extensions.runOnIoScheduler
import com.devhyesun.kolinsample.util.SupportOptional
import com.devhyesun.kolinsample.util.emptyOptional
import com.devhyesun.kolinsample.util.optionalOf
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MainViewModel(private val searchHistoryDao: SearchHistoryDao): ViewModel() {
    val searchHistory: Flowable<SupportOptional<List<GithubRepo>>>
        get() = searchHistoryDao.getHistory()
            .map { optionalOf(it) }
            .doOnNext { optional ->
                if(optional.value.isEmpty()) {
                    message.onNext(optionalOf("No recent repositories."))
                } else {
                    message.onNext(emptyOptional())
                }
            }
            .doOnError { message.onNext(optionalOf(it.message ?: "Unexpected error")) }
            .onErrorReturn { emptyOptional() }

    val message: BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()

    fun clearSearchHistory(): Disposable = runOnIoScheduler { searchHistoryDao.clearAll() }
}