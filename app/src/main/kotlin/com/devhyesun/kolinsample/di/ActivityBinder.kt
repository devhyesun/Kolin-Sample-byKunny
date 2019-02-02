package com.devhyesun.kolinsample.di

import com.devhyesun.kolinsample.ui.main.MainActivity
import com.devhyesun.kolinsample.ui.main.MainModule
import com.devhyesun.kolinsample.ui.repository.RepositoryActivity
import com.devhyesun.kolinsample.ui.search.SearchActivity
import com.devhyesun.kolinsample.ui.signin.SignInActivity
import com.devhyesun.kolinsample.ui.signin.SignInModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBinder {

    @ContributesAndroidInjector(modules = [SignInModule::class])
    abstract fun bindSignInActivity(): SignInActivity

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindSearchActivity(): SearchActivity

    @ContributesAndroidInjector
    abstract fun bindRepositoryActivity(): RepositoryActivity
}