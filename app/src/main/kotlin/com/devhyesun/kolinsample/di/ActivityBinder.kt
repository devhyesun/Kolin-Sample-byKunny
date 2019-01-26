package com.devhyesun.kolinsample.di

import com.devhyesun.kolinsample.ui.main.MainActivity
import com.devhyesun.kolinsample.ui.repository.RepositoryActivity
import com.devhyesun.kolinsample.ui.search.SearchActivity
import com.devhyesun.kolinsample.ui.signin.SignInActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBinder {

    @ContributesAndroidInjector
    abstract fun bindSignInActivity(): SignInActivity

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindSearchActivity(): SearchActivity

    @ContributesAndroidInjector
    abstract fun bindRepositoryActivity(): RepositoryActivity
}