package com.samuelbernard147.moviecataloguelocalstorage;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.samuelbernard147.moviecataloguelocalstorage.Adapter.TabFragmentAdapter;
import com.samuelbernard147.moviecataloguelocalstorage.fragment.FavouriteFragment;
import com.samuelbernard147.moviecataloguelocalstorage.fragment.MainFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navigation;
    TabFragmentAdapter adapter;
    ViewPager viewPagerMain;
    Toolbar toolbar;
    MenuItem prevMenuItem;
    SearchView searchView;

    SettingsPreference mSettingPreference;

    MainFragment mainFragment;
    FavouriteFragment favouriteFragment;

    int navPosition;

    //    Fungsi item bottom nav
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_movie:
                    viewPagerMain.setCurrentItem(0);
                    break;
                case R.id.navigation_favourite:
                    viewPagerMain.setCurrentItem(1);
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        loadLanguage();
        setContentView(R.layout.activity_main);
        viewPagerMain = findViewById(R.id.vp_main);
        toolbar = findViewById(R.id.tb_main);

//        Fungsi merubah title
        toolbar.setTitle("Movie Catalogue");
        setSupportActionBar(toolbar);

//        Inisialisasi bottom nav
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        Fragment
        mainFragment = new MainFragment();
        favouriteFragment = new FavouriteFragment();

//        Adapter
        adapter = new TabFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mainFragment, getResources().getString(R.string.title_movie));
        adapter.addFragment(favouriteFragment, getResources().getString(R.string.title_fav));

        viewPagerMain.setAdapter(adapter);
        viewPagerMain.setCurrentItem(0);
        viewPagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int I) {
            }

            @Override
            public void onPageSelected(int position) {
                navPosition = viewPagerMain.getCurrentItem();
                prevMenuItem = navigation.getMenu().getItem(position);
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                if (searchView != null) {
                    searchView.setQuery("", false);
                    searchView.setIconifiedByDefault(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        navPosition = viewPagerMain.getCurrentItem();
    }

    //    Load bahasa dari preference
    public void loadLanguage() {
        mSettingPreference = new SettingsPreference(this);
        String lang = mSettingPreference.getLang();
        if (lang != null) {
            Locale mLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration config = res.getConfiguration();
            config.locale = mLocale;
            res.updateConfiguration(config, dm);
        }
    }

    //      Pemanggilan Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                if (navPosition == 0) {
                    mainFragment.setQuery(s);
                } else if (navPosition == 1) {
                    favouriteFragment.setQuery(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (navPosition == 0) {
                    mainFragment.setQuery(s);
                } else if (navPosition == 1) {
                    favouriteFragment.setQuery(s);
                }

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //      Fungsi ketika menu dipilih
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.change_language) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}

/*
    TODO : add notification
    TODO : add stack widget
    TODO : add content provider
    TODO : remove unused resource, clean up code
*/
