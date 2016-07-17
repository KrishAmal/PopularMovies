package com.nano.amal.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Amal Krishnan on 01-04-2016.
 */
public class TrailerFragment extends Fragment {

    ArrayList<String> trailers,trailerKey = new ArrayList<String>();

    final String TAG = DetailActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        Bundle B=getArguments();

        Log.d(TAG, "mArgs: "+B);
        Log.d(TAG, "B.blah "+B.getStringArrayList("MO"));

        trailers=B.getStringArrayList("MO");
        trailerKey=B.getStringArrayList("TK");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.d(TAG, "size"+trailers.size());

         Iterator<String> trailerK = trailerKey.iterator();
         Iterator<String> trailerN = trailers.iterator();

        while(trailerK.hasNext()&&trailerN.hasNext()) {

            final String T=trailerK.next();
            View mMovieTrailerItem = LayoutInflater.from(getActivity()).inflate(R.layout.trailer,container,false);

            Uri uri=Uri.parse("http://www.youtube.com/watch?v="+T);
            Log.d("Uri :",uri.toString());

            mMovieTrailerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playYoutubeTrailerIntent(T);
                }
            });

            ImageView trailerBut = (ImageView) mMovieTrailerItem.findViewById(R.id.trailer_button);
            trailerBut.setImageResource(R.drawable.ic_play_circle_filled_black_48dp);
            TextView trailerText = (TextView) mMovieTrailerItem.findViewById(R.id.trailer_text);

            trailerText.setText( trailerN.next());
            container.addView(mMovieTrailerItem);

        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void playYoutubeTrailerIntent(String id) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" +id));
            startActivity(intent);
        }
    }
}
