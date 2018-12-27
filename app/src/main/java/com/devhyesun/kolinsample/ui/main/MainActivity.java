package com.devhyesun.kolinsample.ui.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.devhyesun.kolinsample.R;
import com.devhyesun.kolinsample.ui.search.SearchActivity;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_main);

        fabSearch = findViewById(R.id.fab_main_search);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
    }
}
