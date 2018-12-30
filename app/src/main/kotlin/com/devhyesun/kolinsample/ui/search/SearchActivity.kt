package com.devhyesun.kolinsample.ui.search

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager

import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.model.GithubRepo
import com.devhyesun.kolinsample.api.model.RepoSearchResponse
import com.devhyesun.kolinsample.api.provideGithubApi
import com.devhyesun.kolinsample.ui.repository.RepositoryActivity
import kotlinx.android.synthetic.main.atv_search.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {

    private lateinit var menuSearch: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var searchAdapter: SearchAdapter

    private val api by lazy { provideGithubApi(this) }
    private lateinit var searchCall: Call<RepoSearchResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_search)

        searchAdapter = SearchAdapter()
        searchAdapter.setItemClickListener(this)

        with(rv_search_list) {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        menuSearch = menu.findItem(R.id.menu_search_query)

        searchView = (menuSearch.actionView as SearchView).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    updateTitle(query)
                    hideSoftKeyboard()
                    collapseSearchView()
                    searchRepository(query)

                    return true
                }

                override fun onQueryTextChange(s: String): Boolean {
                    return false
                }
            })
        }

        with(menuSearch) {
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                    if(searchView.query == "") {
                        finish()
                    }

                    return true
                }
            })

            expandActionView()
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.menu_search_query == item.itemId) {
            item.expandActionView()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(repository: GithubRepo) {
        val intent = Intent(this, RepositoryActivity::class.java).apply {
            putExtra(RepositoryActivity.KEY_USER_LOGIN, repository.owner.login)
            putExtra(RepositoryActivity.KEY_REPO_NAME, repository.name)
        }

        startActivity(intent)
    }

    private fun searchRepository(query: String) {
        clearResults()
        hideError()
        showProgress()

        searchCall = api.searchRepository(query)
        searchCall.enqueue(object : Callback<RepoSearchResponse> {
            override fun onResponse(call: Call<RepoSearchResponse>, response: Response<RepoSearchResponse>) {
                hideProgress()

                val searchResult = response.body()
                if (response.isSuccessful && searchResult != null) {
                    with(searchAdapter) {
                        setGithubRepoList(searchResult.items)
                        notifyDataSetChanged()
                    }

                    if (searchResult.totalCount == 0) {
                        showError(getString(R.string.no_search_result))
                    }
                } else {
                    showError("Not successful: " + response.message())
                }
            }

            override fun onFailure(call: Call<RepoSearchResponse>, t: Throwable) {
                hideProgress()
                showError(t.message)
            }
        })
    }

    private fun updateTitle(query: String) {
        supportActionBar?.run { subtitle = query }
    }

    private fun hideSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).run {
            hideSoftInputFromWindow(searchView.windowToken, 0)
        }
    }

    private fun collapseSearchView() {
        menuSearch.collapseActionView()
    }

    private fun clearResults() {
        with(searchAdapter) {
            clearGihubRepoList()
            notifyDataSetChanged()
        }
    }

    private fun showProgress() {
        pb_search.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        pb_search.visibility = View.GONE
    }

    private fun showError(message: String?) {
        with(tv_search_message) {
            text = message ?: "Unexpected error."
            visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        with(tv_search_message) {
            text = ""
            visibility = View.GONE
        }
    }
}
