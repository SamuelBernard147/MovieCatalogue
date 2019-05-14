package com.samuelbernard147.moviecataloguelocalstorage;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.samuelbernard147.moviecataloguelocalstorage.Model.Movie;
import com.samuelbernard147.moviecataloguelocalstorage.db.FavHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movie>> {
    private ArrayList<Movie> mListMovie;
    private boolean mHasResult = false;

    private Context context;
    private String type;
    private String url;
    private String source;
    private String query;

    public static String BHS_INDO = "in";
    public static String BHS_INGGRIS = "en";

    public final static String TYPE_API = "API";
    public final static String TYPE_LOCAL = "LOCAL";

    //    API KEY
    public static final String API_KEY = BuildConfig.TMDB_API_KEY;

    //    URL
    private final String URL_POPULAR = BuildConfig.TMDB_URL_POPULAR;
    private final String URL_SEARCH_MOVIE = BuildConfig.TMDB_URL_SEARCH;

    public MovieLoader(final Context context, String type, String language, String source, String query) {
        super(context);
        this.context = context;
        this.type = type;
        this.url = getUrl(type, language, query);
        this.source = source;
        this.query = query;
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

    /*
     * Fungsi untuk menentukan url
     * @param type(movie/tv), bahasa,
     * dan query pencarian
     */
    private String getUrl(String type, String language, String query) {
        String url;

//        Penentuan bahasa
        String lang = "&language=en-US";
        if (language.equals(BHS_INGGRIS)) {
            lang = "&language=en-US";
        } else if (language.equals(BHS_INDO)) {
            lang = "&language=id-ID";
        }

//        Bila query pencarian kosong maka url yang diambil adalah url popular movie
        if (query == null || query.equals("")) {
            url = URL_POPULAR + type + "?api_key=" + API_KEY + lang;
        } else {
            url = URL_SEARCH_MOVIE + type + "?api_key=" + API_KEY + lang + "&query=" + query;
        }
        return url;
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        ArrayList<Movie> listMovie = null;
        if (source.equals(TYPE_API)) {
            listMovie = loadFromAPI();
        } else if (source.equals(TYPE_LOCAL)) {
            listMovie = loadFromLocal();
        }
        return listMovie;
    }

    /*
     * Load data favorite(local)
     */
    private ArrayList<Movie> loadFromLocal() {
        ArrayList<Movie> listFavorite;
        FavHelper favHelper = FavHelper.getInstance(context);

        if (query != null) {
            listFavorite = favHelper.getMovieByTitle(query, type);
        } else {
            listFavorite = favHelper.getAllFav(type);
        }

        return listFavorite;
    }

    /*
     * Load data movie dari API
     */
    private ArrayList<Movie> loadFromAPI() {
        SyncHttpClient client = new SyncHttpClient();
        final ArrayList<Movie> listMovie = new ArrayList<>();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                setUseSynchronousMode(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray responseArray = responseObject.getJSONArray("results");

                    for (int i = 0; i < responseArray.length(); i++) {
                        Movie movieItems = new Movie();
                        movieItems.setMovie(responseArray, i, type);
                        listMovie.add(movieItems);
                    }
                } catch (Exception e) {
                    //Respon bila eror
                    Log.e("Exception", "Data gagal diparsing");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Respon bila eror
                Log.e("OnFailure", "Data gagal diload");
            }
        });
        return listMovie;
    }
}