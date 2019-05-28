package com.samuelbernard147.favouritemovie.fragment;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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

import com.samuelbernard147.favouritemovie.Adapter.MovieAdapter;
import com.samuelbernard147.favouritemovie.Model.Movie;
import com.samuelbernard147.favouritemovie.MovieLoader;
import com.samuelbernard147.favouritemovie.R;

import java.util.ArrayList;

import static com.samuelbernard147.favouritemovie.Model.Movie.TYPE_MOVIE;
import static com.samuelbernard147.favouritemovie.db.DatabaseContract.FavColumns.CONTENT_URI;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    RecyclerView recyclerViewMovie;
    MovieAdapter movieAdapter;
    ArrayList<Movie> listMovie;
    ProgressBar progressBarMovie;
    SwipeRefreshLayout refresh;
    TextView tvLoading,  tvNone;
    Bundle bundle;

    private DataObserver myObserver;

    //    Key untuk SaveInstanceState
    static final String STATE_MOVIE = "state_movie";


    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //        Recyclerview
        recyclerViewMovie = view.findViewById(R.id.rv_movie);
        showRecycler();

        //        Progress bar muncul
        progressBarMovie = view.findViewById(R.id.pb_movie);
        progressBarMovie.setVisibility(View.VISIBLE);
        tvLoading = view.findViewById(R.id.tv_loading_movie);
        tvLoading.setVisibility(View.VISIBLE);
        tvNone= view.findViewById(R.id.tv_fav_movie_none);

        HandlerThread handlerThread = new HandlerThread("MovieObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        myObserver = new DataObserver(handler, getActivity());

        if (getActivity() != null) {
            getActivity().getContentResolver().registerContentObserver(CONTENT_URI, true, myObserver);
        }

        //      Inisiasi dari Loader
        getLoaderManager().initLoader(0, bundle, MovieFragment.this);

        refresh = view.findViewById(R.id.refresh_movie);
        refresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getLoaderManager().destroyLoader(0);
                        getLoaderManager().initLoader(0, bundle, MovieFragment.this);
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save Data Fragment
        outState.putParcelableArrayList(STATE_MOVIE, this.listMovie);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
//            Restore Data Fragment
            ArrayList<Movie> stateData = savedInstanceState.getParcelableArrayList(STATE_MOVIE);
            this.listMovie = stateData;

//            Setdata ke adapter
            movieAdapter.setListMovie(stateData);

//            Menghilangkan progress bar
            progressBarMovie.setVisibility(View.INVISIBLE);
            tvLoading.setVisibility(View.INVISIBLE);

//            Menghentikan loader
            getLoaderManager().destroyLoader(0);
        }
    }



    void showRecycler() {
        movieAdapter = new MovieAdapter();
        movieAdapter.notifyDataSetChanged();
        recyclerViewMovie.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewMovie.setAdapter(movieAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        movieAdapter.clearListMovie();
        movieAdapter.notifyDataSetChanged();
        refreshLoader(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        movieAdapter.clearListMovie();
        movieAdapter.notifyDataSetChanged();
        refreshLoader(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        movieAdapter.clearListMovie();
        stopLoader(0);
    }

    @NonNull
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, @Nullable Bundle args) {
        return new MovieLoader(getActivity(), TYPE_MOVIE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        movieAdapter.setListMovie(movies);
        this.listMovie = movies;
        stopLoader(0);
//        Progress bar menghilang
        progressBarMovie.setVisibility(View.INVISIBLE);
        tvLoading.setVisibility(View.INVISIBLE);

        if (refresh != null) {
            refresh.setRefreshing(false);
        }

        if (movies.size() <= 0) {
            tvNone.setVisibility(View.VISIBLE);
        } else {
            tvNone.setVisibility(View.INVISIBLE);
        }
    }

    void stopLoader(int id) {
        getLoaderManager().destroyLoader(id);
    }

    void refreshLoader(int id) {
        getLoaderManager().restartLoader(id, bundle, MovieFragment.this);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Movie>> loader) {
        movieAdapter.setListMovie(null);
    }

    static class DataObserver extends ContentObserver {
        final Context context;

        DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }
    }
}