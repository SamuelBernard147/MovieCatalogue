package com.samuelbernard147.moviecataloguelocalstorage.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samuelbernard147.moviecataloguelocalstorage.Adapter.MovieAdapter;
import com.samuelbernard147.moviecataloguelocalstorage.DetailActivity;
import com.samuelbernard147.moviecataloguelocalstorage.ItemClickSupport;
import com.samuelbernard147.moviecataloguelocalstorage.Model.Movie;
import com.samuelbernard147.moviecataloguelocalstorage.MovieLoader;
import com.samuelbernard147.moviecataloguelocalstorage.R;
import com.samuelbernard147.moviecataloguelocalstorage.SettingsPreference;
import com.samuelbernard147.moviecataloguelocalstorage.db.FavHelper;

import java.util.ArrayList;
import java.util.Locale;

import static com.samuelbernard147.moviecataloguelocalstorage.Model.Movie.TYPE_MOVIE;
import static com.samuelbernard147.moviecataloguelocalstorage.MovieLoader.TYPE_LOCAL;

public class FavMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    RecyclerView recyclerViewFavMovie;
    MovieAdapter favMovieAdapter;
    ArrayList<Movie> listFavMovie;
    ProgressBar progressBarFavMovie;
    SwipeRefreshLayout refresh;
    TextView tvLoading, tvNone;
    String language;
    Bundle bundle;

    FavHelper favHelper;

    //    Query search
    public static String queryMovieFav;

    private SettingsPreference mSettingPreference;

    //    Key untuk SaveInstanceState
    static final String STATE_FAV_MOVIE = "state_fav_movie";
    static final String STATE_LANG = "state_language";

    public FavMovieFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            queryMovieFav = getArguments().getString("query");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_fav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            mSettingPreference = new SettingsPreference(getActivity());
        }

//        Recyclerview
        recyclerViewFavMovie = view.findViewById(R.id.rv_fav_movie);
        showRecycler();

//        Progress bar muncul
        progressBarFavMovie = view.findViewById(R.id.pb_fav_movie);
        progressBarFavMovie.setVisibility(View.VISIBLE);
        tvLoading = view.findViewById(R.id.tv_loading_fav_movie);
        tvLoading.setVisibility(View.VISIBLE);
        tvNone = view.findViewById(R.id.tv_fav_movie_none);

//      Inisiasi dari Loader
        getLoaderManager().initLoader(2, bundle, FavMovieFragment.this);//

        favHelper = FavHelper.getInstance(getActivity());
        favHelper.open();

        refresh = view.findViewById(R.id.refresh_fav_movie);
        refresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        queryMovieFav = null;
                        refresh.setRefreshing(false);
                        getLoaderManager().destroyLoader(2);
                        getLoaderManager().initLoader(2, bundle, FavMovieFragment.this);
                    }
                },2000);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save Data Fragment
        outState.putParcelableArrayList(STATE_FAV_MOVIE, this.listFavMovie);
        outState.putString(STATE_LANG, this.language);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
//            Restore Data Fragment
            ArrayList<Movie> stateData = savedInstanceState.getParcelableArrayList(STATE_FAV_MOVIE);
            this.listFavMovie = stateData;
            this.language = savedInstanceState.getString(STATE_LANG);

//            Setdata ke adapter
            favMovieAdapter.setListMovie(stateData);

//            Menghilangkan progress bar
            progressBarFavMovie.setVisibility(View.INVISIBLE);
            tvLoading.setVisibility(View.INVISIBLE);

//            Menghentikan loader
            getLoaderManager().destroyLoader(2);
        }
    }

    void showRecycler() {
        favMovieAdapter = new MovieAdapter();
        favMovieAdapter.notifyDataSetChanged();
        recyclerViewFavMovie.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewFavMovie.setAdapter(favMovieAdapter);

//        Item Click support
        ItemClickSupport.addTo(recyclerViewFavMovie).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Movie selectedMovie = listFavMovie.get(position);
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra(DetailActivity.EXTRA_ID, selectedMovie.getId());
                i.putExtra(DetailActivity.EXTRA_LANG, language);
                i.putExtra(DetailActivity.EXTRA_TYPE, TYPE_MOVIE);
                startActivity(i);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        favMovieAdapter.clearListMovie();
        favMovieAdapter.notifyDataSetChanged();
        getLoaderManager().restartLoader(2, bundle, FavMovieFragment.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        favMovieAdapter.clearListMovie();
        favMovieAdapter.notifyDataSetChanged();
        getLoaderManager().restartLoader(2, bundle, FavMovieFragment.this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        favMovieAdapter.clearListMovie();
        getLoaderManager().destroyLoader(2);
        favHelper.close();
    }

    @NonNull
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, @Nullable Bundle args) {
//        Menentukan bahasa
        String lang = mSettingPreference.getLang();
        if (lang != null) {
            this.language = lang;
        } else {
            this.language = Locale.getDefault().getLanguage();
        }
        return new MovieLoader(getActivity(), TYPE_MOVIE, language, TYPE_LOCAL, queryMovieFav);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        favMovieAdapter.setListMovie(movies);
        this.listFavMovie = movies;
        stopLoader(2);
//        Progress bar menghilang
        progressBarFavMovie.setVisibility(View.INVISIBLE);
        tvLoading.setVisibility(View.INVISIBLE);

        if (queryMovieFav != null && movies.size() <= 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.not_found), Toast.LENGTH_SHORT).show();
            tvNone.setVisibility(View.INVISIBLE);
        } else if (queryMovieFav == null && movies.size() <= 0) {
            tvNone.setVisibility(View.VISIBLE);
        } else {
            tvNone.setVisibility(View.INVISIBLE);
        }
    }

    void stopLoader(int id) {
        getLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Movie>> loader) {
        favMovieAdapter.setListMovie(null);
    }
}