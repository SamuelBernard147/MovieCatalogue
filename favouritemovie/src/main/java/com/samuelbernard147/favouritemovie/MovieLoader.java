package com.samuelbernard147.favouritemovie;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.samuelbernard147.favouritemovie.Model.Movie;

import java.util.ArrayList;

import static com.samuelbernard147.favouritemovie.Model.Movie.TYPE_MOVIE;
import static com.samuelbernard147.favouritemovie.Model.Movie.TYPE_TV;
import static com.samuelbernard147.favouritemovie.db.DatabaseContract.FavColumns.CONTENT_URI;
import static com.samuelbernard147.favouritemovie.db.DatabaseContract.FavColumns.FAVID;
import static com.samuelbernard147.favouritemovie.db.DatabaseContract.FavColumns.OVERVIEW;
import static com.samuelbernard147.favouritemovie.db.DatabaseContract.FavColumns.POSTER;
import static com.samuelbernard147.favouritemovie.db.DatabaseContract.FavColumns.TITLE;
import static com.samuelbernard147.favouritemovie.db.DatabaseContract.FavColumns.TYPE;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {
    private ArrayList<Movie> mListMovie;
    private boolean mHasResult = false;

    private Context context;
    private String type;

    public MovieLoader(final Context context, String type) {
        super(context);
        this.context = context;
        this.type = type;
        onContentChanged();
    }

    //Ketika data loading
    @Override
    protected void onStartLoading() {
        if (takeContentChanged())
            forceLoad();
        else if (mHasResult)
            deliverResult(mListMovie);
    }

    @Override
    public void deliverResult(final ArrayList<Movie> data) {
        mListMovie = data;
        mHasResult = true;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (mHasResult) {
            mListMovie = null;
            mHasResult = false;
        }
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        ArrayList<Movie> listMovie = new ArrayList<>();
        Cursor cursor = null;
        Uri uri;
        if (type.equals(TYPE_MOVIE)) {
            uri = Uri.parse(CONTENT_URI + "/" + TYPE_MOVIE);
            cursor = context.getContentResolver().query(uri, null, null, null, null);
        } else if (type.equals(TYPE_TV)) {
            uri = Uri.parse(CONTENT_URI + "/" + TYPE_TV);
            cursor = context.getContentResolver().query(uri, null, null, null, null);
        }

        if (cursor != null) {
            listMovie = mapCursorToArrayList(cursor);
        }
        return listMovie;
    }

    private static ArrayList<Movie> mapCursorToArrayList(Cursor movieCursor) {
        ArrayList<Movie> notesList = new ArrayList<>();

        while (movieCursor.moveToNext()) {
            int id = movieCursor.getInt(movieCursor.getColumnIndexOrThrow(FAVID));
            String title = movieCursor.getString(movieCursor.getColumnIndexOrThrow(TITLE));
            String poster = movieCursor.getString(movieCursor.getColumnIndexOrThrow(POSTER));
            String overview = movieCursor.getString(movieCursor.getColumnIndexOrThrow(OVERVIEW));
            String type = movieCursor.getString(movieCursor.getColumnIndexOrThrow(TYPE));
            notesList.add(new Movie(id, title, poster, overview, type));
        }

        movieCursor.close();
        return notesList;
    }
}