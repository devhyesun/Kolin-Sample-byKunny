package com.devhyesun.kolinsample.ui.main

import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.devhyesun.kolinsample.R
import com.devhyesun.kolinsample.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {

    private lateinit var fabSearch: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.atv_main)

        fabSearch = findViewById(R.id.fab_main_search)
        fabSearch.setOnClickListener { startActivity(Intent(this@MainActivity, SearchActivity::class.java)) }
    }
}
