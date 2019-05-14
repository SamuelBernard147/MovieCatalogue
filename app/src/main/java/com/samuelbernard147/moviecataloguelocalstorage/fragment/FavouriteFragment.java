package com.samuelbernard147.moviecataloguelocalstorage.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samuelbernard147.moviecataloguelocalstorage.Adapter.TabFragmentAdapter;
import com.samuelbernard147.moviecataloguelocalstorage.R;

public class FavouriteFragment extends Fragment {
    public static final String TAG = FavouriteFragment.class.getSimpleName();
    TabFragmentAdapter adapter;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    Bundle bundle;

    public static int currentFav = 0;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    public void setQuery(String query) {
        bundle.putString("query", query);
        setupAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = new Bundle();

        mViewPager = view.findViewById(R.id.vp_fav);
        setupAdapter();

        mTabLayout = view.findViewById(R.id.tl_fav);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                currentFav = mViewPager.getCurrentItem();
                Log.d(TAG, "ViewPager Position :" + currentFav);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    public void setupAdapter() {
        adapter = new TabFragmentAdapter(getChildFragmentManager());

        FavMovieFragment favMovieFragment = new FavMovieFragment();
        FavTvShowFragment favTvShowFragment = new FavTvShowFragment();

        switch (currentFav) {
            case 0:
                favMovieFragment.setArguments(bundle);
                break;
            case 1:
                favTvShowFragment.setArguments(bundle);
                break;
        }

        adapter.addFragment(favMovieFragment, getResources().getString(R.string.title_movie));
        adapter.addFragment(favTvShowFragment, getResources().getString(R.string.title_tv));
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(currentFav);
    }
}
