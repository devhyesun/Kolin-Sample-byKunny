package com.devhyesun.kolinsample.ui.main

import android.arch.lifecycle.*
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.devhyesun.kolinsample.rx.AutoActivatedDisposable
import com.devhyesun.kolinsample.rx.AutoClearedDisposable
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.api.model.GithubRepo
import com.devhyesun.kolinsample.extensions.plusAssign
import com.devhyesun.kolinsample.ui.repository.RepositoryActivity
import com.devhyesun.kolinsample.ui.search.SearchActivity
import com.devhyesun.kolinsample.ui.search.SearchAdapter
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.atv_main.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), SearchAdapter.ItemClickListener {
    @Inject lateinit var adapter: SearchAdapter
    @Inject lateinit var viewModelFactory: MainViewModelFactory

    private val disposables = AutoClearedDisposable(this)

    private val viewDisposable =
        AutoClearedDisposable(lifecycleOwner = this, alwaysClearOnStop = false)

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_main)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]

        lifecycle += disposables
        lifecycle += viewDisposable
        lifecycle += AutoActivatedDisposable(this) {
            viewModel.searchHistory
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { items ->
                    with(adapter) {
                        if (items.isEmpty) {
                            clearGihubRepoList()
                        } else {
                            setGithubRepoList(items.value)
                        }

                        notifyDataSetChanged()
                    }
                }
        }

        fab_main_search.setOnClickListener { startActivity<SearchActivity>() }

        with(rv_main_list) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        viewDisposable += viewModel.message 
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message -> 
                if(message.isEmpty) {
                    hideMessage()
                } else {
                    showMessage(message.value)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(R.id.menu_main_clear_all == item.itemId) {
            disposables += viewModel.clearSearchHistory()
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

    private fun showMessage(message: String?) {
        with(tv_main_message) {
            text = message ?: "Unexpected error."
            visibility = View.VISIBLE
        }
    }

    private fun hideMessage() {
        with(tv_main_message) {
            text = ""
            visibility = View.GONE
        }
    }
}
