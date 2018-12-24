package com.devhyesun.kolinsample.ui.search;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devhyesun.kolinsample.R;
import com.devhyesun.kolinsample.api.GithubApi;
import com.devhyesun.kolinsample.api.GithubApiProvider;
import com.devhyesun.kolinsample.api.model.GithubRepo;
import com.devhyesun.kolinsample.api.model.RepoSearchResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.ItemClickListener {
    private RecyclerView rvList;
    private ProgressBar progressBar;
    private TextView tvMessage;

    private MenuItem menuSearch;
    private SearchView searchView;
    private SearchAdapter searchAdapter;

    private GithubApi api;
    private Call<RepoSearchResponse> searchCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_search);

        rvList = findViewById(R.id.rv_search_list);
        progressBar = findViewById(R.id.pb_search);
        tvMessage = findViewById(R.id.tv_search_message);

        searchAdapter = new SearchAdapter();
        searchAdapter.setItemClickListener(this);

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(searchAdapter);

        api = GithubApiProvider.provideGithubApi(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        menuSearch = menu.findItem(R.id.menu_search_query);

        searchView = (SearchView) menuSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateTitle(query);
                hideSoftKeyboard();
                collapseSearchView();
                searchRepository(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(R.id.menu_search_query == item.getItemId()) {
            item.expandActionView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(GithubRepo repository) {
    }

    private void searchRepository(String query) {
        clearResults();
        hideError();
        showProgress();

        searchCall = api.searchRepository(query);
        searchCall.enqueue(new Callback<RepoSearchResponse>() {
            @Override
            public void onResponse(Call<RepoSearchResponse> call, Response<RepoSearchResponse> response) {
                hideProgress();

                RepoSearchResponse searchResult = response.body();
                if(response.isSuccessful() && searchResult != null) {
                    searchAdapter.setGithubRepoList(searchResult.items);
                    searchAdapter.notifyDataSetChanged();

                    if(searchResult.totalCount == 0) {
                        showError(getString(R.string.no_search_result));
                    }
                } else {
                    showError("Not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RepoSearchResponse> call, Throwable t) {
                hideProgress();
                showError(t.getMessage());
            }
        });
    }

    private void updateTitle(String query) {
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setSubtitle(query);
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    private void collapseSearchView() {
        menuSearch.collapseActionView();
    }

    private void clearResults() {
        searchAdapter.clearGihubRepoList();
        searchAdapter.notifyDataSetChanged();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        tvMessage.setText(message);
        tvMessage.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvMessage.setText("");
        tvMessage.setVisibility(View.GONE);
    }
}
