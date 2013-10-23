package aau.sw7.exhib;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

//import android.support.v4.view.ViewPager;

public class TabActivity extends FragmentActivity implements ActionBar.TabListener {

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        this.viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    private static String TAG = TabActivity.class.getSimpleName();

    private CustomViewPager viewPager;
    private AppSectionsPagerAdapter appSectionsPagerAdapter;

    private boolean locked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        this.viewPager = new CustomViewPager(this);

        this.appSectionsPagerAdapter = new AppSectionsPagerAdapter(super.getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();
        /* Remove title bar etc. Doesn't work when applied to the style directly via the xml */
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        this.viewPager = (CustomViewPager) super.findViewById(R.id.pager);
        this.viewPager.setAdapter(this.appSectionsPagerAdapter);
        this.viewPager.setOnPageChangeListener(new CustomViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < appSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(appSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    public boolean getLock() {
        return this.locked;
    }

    public void setLock(boolean lock) {
        this.locked = lock;
    }

    public CustomViewPager getViewPager() {
        return this.viewPager;
    }

    public FeedFragment getFeedFragment() {
        return this.appSectionsPagerAdapter.feedFragment;
    }

    public ScheduleFragment getScheduleFragment() {
        return this.appSectionsPagerAdapter.scheduleFragment;
    }

    public ExhibitionInfoFragment getExhibitionInfoFragment() {
        return this.appSectionsPagerAdapter.exhibitionInfoFragment;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ExhibitionInfoFragment exhibitionInfoFragment;
        public FeedFragment feedFragment;
        public MapFragment mapFragment;
        public ScheduleFragment scheduleFragment;

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return this.exhibitionInfoFragment = new ExhibitionInfoFragment();

                case 1:
                    return this.feedFragment = new FeedFragment();

                case 2:
                    return this.scheduleFragment = new ScheduleFragment();

                case 3:
                    return this.mapFragment = new MapFragment();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public String getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";

                case 1:
                    return "Feeds";

                case 2:
                    return "Schedule";

                case 3:
                    return "Map";

                default:
                    return null;

            }
        }

    }

    public void onClickLockButton(View v)
    {
        if (this.locked == false) {
            this.viewPager.setPagingEnabled(false);
            this.locked = true;
        }
        else {
            this.viewPager.setPagingEnabled(true);
            this.locked = false;
        }
    }
    
}
