package com.devhyesun.kolinsample.ui.repository;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devhyesun.kolinsample.R;
import com.devhyesun.kolinsample.api.GithubApi;
import com.devhyesun.kolinsample.api.GithubApiProvider;
import com.devhyesun.kolinsample.api.model.GithubRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositoryActivity extends AppCompatActivity {

    public static final String KEY_USER_LOGIN = "user_login";
    public static final String KEY_REPO_NAME = "repo_name";

    ConstraintLayout clContent;
    ImageView ivProfile;
    TextView tvName;
    TextView tvStars;
    TextView tvDescription;
    TextView tvLanguage;
    TextView tvLastUpdate;
    ProgressBar progressBar;
    TextView tvMessage;

    GithubApi api;
    Call<GithubRepo> githubRepoCall;

    SimpleDateFormat dateFormatInResponse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault());
    SimpleDateFormat dateFormatToShow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_repository);

        clContent = findViewById(R.id.cl_repository_content);
        ivProfile = findViewById(R.id.iv_repository_profile);
        tvName = findViewById(R.id.tv_repository_name);
        tvStars = findViewById(R.id.tv_repository_star);
        tvDescription = findViewById(R.id.tv_repository_description);
        tvLanguage = findViewById(R.id.tv_repository_language);
        tvLastUpdate = findViewById(R.id.tv_repository_last_update);
        progressBar = findViewById(R.id.pb_repository);
        tvMessage = findViewById(R.id.tv_repository_message);

        api = GithubApiProvider.provideGithubApi(this);

        String login = getIntent().getStringExtra(KEY_USER_LOGIN);
        if(login == null) {
            throw new IllegalArgumentException("No login info exists int extras");
        }

        String repo = getIntent().getStringExtra(KEY_REPO_NAME);
        if(repo == null) {
            throw new IllegalArgumentException("No repo info exists in extras");
        }

        showRepositoryInfo(login, repo);
    }

    private void showRepositoryInfo(String login, String repoName) {
        showProgress();

        githubRepoCall = api.getRepository(login, repoName);
        githubRepoCall.enqueue(new Callback<GithubRepo>() {
            @Override
            public void onResponse(Call<GithubRepo> call, Response<GithubRepo> response) {
                hideProgress(true);

                GithubRepo githubRepo = response.body();
                if(response.isSuccessful() && githubRepo != null) {
                    Glide.with(RepositoryActivity.this)
                            .load(githubRepo.owner.avatarUrl)
                            .into(ivProfile);

                    tvName.setText(githubRepo.fullName);
                    tvStars.setText(getResources().getQuantityString(R.plurals.star, githubRepo.stars, githubRepo.stars));

                    if(githubRepo.description == null) {
                        tvDescription.setText(getString(R.string.no_description_provided));
                    } else {
                        tvDescription.setText(githubRepo.description);
                    }

                    if(githubRepo.language == null) {
                        tvLanguage.setText(getString(R.string.no_language_specified));
                    } else {
                        tvLanguage.setText(githubRepo.language);
                    }

                    try {
                        Date lastUpdate = dateFormatInResponse.parse(githubRepo.updateAt);
                        tvLastUpdate.setText(dateFormatToShow.format(lastUpdate));
                    } catch (ParseException e) {
                        tvLastUpdate.setText(getString(R.string.unknown));
                    }
                } else {
                    showError("Not successful : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GithubRepo> call, Throwable t) {
                hideProgress(false);
                showError(t.getMessage());
            }
        });
    }

    private void showProgress() {
        clContent.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress(boolean isSucceed) {
        clContent.setVisibility(isSucceed ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        tvMessage.setText(message);
        tvMessage.setVisibility(View.VISIBLE);
    }
}
