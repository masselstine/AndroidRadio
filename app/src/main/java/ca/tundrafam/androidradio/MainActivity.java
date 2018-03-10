package ca.tundrafam.androidradio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements OnPageChangeListener {

    private ViewPager pager;
    private Playlist playlist;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_playlist:
                    pager.setCurrentItem(0);
                    return true;
                case R.id.navigation_favorites:
                    pager.setCurrentItem(1);
                    return true;
                case R.id.navigation_settings:
                    pager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onPageScrollStateChanged(int pos) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int pos) {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        switch(pos) {
            case 0:
                navigation.setSelectedItemId(R.id.navigation_playlist);
                break;
            case 1:
                navigation.setSelectedItemId(R.id.navigation_favorites);
                break;
            case 2:
                navigation.setSelectedItemId(R.id.navigation_settings);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //playlist = new Playlist();
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        private Fragment playlist_fragment = null;
        private Fragment favorites_fragment = null;
        private Fragment settings_fragment = null;

        public MainPagerAdapter(FragmentManager mgr) {
            super(mgr);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;

            switch (position) {
                case 0:
                    if (playlist_fragment == null)
                        playlist_fragment = new PlaylistFragment();
                    frag = playlist_fragment;
                    break;
                case 1:
                    if (favorites_fragment == null)
                        favorites_fragment = new FavoritesFragment();
                    frag = favorites_fragment;
                    break;
                case 2:
                    if (settings_fragment == null)
                        settings_fragment = new SettingsFragment();
                    frag = settings_fragment;
                    break;
                default:
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
