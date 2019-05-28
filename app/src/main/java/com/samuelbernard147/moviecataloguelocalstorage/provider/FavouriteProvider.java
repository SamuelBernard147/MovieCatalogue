package com.samuelbernard147.moviecataloguelocalstorage.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.samuelbernard147.moviecataloguelocalstorage.db.FavHelper;

import static com.samuelbernard147.moviecataloguelocalstorage.Model.Movie.TYPE_MOVIE;
import static com.samuelbernard147.moviecataloguelocalstorage.Model.Movie.TYPE_TV;
import static com.samuelbernard147.moviecataloguelocalstorage.db.DatabaseContract.AUTHORITY;
import static com.samuelbernard147.moviecataloguelocalstorage.db.DatabaseContract.FavColumns.TABLE_NAME;

public class FavouriteProvider extends ContentProvider {
    private static final int MOVIE_ID = 1;
    private static final int TV_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + TYPE_MOVIE, MOVIE_ID);
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME + "/" + TYPE_TV, TV_ID);
    }

    private FavHelper favHelper;

    @Override
    public boolean onCreate() {
        favHelper = favHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        favHelper.open();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIE_ID:
                cursor = favHelper.queryProvider("movie");
                break;
            case TV_ID:
                cursor = favHelper.queryProvider("tv");
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}