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
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Locale;

import static com.samuelbernard147.moviecataloguelocalstorage.Model.Movie.TYPE_TV;
import static com.samuelbernard147.moviecataloguelocalstorage.MovieLoader.TYPE_API;

public class MainTvShowFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    public static final String TAG = MainTvShowFragment.class.getSimpleName();
    RecyclerView recyclerViewTv;
    MovieAdapter tvAdapter;
    ArrayList<Movie> listTv;
    ProgressBar progressBarTv;
    SwipeRefreshLayout refresh;
    TextView tvLoading;
    String language;
    Bundle bundle;

    //    Query search
    String querySearch;

    private SettingsPreference mSettingPreference;

    //    Key untuk SaveInstanceState
    static final String STATE_TV = "state_TV";
    static final String STATE_LANG = "state_language";

    public MainTvShowFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            querySearch = getArguments().getString("query");
        }
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
        if (getActivity() != null) {
            mSettingPreference = new SettingsPreference(getActivity());
        }

//        Recyclerview
        recyclerViewTv = view.findViewById(R.id.rv_tv);
        showRecyler();

//        Progress bar muncul
        progressBarTv = view.findViewById(R.id.pb_tvShow);
        progressBarTv.setVisibility(View.VISIBLE);
        tvLoading = view.findViewById(R.id.tv_loading_tv);
        tvLoading.setVisibility(View.VISIBLE);

//      Inisiasi dari Loader, dimasukkan ke dalam onCreate
        getLoaderManager().initLoader(1, bundle, MainTvShowFragment.this);
        Log.d(TAG, "Query search :" + querySearch);

        refresh = view.findViewById(R.id.refresh_tv);
        refresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        querySearch = null;
                        refresh.setRefreshing(false);
                        getLoaderManager().destroyLoader(1);
                        getLoaderManager().initLoader(1, bundle, MainTvShowFragment.this);
                    }
                },2000);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//       Save Data Fragment
        outState.putParcelableArrayList(STATE_TV, this.listTv);
        outState.putString(STATE_LANG, this.language);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
//            Restore Data Fragment
            ArrayList<Movie> stateData = savedInstanceState.getParcelableArrayList(STATE_TV);
            this.listTv = stateData;
            this.language = savedInstanceState.getString(STATE_LANG);

//            Setdata ke adapter
            tvAdapter.setListMovie(stateData);

//            Menghilangkan progress bar
            progressBarTv.setVisibility(View.INVISIBLE);
            tvLoading.setVisibility(View.INVISIBLE);

//            Menghentikan loader
            getLoaderManager().destroyLoader(1);
        }
    }

    void showRecyler() {
        tvAdapter = new MovieAdapter();
        tvAdapter.notifyDataSetChanged();
        recyclerViewTv.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTv.setAdapter(tvAdapter);

//        Item Click support
        ItemClickSupport.addTo(recyclerViewTv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Movie selectedTv = listTv.get(position);
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra(DetailActivity.EXTRA_ID, selectedTv.getId());
                i.putExtra(DetailActivity.EXTRA_LANG, language);
                i.putExtra(DetailActivity.EXTRA_TYPE, selectedTv.TYPE_TV);
                startActivity(i);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tvAdapter.clearListMovie();
        getLoaderManager().destroyLoader(1);
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
        return new MovieLoader(getActivity(), TYPE_TV, language, TYPE_API, querySearch);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Movie>> loader, ArrayList<Movie> tv) {
        this.listTv = tv;
        tvAdapter.setListMovie(tv);
        stopLoader(1);
//        Progress bar menghilang
        progressBarTv.setVisibility(View.INVISIBLE);
        tvLoading.setVisibility(View.INVISIBLE);

        if (querySearch != null && tv.size() <= 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.not_found), Toast.LENGTH_SHORT).show();
        } else if (querySearch == null && tv.size() <= 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
        }
    }

    void stopLoader(int id) {
        getLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Movie>> loader) {
        tvAdapter.setListMovie(null);
    }
}