package com.nano.amal.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public List<movie_json> movieArr = new ArrayList<movie_json>();
    GridView gridView,gridViewFav;
    SharedPreferences sharedPref;
    String sortByValue;
    public static final String LOG = MainActivity.class.getSimpleName();
    private Boolean mTabletMode = false;
    Bundle bundle=new Bundle();
    private static final int URL_LOADER = 0;
    MovieCursorAdapter cursorAdapter;
    CursorLoader cursorLoader;

    public boolean isTablet() {
        return mTabletMode;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        getSupportLoaderManager().initLoader(URL_LOADER,null,this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = this.getSharedPreferences("PrefData", Context.MODE_PRIVATE);
        sortByValue = sharedPref.getString("SORT_VALUE", getResources().getString(R.string.popularity_desc));

        cursorAdapter = new MovieCursorAdapter(getApplicationContext(), null, 0);

        if(findViewById(R.id.container)!= null)
            mTabletMode=true;

        if(isTablet()||findViewById(R.id.container)!= null)
        {
            if(savedInstanceState==null) {
            gridView=(GridView)findViewById(R.id.photo_view2);
                gridViewFav=(GridView)findViewById(R.id.photo_view2);
            new Parser_Async().execute(sortByValue);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        movie_json Movie = new movie_json(movieArr.get(position).movieName, movieArr.get(position).posterPath,
                                movieArr.get(position).movieOverview, movieArr.get(position).movieRelDate,
                                movieArr.get(position).movieVoteAvg, movieArr.get(position).movieID);
                        bundle.putParcelable("M", Movie);

                        DetailFragment detailFragment = new DetailFragment();
                        detailFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, detailFragment).addToBackStack(null).commit();

                    }
                });
            }
        }else{
            gridView= (GridView) findViewById(R.id.photo_view);
            gridViewFav= (GridView) findViewById(R.id.photo_view);
            new Parser_Async().execute(sortByValue);

             gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                    movie_json Movie= new movie_json(movieArr.get(position).movieName,movieArr.get(position).posterPath,
                            movieArr.get(position).movieOverview,movieArr.get(position).movieRelDate,
                            movieArr.get(position).movieVoteAvg,movieArr.get(position).movieID);
                    Log.i(LOG,"Fragment"+position);
                    intent.putExtra("M",Movie);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        cursorLoader = new CursorLoader(this,MovieProvider.CONTENT_URI.buildUpon().appendPath("movies").build(), null, null, null, null);
        return cursorLoader;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public class Parser_Async extends AsyncTask<String,Void,String> {

        private final String LOG_TAG = Parser_Async.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            movieArr.clear();

            // Will contain the raw JSON response as a string.
            String MovieJsonStr=null;

            try {

                //String Sort="sort_by";
                // Construct the URL for the movie query
                Uri uri=Uri.parse("https://api.themoviedb.org/3/discover/movie?").buildUpon()
                        .appendQueryParameter("sort_by",params[0])
                       // .appendQueryParameter("append_to_response","trailers,reviews")
                        .appendQueryParameter("api_key","INSERT_API_KEY_HERE")//Insert the API KEY here
                        .build();
                URL url =new URL(uri.toString());
                Log.d(LOG_TAG,"URI :"+url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.d(LOG_TAG, "SOrt param: " + params);
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    MovieJsonStr = null;
                }
                MovieJsonStr = buffer.toString();
                Log.v(LOG_TAG, MovieJsonStr);
                try {
                    getMovieDataFromJson(MovieJsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return MovieJsonStr;

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(),movieArr);
            gridView.setAdapter(imageAdapter);
        }

        private void getMovieDataFromJson(String s) throws JSONException {

            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String MOVIE_TITLE="original_title";
            final String MOVIE_OVERVIEW="overview";
            final String MOVIE_REL_DATE="release_date";
            final String MOVIE_VOTE_AVG="vote_average";

            final String MOVIE_ID="id";
            //final String MOVIE_BACKDROP="backdrop_path";

            JSONObject movieJson = new JSONObject(s);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            String movieName;
            String posterPath;
            String movieOverview;
            String movieRelDate;
            String movieVoteAvg;
            String movieID;


            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject movieData = movieArray.getJSONObject(i);

                movieName=movieData.getString(MOVIE_TITLE);
                movieOverview =movieData.getString(MOVIE_OVERVIEW);
                movieRelDate=movieData.getString(MOVIE_REL_DATE);
                movieVoteAvg=movieData.getString(MOVIE_VOTE_AVG);
                //movieTrailer=movieData.getString(MOVIE_TRAILER);
                movieID=movieData.getString(MOVIE_ID);

                posterPath= "http://image.tmdb.org/t/p/w185/" + movieData.getString(POSTER_PATH);
                //movieBackdrop="http://image.tmdb.org/t/p/w185/"+movieData.getString(MOVIE_BACKDROP);

                movie_json movie = new movie_json(movieName,posterPath,movieOverview,movieRelDate,movieVoteAvg,movieID);
                //movie.putTrailer(movieTrailer);

                movieArr.add(movie);

                Log.v(LOG_TAG, movieName);
                Log.v(LOG_TAG, "Poster Paths fetched: " + movie.posterPath);
                Log.v(LOG_TAG, "IDS: " + movie.movieID);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.popularity ) {
            String sortVal = getResources().getString(R.string.popularity_desc);
            if (sharedPref.getString("SORT_VALUE", "vote_count.desc").equals(sortVal)) {

            } else {
                poster_reload(sortVal);
            }
        }
        if(id == R.id.rating){
            String sortVal = getResources().getString(R.string.rating_desc);
            if(sharedPref.getString("SORT_VALUE", "popularity.desc").equals(sortVal)) {

            } else {
                poster_reload(sortVal);
            }
        }
        if(id== R.id.fav){
            String sortVal = getResources().getString(R.string.fav);
            SharedPreferences.Editor editor= sharedPref.edit();
            editor.putString("SORT_VALUE", sortVal);
            editor.commit();
            Favourites();

        }

        return super.onOptionsItemSelected(item);
    }

    private void poster_reload(String sortVal) {
        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putString("SORT_VALUE", sortVal);
        editor.commit();
        Parser_Async parseAsync = new Parser_Async();
        parseAsync.execute(sortVal);

        if(isTablet()||findViewById(R.id.container)!= null) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    movie_json Movie = new movie_json(movieArr.get(position).movieName, movieArr.get(position).posterPath,
                            movieArr.get(position).movieOverview, movieArr.get(position).movieRelDate,
                            movieArr.get(position).movieVoteAvg, movieArr.get(position).movieID);
                    bundle.putParcelable("M", Movie);
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, detailFragment).addToBackStack(null).commit();

                }
            });
        }
        else {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    movie_json Movie = new movie_json(movieArr.get(position).movieName, movieArr.get(position).posterPath,
                            movieArr.get(position).movieOverview, movieArr.get(position).movieRelDate,
                            movieArr.get(position).movieVoteAvg, movieArr.get(position).movieID);
                    Log.i(LOG, "Fragment" + position);
                    intent.putExtra("M", Movie);
                    startActivity(intent);
                }
            });
        }
        Log.d(LOG, "Sort value" + sortVal);
    }

    private void Favourites() {
        gridViewFav.setAdapter(cursorAdapter);

            if (isTablet() || findViewById(R.id.container) != null) {
                gridViewFav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Cursor cursor=null;

                        try {
                            cursor = (Cursor) gridViewFav.getItemAtPosition(position);
                            String MovieName = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_NAME));
                            String MovieDate = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_DATE));
                            String MoviePoster = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_POSTER));
                            String MovieVotes = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_VOTES));
                            String MovieOverview = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_OVERVIEW));
                            String MovieID = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_ID));

                            movie_json Movie = new movie_json(MovieName, MoviePoster, MovieOverview, MovieDate, MovieVotes, MovieID);

                            bundle.putParcelable("M", Movie);
                            DetailFragment detailFragment = new DetailFragment();
                            detailFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, detailFragment).addToBackStack(null).commit();
                        }
                        catch (Exception e) {
                        }
                    }
                });

            } else {
                gridViewFav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Cursor cursor=null;
                        try {
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                            cursor = (Cursor) gridViewFav.getItemAtPosition(position);
                            String MovieName = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_NAME));
                            String MovieDate = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_DATE));
                            String MoviePoster = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_POSTER));
                            String MovieVotes = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_VOTES));
                            String MovieOverview = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_OVERVIEW));
                            String MovieID = cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_ID));

                            movie_json Movie = new movie_json(MovieName, MoviePoster, MovieOverview, MovieDate, MovieVotes, MovieID);

                            intent.putExtra("M", Movie);
                            startActivity(intent);
                        }
                        catch (Exception e) {

                        }
                    }
                });
            }
    }
}
