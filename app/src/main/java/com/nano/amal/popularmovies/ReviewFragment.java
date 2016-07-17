package com.nano.amal.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Amal Krishnan on 23-04-2016.
 */
public class ReviewFragment extends Fragment {
    movie_json movie;
    ArrayList<String> reviewAuthor,reviewContent = new ArrayList<String>();
    final String TAG = DetailActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle B=getArguments();

        Log.d(TAG, "mArgs: "+B);
        Log.d(TAG, "B.blah "+B.getStringArrayList("MO"));

        reviewAuthor=B.getStringArrayList("RA");
        reviewContent=B.getStringArrayList("RC");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Iterator<String> revA= reviewAuthor.iterator();
        Iterator<String> revC = reviewContent.iterator();


        while (revA.hasNext()&&revC.hasNext()) {

                View mMovieTrailerItem = LayoutInflater.from(getActivity()).inflate(R.layout.review, container,false);

                TextView reviewA = (TextView) mMovieTrailerItem.findViewById(R.id.reviewAuthor);
                TextView reviewC = (TextView) mMovieTrailerItem.findViewById(R.id.reviewContent);

                reviewA.setText(revA.next());
                reviewC.setText(revC.next());

                container.addView(mMovieTrailerItem);

        }
        return super.onCreateView(inflater, container, savedInstanceState);

    }

}
