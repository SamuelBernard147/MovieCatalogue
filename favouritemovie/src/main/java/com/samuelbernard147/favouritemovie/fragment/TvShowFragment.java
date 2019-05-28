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

import static com.samuelbernard147.favouritemovie.Model.Movie.TYPE_TV;
import static com.samuelbernard147.favouritemovie.db.DatabaseContract.FavColumns.CONTENT_URI;

public class TvShowFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    RecyclerView recyclerViewTv;
    MovieAdapter tvAdapter;
    ArrayList<Movie> listTv;
    ProgressBar progressBarTv;
    SwipeRefreshLayout refresh;
    TextView tvLoading, tvNone;
    Bundle bundle;

    private DataObserver myObserver;

    //    Key untuk SaveInstanceState
    static final String STATE_TV = "state_tv";

    public TvShowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tv_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //        Recyclerview
        recyclerViewTv = view.findViewById(R.id.rv_tv);
        showRecycler();

        //        Progress bar muncul
        progressBarTv = view.findViewById(R.id.pb_tvShow);
        progressBarTv.setVisibility(View.VISIBLE);
        tvLoading = view.findViewById(R.id.tv_loading_tv);
        tvLoading.setVisibility(View.VISIBLE);
        tvNone = view.findViewById(R.id.tv_fav_tv_none);

        HandlerThread handlerThread = new HandlerThread("TvObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        myObserver = new TvShowFragment.DataObserver(handler, getActivity());

        if (getActivity() != null) {
            getActivity().getContentResolver().registerContentObserver(CONTENT_URI, true, myObserver);
        }

        //      Inisiasi dari Loader
        getLoaderManager().initLoader(1, bundle, TvShowFragment.this);

        refresh = view.findViewById(R.id.refresh_tv);
        refresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getLoaderManager().destroyLoader(1);
                        getLoaderManager().initLoader(1, bundle, TvShowFragment.this);
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save Data Fragment
        outState.putParcelableArrayList(STATE_TV, this.listTv);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
//            Restore Data Fragment
            ArrayList<Movie> stateData = savedInstanceState.getParcelableArrayList(STATE_TV);
            this.listTv = stateData;

//            Setdata ke adapter
            tvAdapter.setListMovie(stateData);

//            Menghilangkan progress bar
            progressBarTv.setVisibility(View.INVISIBLE);
            tvLoading.setVisibility(View.INVISIBLE);

//            Menghentikan loader
            getLoaderManager().destroyLoader(1);
        }
    }

    void showRecycler() {
        tvAdapter = new MovieAdapter();
        tvAdapter.notifyDataSetChanged();
        recyclerViewTv.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTv.setAdapter(tvAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        tvAdapter.clearListMovie();
        tvAdapter.notifyDataSetChanged();
        refreshLoader(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        tvAdapter.clearListMovie();
        tvAdapter.notifyDataSetChanged();
        refreshLoader(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tvAdapter.clearListMovie();
        stopLoader(1);
    }

    @NonNull
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, @Nullable Bundle args) {
        return new MovieLoader(getActivity(), TYPE_TV);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Movie>> loader, ArrayList<Movie> tvshows) {
        tvAdapter.setListMovie(tvshows);
        this.listTv = tvshows;
        stopLoader(1);
//        Progress bar menghilang
        progressBarTv.setVisibility(View.INVISIBLE);
        tvLoading.setVisibility(View.INVISIBLE);

        if (refresh != null) {
            refresh.setRefreshing(false);
        }

        if (tvshows.size() <= 0) {
            tvNone.setVisibility(View.VISIBLE);
        } else {
            tvNone.setVisibility(View.INVISIBLE);
        }
    }

    void stopLoader(int id) {
        getLoaderManager().destroyLoader(id);
    }

    void refreshLoader(int id) {
        getLoaderManager().restartLoader(id, bundle, TvShowFragment.this);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Movie>> loader) {
        tvAdapter.setListMovie(null);
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