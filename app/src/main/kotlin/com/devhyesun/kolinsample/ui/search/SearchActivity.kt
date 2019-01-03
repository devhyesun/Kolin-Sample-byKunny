package com.devhyesun.kolinsample.ui.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.devhyesun.kolinsample.AutoClearedDisposable

import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.model.GithubRepo
import com.devhyesun.kolinsample.api.provideGithubApi
import com.devhyesun.kolinsample.extensions.plusAssign
import com.devhyesun.kolinsample.ui.repository.RepositoryActivity
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChangeEvents
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.atv_search.*
import org.jetbrains.anko.startActivity

class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {

    private lateinit var menuSearch: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var searchAdapter: SearchAdapter

    private val api by lazy { provideGithubApi(this) }
    private val disposables = AutoClearedDisposable(this)
    private val viewDisposable = AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_search)

        lifecycle += disposables
        lifecycle += viewDisposable

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

        with(menuSearch) {
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                    if (searchView.query == "") {
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
        startActivity<RepositoryActivity>(
            RepositoryActivity.KEY_USER_LOGIN to repository.owner.login,
            RepositoryActivity.KEY_REPO_NAME to repository.name
        )
    }

    private fun searchRepository(query: String) {
        disposables += api.searchRepository(query)
            .flatMap {
                if (it.totalCount == 0) {
                    Observable.error(IllegalStateException("No search result"))
                } else {
                    Observable.just(it.items)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                clearResults()
                hideError()
                showProgress()
            }
            .doOnTerminate { hideProgress() }
            .subscribe({ items ->
                with(searchAdapter) {
                    setGithubRepoList(items)
                    notifyDataSetChanged()
                }
            })
            { showError(it.message) }
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
