package com.devhyesun.kolinsample.ui.search

import android.arch.lifecycle.ViewModel
import com.devhyesun.kolinsample.api.GithubApi
import com.devhyesun.kolinsample.api.model.GithubRepo
import com.devhyesun.kolinsample.data.SearchHistoryDao
import com.devhyesun.kolinsample.extensions.runOnIoScheduler
import com.devhyesun.kolinsample.util.SupportOptional
import com.devhyesun.kolinsample.util.emptyOptional
import com.devhyesun.kolinsample.util.optionalOf
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class SearchViewModel(private val api: GithubApi, private val searchHistoryDao: SearchHistoryDao) : ViewModel() {
    val searchResult: BehaviorSubject<SupportOptional<List<GithubRepo>>> = BehaviorSubject.createDefault(emptyOptional())
    val lastSearchKeyword: BehaviorSubject<SupportOptional<String>> = BehaviorSubject.createDefault(emptyOptional())
    val message: BehaviorSubject<SupportOptional<String>> = BehaviorSubject.create()
    val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun searchRepository(query: String): Disposable = api.searchRepository(query)
        .doOnNext { lastSearchKeyword.onNext(optionalOf(query)) }
        .flatMap {
            if(it.totalCount == 0) {
                Observable.error(IllegalStateException("No search result"))
            } else {
                Observable.just(it.items)
            }
        }
        .doOnSubscribe {
            searchResult.onNext(emptyOptional())
            message.onNext(emptyOptional())
            isLoading.onNext(true)
        }
        .doOnTerminate { isLoading.onNext(false) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ items ->
            searchResult.onNext(optionalOf(items))
        })
        { message.onNext(optionalOf(it.message ?: "Unexpected error")) }

    fun addToSearchHistory(repository: GithubRepo): Disposable = runOnIoScheduler { searchHistoryDao.add(repository) }
}