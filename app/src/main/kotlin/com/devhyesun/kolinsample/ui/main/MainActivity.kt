package com.devhyesun.kolinsample.ui.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.ui.search.SearchActivity
import kotlinx.android.synthetic.main.atv_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_main)

        fab_main_search.setOnClickListener { startActivity(Intent(this@MainActivity, SearchActivity::class.java)) }
    }
}
