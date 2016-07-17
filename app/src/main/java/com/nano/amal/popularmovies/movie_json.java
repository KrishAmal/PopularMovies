package com.nano.amal.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amal Krishnan on 19-02-2016.
 */
public class movie_json implements Parcelable {
    String movieName;
    String posterPath;
    String movieOverview;
    String movieRelDate;
    String movieVoteAvg;
    ArrayList<String> movieTrailer;
    ArrayList<String> reviewAuthor;
    String movieID;
    ArrayList<String> movieTrailerName;
    ArrayList<String> reviewContent;

    //String movieBackdrop;

    public movie_json(String movieName, String posterPath,String movieOverview,String movieRelDate,String movieVoteAvg
    ,String movieID) {
        this.posterPath = posterPath;
        this.movieName = movieName;
        this.movieOverview=movieOverview;
        this.movieRelDate=movieRelDate;
        this.movieVoteAvg=movieVoteAvg;
        this.movieID=movieID;
        //this.movieBackdrop=movieBackdrop;
    }

    public void putTrailer(ArrayList<String> movieTrailer,ArrayList<String> movieTrailerName)
    {
        this.movieTrailer=movieTrailer;
        this.movieTrailerName=movieTrailerName;
    }

    public void putReview(ArrayList<String> reviewAuthor,ArrayList<String> reviewContent){
        this.reviewAuthor=reviewAuthor;
        this.reviewContent=reviewContent;
    }

    protected movie_json(Parcel in) {
        movieName = in.readString();
        posterPath = in.readString();
        movieOverview = in.readString();
        movieRelDate=in.readString();
        movieVoteAvg=in.readString();
        movieID=in.readString();

    }

    public static final Creator<movie_json> CREATOR = new Creator<movie_json>() {
        @Override
        public movie_json createFromParcel(Parcel in) {
            return new movie_json(in);
        }

        @Override
        public movie_json[] newArray(int size) {
            return new movie_json[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieName);
        dest.writeString(posterPath);
        dest.writeString(movieOverview);
        dest.writeString(movieRelDate);
        dest.writeString(movieVoteAvg);
        dest.writeString(movieID);
        //dest.writeString(movieBackdrop);
    }
}
