package com.devhyesun.kolinsample.ui.repository

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View

import com.bumptech.glide.Glide
import com.devhyesun.kolinsample.rx.AutoClearedDisposable
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.extensions.plusAssign
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.atv_repository.*

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class RepositoryActivity : DaggerAppCompatActivity() {
    companion object {

        const val KEY_USER_LOGIN = "user_login"
        const val KEY_REPO_NAME = "repo_name"
    }

    @Inject lateinit var viewModelFactory: RepositoryViewModelFactory

    private val disposables = AutoClearedDisposable(this)
    private val viewDisposables =
        AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)

    lateinit var viewModel: RepositoryViewModel

    private val dateFormatInResponse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    private val dateFormatToShow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_repository)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[RepositoryViewModel::class.java]

        lifecycle += disposables
        lifecycle += viewDisposables

        viewDisposables += viewModel.repository
            .filter { !it.isEmpty }
            .map { it.value }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { repository ->
                Glide.with(this@RepositoryActivity)
                    .load(repository.owner.avatarUrl)
                    .into(iv_repository_profile)
                tv_repository_name.text = repository.fullName
                tv_repository_star.text = resources.getQuantityString(R.plurals.star, repository.stars, repository.stars)
                if(repository.description == null) {
                    tv_repository_description.setText(R.string.no_description_provided)
                } else {
                    tv_repository_description.text = repository.description
                }
                if(repository.language == null) {
                    tv_repository_language.setText(R.string.no_language_specified)
                } else {
                    tv_repository_language.text = repository.language
                }

                try {
                    val lastUpdate = dateFormatInResponse.parse(repository.updateAt)
                    tv_repository_last_update.text = dateFormatToShow.format(lastUpdate)
                } catch (e: ParseException) {
                    tv_repository_last_update.text = getString(R.string.unknown)
                }
            }

        viewDisposables += viewModel.message
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message -> showError(message) }

        viewDisposables += viewModel.isContentVisible
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { visible -> setContentVisible(visible) }

        viewDisposables += viewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isLoading ->
                if(isLoading) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }

        val login =
            intent.getStringExtra(KEY_USER_LOGIN) ?: throw IllegalArgumentException("No login info exists int extras")

        val repo =
            intent.getStringExtra(KEY_REPO_NAME) ?: throw IllegalArgumentException("No repo info exists in extras")

        disposables += viewModel.requestRepositoryInfo(login, repo)

    }

    private fun showProgress() {
        cl_repository_content.visibility = View.GONE
        pb_repository.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        pb_repository.visibility = View.GONE
    }

    private fun setContentVisible(show: Boolean) {
        cl_repository_content.visibility = if(show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String?) {
        with(tv_repository_message) {
            text = message ?: "Unexpected error."
            visibility = View.VISIBLE
        }
    }
}
