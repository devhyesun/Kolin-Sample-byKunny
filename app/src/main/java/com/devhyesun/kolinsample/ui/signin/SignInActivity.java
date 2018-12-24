package com.devhyesun.kolinsample.ui.signin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import android.widget.Toast;
import com.devhyesun.kolinsample.BuildConfig;
import com.devhyesun.kolinsample.R;
import com.devhyesun.kolinsample.api.AuthApi;
import com.devhyesun.kolinsample.api.GithubApiProvider;
import com.devhyesun.kolinsample.api.model.GithubAccessToken;
import com.devhyesun.kolinsample.data.AuthTokenProvider;
import com.devhyesun.kolinsample.ui.main.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    private Button btnStart;
    private ProgressBar progressBar;

    private AuthApi api;
    private AuthTokenProvider authTokenProvider;
    private Call<GithubAccessToken> accessTokenCall;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_sign_in);

        btnStart = findViewById(R.id.btn_sign_in_start);
        progressBar = findViewById(R.id.pb_sign_in);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri authUri = new Uri.Builder().scheme("https").authority("github.com")
                        .appendPath("login")
                        .appendPath("oauth")
                        .appendPath("authorize")
                        .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                        .build();

                CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                intent.launchUrl(SignInActivity.this, authUri);
            }
        });

        api = GithubApiProvider.provideAuthApi();
        authTokenProvider = new AuthTokenProvider(this);

        if (authTokenProvider.getToken() != null) {
            lanchMainActivity();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        showProgress();

        Uri uri = intent.getData();
        if (uri == null) {
            throw new IllegalArgumentException("No data exists");
        }

        String code = uri.getQueryParameter("code");
        if (code == null) {
            throw new IllegalStateException("No code exists");
        }

        getAccessToken(code);
    }

    private void getAccessToken(@NonNull String code) {
        showProgress();

        accessTokenCall = api.getAccessToken(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_CLIENT_SECRET, code);
        accessTokenCall.enqueue(new Callback<GithubAccessToken>() {
            @Override
            public void onResponse(Call<GithubAccessToken> call, Response<GithubAccessToken> response) {
                hideProgress();

                GithubAccessToken token = response.body();
                if (response.isSuccessful() && token != null) {
                    authTokenProvider.updateToken(token.accessToken);

                    lanchMainActivity();
                } else {
                    showError(new IllegalStateException("Not successful : " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<GithubAccessToken> call, Throwable t) {
                hideProgress();
                showError(t);
            }
        });
    }

    private void showProgress() {
        btnStart.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        btnStart.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void showError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void lanchMainActivity() {
        startActivity(new Intent(SignInActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
