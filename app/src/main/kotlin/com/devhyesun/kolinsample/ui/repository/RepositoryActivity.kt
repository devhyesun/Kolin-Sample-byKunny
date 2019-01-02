package com.devhyesun.kolinsample.ui.repository

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.bumptech.glide.Glide
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.model.GithubRepo
import com.devhyesun.kolinsample.api.provideGithubApi
import com.devhyesun.kolinsample.extensions.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.atv_repository.*

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryActivity : AppCompatActivity() {
    companion object {

        const val KEY_USER_LOGIN = "user_login"
        const val KEY_REPO_NAME = "repo_name"
    }

    private val api by lazy { provideGithubApi(this) }
    private val disposables = CompositeDisposable()

    internal val dateFormatInResponse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    internal val dateFormatToShow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_repository)

        val login =
            intent.getStringExtra(KEY_USER_LOGIN) ?: throw IllegalArgumentException("No login info exists int extras")

        val repo =
            intent.getStringExtra(KEY_REPO_NAME) ?: throw IllegalArgumentException("No repo info exists in extras")

        showRepositoryInfo(login, repo)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun showRepositoryInfo(login: String, repoName: String) {
        disposables += api.getRepository(login, repoName)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress() }
            .doOnError { hideProgress(false) }
            .doOnComplete { hideProgress(true) }
            .subscribe({ repo ->
                Glide.with(this@RepositoryActivity)
                    .load(repo.owner.avatarUrl)
                    .into(iv_repository_profile)

                tv_repository_name.text = repo.fullName
                tv_repository_star.text = resources.getQuantityString(R.plurals.star, repo.stars, repo.stars)
                if(repo.description == null) {
                    tv_repository_description.setText(R.string.no_description_provided)
                } else {
                    tv_repository_description.text = repo.description
                }
                if(repo.language == null) {
                    tv_repository_language.setText(R.string.no_language_specified)
                } else {
                    tv_repository_language.text = repo.language
                }

                try {
                    val lastUpdate = dateFormatInResponse.parse(repo.updateAt)
                    tv_repository_last_update.text = dateFormatToShow.format(lastUpdate)
                } catch (e: ParseException) {
                    tv_repository_last_update.setText(R.string.unknown)
                }
            })
            { showError(it.message) }
    }

    private fun showProgress() {
        cl_repository_content.visibility = View.GONE
        pb_repository.visibility = View.VISIBLE
    }

    private fun hideProgress(isSucceed: Boolean) {
        cl_repository_content.visibility = if (isSucceed) View.VISIBLE else View.GONE
        pb_repository.visibility = View.GONE
    }

    private fun showError(message: String?) {
        with(tv_repository_message) {
            text = message ?: "Unexpected error."
            visibility = View.VISIBLE
        }
    }
}
