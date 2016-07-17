package com.nano.amal.popularmovies;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Amal Krishnan on 02-03-2016.
 */
public class DetailActivity extends AppCompatActivity {
    final String LOG_TAG2 = DetailActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_main);

        Log.d(LOG_TAG2, "Detail Activity");

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(getIntent().getExtras());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container2, detailFragment).commit();
            Log.d(LOG_TAG2, "Detail - Tablet");

        }

    }
}