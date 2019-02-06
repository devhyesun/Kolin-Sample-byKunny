package com.devhyesun.kolinsample.ui.search

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.devhyesun.kolinsample.rx.AutoClearedDisposable

import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.model.GithubRepo
import com.devhyesun.kolinsample.extensions.plusAssign
import com.devhyesun.kolinsample.ui.repository.RepositoryActivity
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChangeEvents
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.atv_search.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class SearchActivity : DaggerAppCompatActivity(), SearchAdapter.ItemClickListener {

    @Inject lateinit var adapter: SearchAdapter
    @Inject lateinit var viewModelFactory: SearchViewModelFactory

    private lateinit var menuSearch: MenuItem
    private lateinit var searchView: SearchView

    private val disposables = AutoClearedDisposable(this)
    private val viewDisposable =
        AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)

    lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_search)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SearchViewModel::class.java]

        lifecycle += disposables
        lifecycle += viewDisposable

        with(rv_search_list) {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }

        viewDisposable += viewModel.searchResult
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { items ->
                with(adapter) {
                    if(items.isEmpty) {
                        clearGihubRepoList()
                    } else {
                        setGithubRepoList(items.value)
                    }

                    notifyDataSetChanged()
                }
            }

        viewDisposable += viewModel.message
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                if(message.isEmpty) {
                    hideError()
                } else {
                    showError(message.value)
                }
            }

        viewDisposable += viewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isLoading ->
                if(isLoading) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        menuSearch = menu.findItem(R.id.menu_search_query)

        searchView = (menuSearch.actionView as SearchView)

        viewDisposable += searchView.queryTextChangeEvents()
            .filter { it.isSubmitted }
            .map { it.queryText() }
            .filter { it.isNotEmpty() }
            .map { it.toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { query ->
                updateTitle(query)
                hideSoftKeyboard()
                collapseSearchView()
                searchRepository(query)
            }

        viewDisposable += viewModel.lastSearchKeyword
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { keyword ->
                if(keyword.isEmpty) {
                    menuSearch.expandActionView()
                } else {
                    updateTitle(keyword.value)
                }
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
        disposables += viewModel.addToSearchHistory(repository)
        startActivity<RepositoryActivity>(
            RepositoryActivity.KEY_USER_LOGIN to repository.owner.login,
            RepositoryActivity.KEY_REPO_NAME to repository.name
        )
    }

    private fun searchRepository(query: String) {
        disposables += viewModel.searchRepository(query)
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
