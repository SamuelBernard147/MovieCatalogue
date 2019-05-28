package com.samuelbernard147.favouritemovie;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.samuelbernard147.favouritemovie.Adapter.TabFragmentAdapter;
import com.samuelbernard147.favouritemovie.fragment.MovieFragment;
import com.samuelbernard147.favouritemovie.fragment.TvShowFragment;

public class MainActivity extends AppCompatActivity {
    TabFragmentAdapter adapter;
    TabLayout mTabLayout;
    ViewPager viewPagerMain;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        viewPagerMain = findViewById(R.id.vp_main);
        toolbar = findViewById(R.id.tb_main);
        mTabLayout = findViewById(R.id.tl_main);

//        Fungsi merubah title
        toolbar.setTitle("Movie Catalogue");
        setSupportActionBar(toolbar);

//        Fragment
        MovieFragment movieFragment = new MovieFragment();
        TvShowFragment tvShowFragment = new TvShowFragment();

//        Adapter
        adapter = new TabFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(movieFragment, getResources().getString(R.string.title_movie));
        adapter.addFragment(tvShowFragment, getResources().getString(R.string.title_tv));

        viewPagerMain.setAdapter(adapter);
        viewPagerMain.setCurrentItem(0);

        mTabLayout.setupWithViewPager(viewPagerMain);
    }
}