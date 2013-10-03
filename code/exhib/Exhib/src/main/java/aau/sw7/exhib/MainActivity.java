package aau.sw7.exhib;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
//import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.TextRecord;

import java.util.List;

public class MainActivity extends FragmentActivity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback, ActionBar.TabListener {

    private boolean locked = false;

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

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    private static String TAG = MainActivity.class.getSimpleName();

    public CustomViewPager viewPager;
    private AppSectionsPagerAdapter appSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Create global configuration and initialize ImageLoader with this configuration

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);

        // initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


    }

    public FeedFragment getFeedFragment() {
        return this.appSectionsPagerAdapter.feedFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundMode();
    }

    @Override
    protected void onPause() {
        super.onResume();

        disableForegroundMode();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        Log.d(TAG, "createNdefMessage");

        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        Log.d(TAG, "onNdefPushComplete");


        throw new IllegalArgumentException("Not implemented");
    }

    public void enableForegroundMode() {
        Log.d(TAG, "enableForegroundMode");

        // foreground mode gives the current active application priority for reading scanned tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) { // this method is called when an NFC tag is scanned
        NfcHandler handler = new NfcHandler(this.TAG, intent, this);
        List<Record> records = handler.newIntentEvent();

        if (records != null) {
            for(Record record : records) {

                if(record instanceof AndroidApplicationRecord) {
                    AndroidApplicationRecord aar = (AndroidApplicationRecord)record;
                    Log.d(TAG, "Package is " + aar.getPackageName());
                }

                if(record instanceof TextRecord) {
                    TextRecord hest = (TextRecord)record;
                    Log.d(TAG, "Teksten er " + hest.getText());
                }
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public FeedFragment feedFragment;
        public MapFragment mapFragment;

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    this.feedFragment = new FeedFragment();
                    return this.feedFragment;

                case 1:
                    this.mapFragment = new MapFragment();
                    return this.mapFragment;


                default:
                    return null;


            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Feeds";

                case 1:
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
