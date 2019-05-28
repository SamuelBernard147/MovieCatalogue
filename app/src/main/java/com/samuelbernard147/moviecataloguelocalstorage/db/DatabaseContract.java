package com.samuelbernard147.moviecataloguelocalstorage.db;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    // Authority yang digunakan
    public static final String AUTHORITY = "com.samuelbernard147.moviecataloguelocalstorage";
    private static final String SCHEME = "content";

    public static final class FavColumns implements BaseColumns {
        public static final String TABLE_NAME = "favourite";

        //Favourite Id
        public static final String FAVID = "favid";
        //Favourite title
        public static final String TITLE = "title";
        //Favourite poster url
        public static final String POSTER = "poster";
        //Favourite overview
        public static final String OVERVIEW = "overview";
        //Favourite type
        public static final String TYPE = "type";

        // Base content yang digunakan untuk akses content provider
        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();
    }

    /*
    Digunakan untuk mempermudah akses data di dalam cursor dengan parameter nama column
    */
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }
}