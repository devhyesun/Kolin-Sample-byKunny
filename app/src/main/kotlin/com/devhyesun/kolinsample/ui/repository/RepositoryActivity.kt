package com.devhyesun.kolinsample.ui.repository

import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.bumptech.glide.Glide
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.GithubApi
import com.devhyesun.kolinsample.api.GithubApiProvider
import com.devhyesun.kolinsample.api.model.GithubRepo

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryActivity : AppCompatActivity() {

    private lateinit var clContent: ConstraintLayout
    private lateinit var ivProfile: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvStars: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvLanguage: TextView
    private lateinit var tvLastUpdate: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMessage: TextView

    private lateinit var api: GithubApi
    private lateinit var githubRepoCall: Call<GithubRepo>

    internal var dateFormatInResponse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    internal var dateFormatToShow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_repository)

        clContent = findViewById(R.id.cl_repository_content)
        ivProfile = findViewById(R.id.iv_repository_profile)
        tvName = findViewById(R.id.tv_repository_name)
        tvStars = findViewById(R.id.tv_repository_star)
        tvDescription = findViewById(R.id.tv_repository_description)
        tvLanguage = findViewById(R.id.tv_repository_language)
        tvLastUpdate = findViewById(R.id.tv_repository_last_update)
        progressBar = findViewById(R.id.pb_repository)
        tvMessage = findViewById(R.id.tv_repository_message)

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
                        .into(ivProfile)

                    tvName.text = githubRepo.fullName
                    tvStars.text = resources.getQuantityString(R.plurals.star, githubRepo.stars, githubRepo.stars)

                    if (githubRepo.description == null) {
                        tvDescription.text = getString(R.string.no_description_provided)
                    } else {
                        tvDescription.text = githubRepo.description
                    }

                    if (githubRepo.language == null) {
                        tvLanguage.text = getString(R.string.no_language_specified)
                    } else {
                        tvLanguage.text = githubRepo.language
                    }

                    try {
                        val lastUpdate = dateFormatInResponse.parse(githubRepo.updateAt)
                        tvLastUpdate.text = dateFormatToShow.format(lastUpdate)
                    } catch (e: ParseException) {
                        tvLastUpdate.text = getString(R.string.unknown)
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
        clContent.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress(isSucceed: Boolean) {
        clContent.visibility = if (isSucceed) View.VISIBLE else View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showError(message: String?) {
        tvMessage.text = message ?: "Unexpected error."
        tvMessage.visibility = View.VISIBLE
    }

    companion object {

        val KEY_USER_LOGIN = "user_login"
        val KEY_REPO_NAME = "repo_name"
    }
}
