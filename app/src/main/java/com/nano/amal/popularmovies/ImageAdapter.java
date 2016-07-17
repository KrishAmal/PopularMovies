package com.nano.amal.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Amal Krishnan on 06-02-2016.
 */

public class ImageAdapter extends ArrayAdapter<movie_json>{

    public ImageAdapter(Context context, List<movie_json> objects) {
        super(context, 0, objects);
    }
    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        movie_json movie=getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);

        }
        Uri uri = Uri.parse(movie.posterPath);

        ImageView poster = (ImageView) convertView.findViewById(R.id.poster_image);
        Picasso.with(getContext()).load(uri).into(poster);
        return convertView;
    }
}



