package com.devhyesun.kolinsample.ui.signin

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.devhyesun.kolinsample.AutoClearedDisposable

import com.devhyesun.kolinsample.BuildConfig
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.provideAuthApi
import com.devhyesun.kolinsample.data.AuthTokenProvider
import com.devhyesun.kolinsample.extensions.plusAssign
import com.devhyesun.kolinsample.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.atv_sign_in.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import org.jetbrains.anko.newTask

class SignInActivity : AppCompatActivity() {
    
    private val disposables = AutoClearedDisposable(this)
    private val viewDisposables = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)

    private val viewModelFactory by lazy {
        SignInViewModelFactory(provideAuthApi(), AuthTokenProvider(this))
    }

    lateinit var viewModel: SignInViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_sign_in)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SignInViewModel::class.java]

        lifecycle += disposables
        lifecycle += viewDisposables

        btn_sign_in_start.setOnClickListener {
            val authUri = Uri.Builder().scheme("https").authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                .build()

            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this@SignInActivity, authUri)
        }

        viewDisposables += viewModel.accessToken
            .filter { !it.isEmpty }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { launchMainActivity() }

        viewDisposables += viewModel.message
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message -> showError(message) }

        viewDisposables += viewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isLoading ->
                if(isLoading) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }

        disposables += viewModel.loadAccessToken()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        showProgress()

        val uri = intent.data ?: throw IllegalArgumentException("No data exists")

        val code = uri.getQueryParameter("code") ?: throw IllegalStateException("No code exists")

        getAccessToken(code)
    }

    private fun getAccessToken(code: String) {
        disposables += viewModel.requestAccessToken(
            BuildConfig.GITHUB_CLIENT_ID,
            BuildConfig.GITHUB_CLIENT_SECRET
        )
    }

    private fun showProgress() {
        btn_sign_in_start.visibility = View.GONE
        pb_sign_in.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btn_sign_in_start.visibility = View.VISIBLE
        pb_sign_in.visibility = View.GONE
    }

    private fun showError(message: String) {
        longToast(message)
    }

    private fun launchMainActivity() {
        startActivity(intentFor<MainActivity>().clearTask().newTask())
    }
}
