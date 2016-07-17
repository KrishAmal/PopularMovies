package com.nano.amal.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

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

/**
 * Created by Amal Krishnan on 08-05-2016.
 */
public class DetailFragment extends Fragment  {

    movie_json movie;
    final String LOG_TAG2 = DetailActivity.class.getSimpleName();
    Bundle bundle = new Bundle();
    ArrayList<String> trailerName = new ArrayList<String>();
    ArrayList<String> trailerKey = new ArrayList<String>();
    ArrayList<String> reviewAuthor =new ArrayList<String>();
    ArrayList<String> reviewContent=new ArrayList<String>();
    CursorLoader cursorLoader;
    ToggleButton Fav =null;
    int isFavorite =0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG2, "In details frag");
        Bundle bun=getArguments();
        if(bun!=null){
            movie=bun.getParcelable("M");
            Log.d(LOG_TAG2, "Bundle is Bun ="+ bun);

        }
        if(movie==null) {
           bundle = getActivity().getIntent().getExtras();
           movie = bundle.getParcelable("M");
        }
        Log.d(LOG_TAG2, "Bundle "+bundle);

        new Extra_Async().execute(movie.movieID);
        //getLoaderManager().initLoader(0,null,this);

        Cursor cursor=getContext().getContentResolver().query(MovieProvider.CONTENT_URI.buildUpon().appendPath("movies").build(),null,MovieProvider.Movie.KEY_ID+"="+movie.movieID,null,null);
        if(cursor.getCount() > 0) {
            while(cursor.moveToNext())
            isFavorite = cursor.getInt(cursor.getColumnIndex(MovieProvider.Movie.KEY_FAV));
        }
        else
            isFavorite=0;

        cursor.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View detailView = LayoutInflater.from(getActivity()).inflate(R.layout.details, null);
        Log.d(LOG_TAG2, "ID :" + movie.movieID);
        Log.d(LOG_TAG2, "Trailer :" + movie.movieTrailer);

        TextView Name = (TextView) detailView.findViewById(R.id.Movie_name_detail);
        TextView Overview = (TextView) detailView.findViewById(R.id.movie_overview);
        TextView Rel_Date = (TextView) detailView.findViewById(R.id.movie_date);
        TextView Vote_avg = (TextView) detailView.findViewById(R.id.Votes_Avg);

        Fav =(ToggleButton) detailView.findViewById(R.id.FavButton);

        if(isFavorite==1)
            Fav.setChecked(true);
        else
            Fav.setChecked(false);

        Fav.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star));
        Fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Fav.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_on));

                    ContentValues contentValues = new ContentValues();


                    contentValues.put(MovieProvider.Movie.KEY_ID, movie.movieID);
                    contentValues.put(MovieProvider.Movie.KEY_NAME, movie.movieName);
                    contentValues.put(MovieProvider.Movie.KEY_DATE, movie.movieRelDate);
                    contentValues.put(MovieProvider.Movie.KEY_VOTES, movie.movieVoteAvg);
                    contentValues.put(MovieProvider.Movie.KEY_OVERVIEW, movie.movieOverview);
                    contentValues.put(MovieProvider.Movie.KEY_POSTER, movie.posterPath);
                    contentValues.put(MovieProvider.Movie.KEY_FAV,1);
                    try {
                        getContext().getContentResolver().insert(MovieProvider.CONTENT_URI.buildUpon().appendPath("movies").build(), contentValues);

                        Log.d(LOG_TAG2,"Insert Successful");
                        Cursor cursor=getContext().getContentResolver().query(MovieProvider.CONTENT_URI.buildUpon().appendPath("movies").build(),null,MovieProvider.Movie.KEY_ID+"="+movie.movieID,null,null);
                        if(cursor!=null){
                            int temp=cursor.getInt(cursor.getColumnIndex(MovieProvider.Movie.KEY_FAV));
                            Log.d(LOG_TAG2,"the value is :"+temp);
                        }
                        cursor.close();
                    }
                    catch (Exception e){

                    }
                }
                else {
                    Fav.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star));

                    getContext().getContentResolver().delete(MovieProvider.CONTENT_URI.buildUpon().appendPath("movies").build(),MovieProvider.Movie.KEY_ID+"="+movie.movieID,null);

                    Log.d(LOG_TAG2,"Delete Successful");
                }
            }
        });


        Rel_Date.setText(movie.movieRelDate);
        Overview.setText(movie.movieOverview);
        Name.setText(movie.movieName);
        String votes = getString(R.string.vote_avg);
        Vote_avg.setText(movie.movieVoteAvg + votes);


        ImageView poster = (ImageView) detailView.findViewById(R.id.poster_detail);
        Uri uri = Uri.parse(movie.posterPath);
        Picasso.with(getContext()).load(uri).into(poster);

        container.addView(detailView);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    class Extra_Async extends AsyncTask<String,Void,String> {

        final String LOG_TAG3 = Extra_Async.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String MovieTrailerStr = null;
            try {
                int result;
                Uri uri = Uri.parse("https://api.themoviedb.org/3/movie/"+params[0]+"?").buildUpon()
                        //.path(params[0]+"/videos")
                        .appendQueryParameter("append_to_response","trailers,reviews")
                        .appendQueryParameter("api_key","INSERT_API_KEY_HERE")   //Insert the API KEY here
                        .build();

                URL url = new URL(uri.toString());

                Log.d(LOG_TAG3, "URI :" + url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

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
                    MovieTrailerStr = null;
                }
                MovieTrailerStr = buffer.toString();
                Log.v(LOG_TAG3, MovieTrailerStr);
                try {
                    getExtraDataFromJson(MovieTrailerStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return MovieTrailerStr;

            } catch (IOException e) {
                Log.e(LOG_TAG3, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG3, "Error closing stream", e);
                    }
                }
            }
        }

        public void getExtraDataFromJson(String s) throws JSONException {

            final String TRAILERS = "trailers";
            final String TRAILER_MOVIE = "youtube";
            final String TRAILER_KEY="source";
            final String TRAILER_NAME="name";

            final String REVIEWS ="reviews";
            final String REVIEW_RESULTS="results";
            final String REVIEW_AUTHOR="author";
            final String REVIEW_CONTENT="content";

            JSONObject movieJson = new JSONObject(s);
            JSONObject trailerJson= movieJson.getJSONObject(TRAILERS);
            JSONArray movieArray = trailerJson.getJSONArray(TRAILER_MOVIE);

            JSONObject reviewJson= movieJson.getJSONObject(REVIEWS);
            JSONArray reviewArray = reviewJson.getJSONArray(REVIEW_RESULTS);

            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject trailerData = movieArray.getJSONObject(i);

                trailerKey.add(trailerData.getString(TRAILER_KEY));
                trailerName.add(trailerData.getString(TRAILER_NAME));

                movie.putTrailer(trailerKey,trailerName);

                Log.v(LOG_TAG3, "Trailers : " + movie.movieTrailer);
                Log.v(LOG_TAG3, "Trailers_NAME : " + movie.movieTrailerName);
            }

            for(int j = 0; j < reviewArray.length(); j++){

                JSONObject reviewData =reviewArray.getJSONObject(j);

                reviewAuthor.add(reviewData.getString(REVIEW_AUTHOR));
                reviewContent.add(reviewData.getString(REVIEW_CONTENT));

                movie.putReview(reviewAuthor,reviewContent);

                Log.v(LOG_TAG3, "ReviewsAuthor : " + reviewAuthor);
                Log.v(LOG_TAG3, "ReviewContent : " + reviewContent);

            }

        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //Bundle for passing to fragment

            bundle.putStringArrayList("MO", movie.movieTrailerName);
            bundle.putStringArrayList("TK", movie.movieTrailer);
            bundle.putStringArrayList("RA",movie.reviewAuthor);
            bundle.putStringArrayList("RC",movie.reviewContent);

            // set TrailerFrag Arguments
            TrailerFragment fragObj = new TrailerFragment();
            ReviewFragment fragObj2=new ReviewFragment();
            fragObj.setArguments(bundle);
            fragObj2.setArguments(bundle);

            FragmentManager FM=getFragmentManager();
            FragmentTransaction ft = FM.beginTransaction();

            // Replace the contents of the container with the new fragment
            ft.add(R.id.Fragment_placeholder, fragObj);
            if (movie.reviewAuthor != null) ft.add(R.id.Fragment2_placeholder, fragObj2);
            // or ft.add(R.id.your_placeholder, new FooFragment());
            // Complete the changes added above
            ft.commit();

        }
    }
}
