package com.devhyesun.kolinsample.ui.repository

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.bumptech.glide.Glide
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.GithubApi
import com.devhyesun.kolinsample.api.GithubApiProvider
import com.devhyesun.kolinsample.api.model.GithubRepo
import kotlinx.android.synthetic.main.atv_repository.*

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryActivity : AppCompatActivity() {

    private lateinit var api: GithubApi
    private lateinit var githubRepoCall: Call<GithubRepo>

    internal var dateFormatInResponse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    internal var dateFormatToShow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_repository)

        api = GithubApiProvider.provideGithubApi(this)

        val login =
            intent.getStringExtra(KEY_USER_LOGIN) ?: throw IllegalArgumentException("No login info exists int extras")

        val repo =
            intent.getStringExtra(KEY_REPO_NAME) ?: throw IllegalArgumentException("No repo info exists in extras")

        showRepositoryInfo(login, repo)
    }

    private fun showRepositoryInfo(login: String, repoName: String) {
        showProgress()

        githubRepoCall = api.getRepository(login, repoName)
        githubRepoCall.enqueue(object : Callback<GithubRepo> {
            override fun onResponse(call: Call<GithubRepo>, response: Response<GithubRepo>) {
                hideProgress(true)

                val githubRepo = response.body()
                if (response.isSuccessful && githubRepo != null) {
                    Glide.with(this@RepositoryActivity)
                        .load(githubRepo.owner.avatarUrl)
                        .into(iv_repository_profile)

                    tv_repository_name.text = githubRepo.fullName
                    tv_repository_star.text = resources.getQuantityString(R.plurals.star, githubRepo.stars, githubRepo.stars)

                    if (githubRepo.description == null) {
                        tv_repository_description.text = getString(R.string.no_description_provided)
                    } else {
                        tv_repository_description.text = githubRepo.description
                    }

                    if (githubRepo.language == null) {
                        tv_repository_language.text = getString(R.string.no_language_specified)
                    } else {
                        tv_repository_language.text = githubRepo.language
                    }

                    try {
                        val lastUpdate = dateFormatInResponse.parse(githubRepo.updateAt)
                        tv_repository_last_update.text = dateFormatToShow.format(lastUpdate)
                    } catch (e: ParseException) {
                        tv_repository_last_update.text = getString(R.string.unknown)
                    }

                } else {
                    showError("Not successful : " + response.message())
                }
            }

            override fun onFailure(call: Call<GithubRepo>, t: Throwable) {
                hideProgress(false)
                showError(t.message)
            }
        })
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
        tv_repository_message.text = message ?: "Unexpected error."
        tv_repository_message.visibility = View.VISIBLE
    }

    companion object {

        val KEY_USER_LOGIN = "user_login"
        val KEY_REPO_NAME = "repo_name"
    }
}
