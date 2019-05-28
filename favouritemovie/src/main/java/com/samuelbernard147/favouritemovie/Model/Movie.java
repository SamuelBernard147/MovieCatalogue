package com.samuelbernard147.favouritemovie.Model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.samuelbernard147.favouritemovie.db.DatabaseContract;

import static android.provider.BaseColumns._ID;
import static com.samuelbernard147.favouritemovie.db.DatabaseContract.getColumnInt;
import static com.samuelbernard147.favouritemovie.db.DatabaseContract.getColumnString;

public class Movie implements Parcelable {
    private int id;
    private String title;
    private String poster;
    private String overview;
    private String type;

    public static final String TYPE_MOVIE = "movie";
    public static final String TYPE_TV = "tv";

    public Movie() {
    }

    public Movie(Cursor cursor) {
        this.id = getColumnInt(cursor, _ID);
        this.title = getColumnString(cursor, DatabaseContract.FavColumns.TITLE);
        this.poster = getColumnString(cursor, DatabaseContract.FavColumns.POSTER);
        this.overview = getColumnString(cursor, DatabaseContract.FavColumns.OVERVIEW);
        this.type = getColumnString(cursor, DatabaseContract.FavColumns.TYPE);
    }

    public Movie(int id, String title, String poster, String overview, String type) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.type = type;
    }

    //    Getter Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //  Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.poster);
        dest.writeString(this.overview);
        dest.writeString(this.type);
    }

    private Movie(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.poster = in.readString();
        this.overview = in.readString();
        this.type = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}