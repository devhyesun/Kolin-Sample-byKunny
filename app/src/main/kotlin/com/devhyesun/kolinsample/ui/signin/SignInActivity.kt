package com.devhyesun.kolinsample.ui.signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View

import android.widget.Toast
import com.devhyesun.kolinsample.BuildConfig
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.AuthApi
import com.devhyesun.kolinsample.api.model.GithubAccessToken
import com.devhyesun.kolinsample.api.provideAuthApi
import com.devhyesun.kolinsample.data.AuthTokenProvider
import com.devhyesun.kolinsample.ui.main.MainActivity
import kotlinx.android.synthetic.main.atv_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    private val api by lazy { provideAuthApi()}
    private val authTokenProvider by lazy { AuthTokenProvider(this) }
    private var accessTokenCall: Call<GithubAccessToken>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_sign_in)
        
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

        if (authTokenProvider.token != null) {
            lanchMainActivity()
        }
    }

    override fun onStop() {
        super.onStop()
        
        accessTokenCall?.run { cancel() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        showProgress()

        val uri = intent.data ?: throw IllegalArgumentException("No data exists")

        val code = uri.getQueryParameter("code") ?: throw IllegalStateException("No code exists")

        getAccessToken(code)
    }

    private fun getAccessToken(code: String) {
        showProgress()

        accessTokenCall = api.getAccessToken(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code)
        accessTokenCall!!.enqueue(object : Callback<GithubAccessToken> {
            override fun onResponse(call: Call<GithubAccessToken>, response: Response<GithubAccessToken>) {
                hideProgress()

                val token = response.body()
                if (response.isSuccessful && token != null) {
                    authTokenProvider.updateToken(token.accessToken)

                    lanchMainActivity()
                } else {
                    showError(IllegalStateException("Not successful : " + response.message()))
                }
            }

            override fun onFailure(call: Call<GithubAccessToken>, t: Throwable) {
                hideProgress()
                showError(t)
            }
        })
    }

    private fun showProgress() {
        btn_sign_in_start.visibility = View.GONE
        pb_sign_in.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        btn_sign_in_start.visibility = View.VISIBLE
        pb_sign_in.visibility = View.GONE
    }

    private fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
    }

    private fun lanchMainActivity() {
        startActivity(
            Intent(this@SignInActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
