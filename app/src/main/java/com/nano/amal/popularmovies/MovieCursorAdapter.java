package com.nano.amal.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.squareup.picasso.Picasso;

/**
 * Created by Amal Krishnan on 14-05-2016.
 */
public class MovieCursorAdapter extends CursorAdapter {

    private Context mContext;

    final String LOG = MovieCursorAdapter.class.getSimpleName();
    public static class ViewHolder {
        public final ImageView imageView;
        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.poster_image);
        }
    }

    public MovieCursorAdapter(Context context, Cursor c,int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String Poster=cursor.getString(cursor.getColumnIndex(MovieProvider.Movie.KEY_POSTER));
        Uri uri = Uri.parse(Poster);
        Log.d(LOG,"Uri :"+uri);
        Picasso.with(context).load(uri).into(viewHolder.imageView);

    }
}
