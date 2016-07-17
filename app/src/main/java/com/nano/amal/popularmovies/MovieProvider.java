package com.nano.amal.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.net.URI;
import java.util.List;

import de.triplet.simpleprovider.AbstractProvider;
import de.triplet.simpleprovider.Column;
import de.triplet.simpleprovider.Table;

/**
 * Created by Amal Krishnan on 10-05-2016.
 */
public class MovieProvider extends AbstractProvider{

    static final String AUTHORITY="com.nano.amal.popularmovies";
    static final String URL="content://"+AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    @Override
    protected String getAuthority() {
        return AUTHORITY;
    }

    @Table
    public class Movie {

        @Column(value = Column.FieldType.INTEGER,primaryKey = true)
        public static final String KEY_ID="_id";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_NAME="movieName";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_OVERVIEW="movieOverview";

        @Column(Column.FieldType.REAL)
        public static final String KEY_VOTES="movieVotes";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_DATE="movieDate";

        @Column(Column.FieldType.TEXT)
        public static final String KEY_POSTER="moviePoster";

        @Column(Column.FieldType.INTEGER)
        public static final String KEY_FAV="movieFav";

    }
}
