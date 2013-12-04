package aau.sw7.exhib;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.message.BasicNameValuePair;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.TextRecord;

import java.util.ArrayList;

import NfcForeground.NfcForegroundFragment;
import map.Graph;
import map.MapController;
import map.MapWrapperLayout;
import map.Node;
import map.OnInfoWindowElemTouchListener;


public class TabActivity extends NfcForegroundFragment implements ActionBar.TabListener, FloorFragment.OnFloorFragmentListener {
    public static MapController mapController;
    private static Graph graph;
    private static ArrayList<BoothItem> boothItems;
    private static BoothItem targetBooth;
    private static Node sourceNode;
    private MapWrapperLayout mapWrapperLayout;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private ImageView infoImage;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();

    @Override
    public void onMapReady(GoogleMap map) {
        mapController = new MapController(map, this);
        mapWrapperLayout = this.getFloorFragment().getMapWrapperLayout();
        mapWrapperLayout.init(map, getPixelsFromDp(this, 39 + 20));
        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance

        this.initializeInfoButton();
        this.initializeInfoWindow(map);
        mapController.initialize();

        if(boothItems != null && graph != null) {
            //this.mapController.setCustomInfoWindow(this.getLayoutInflater(), boothItems);
            mapController.drawBooths(boothItems);
            mapController.drawGraph(graph);

            //ArrayList<Node> path = this.graph.shortestRoute(5,6);
            //this.mapController.drawPolyline(path, 5, Color.RED, 3);
        }       
    }

    public static final String BOOTH_ITEMS = "boothItems";

    private CustomViewPager viewPager;
    private AppSectionsPagerAdapter appSectionsPagerAdapter;

    private boolean locked = false;

    private long exhibId = 0;
    private long userId = 0;
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        this.viewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    protected void onNfcScanned(ArrayList<Record> records) {
        //TODO if a new exhibition id is scanned, then open the app again with the package?

        long exhibId = 0L;
        long nodeId = 0L;

        for (int i = 0; i < records.size(); i++) {

            if (records.get(i) instanceof AndroidApplicationRecord) {
                AndroidApplicationRecord appRecord = (AndroidApplicationRecord) records.get(i);
            } else if (records.get(i) instanceof TextRecord) {
                TextRecord textRecord = (TextRecord) records.get(i);

                if (i == 0) {
                    exhibId = Long.valueOf(textRecord.getText());
                } else if (i == 1 && records.size() > 2) {
                    nodeId = Long.valueOf(textRecord.getText()); //this can either be a node ID
                }

            }
        }

        if(nodeId != 0L){
            this.showOnMap(nodeId);
        }
    }

    private void initializeInfoButton(){
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
                getResources().getDrawable(R.drawable.btn_default_normal_holo_light),
                getResources().getDrawable(R.drawable.btn_default_pressed_holo_light))
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                targetBooth = TabActivity.findBoothByName(marker.getTitle());
                calculateAndDrawRoute();
                Toast.makeText(TabActivity.this, "Navigating to " +  marker.getTitle(), Toast.LENGTH_SHORT).show();
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);
    }

    private void initializeInfoWindow(GoogleMap map){
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());

                BoothItem boothItem = findBoothByName(marker.getTitle());

                if(boothItem != null){
                    imageLoader.displayImage(boothItem.getCompanyLogo(), infoImage, imageLoaderOptions);
                }else{
                    //imgview none, is standard ic_launcher.png
                }

                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
    }

    private long pendingId = 0;
    private void showOnMap(long nodeId) {
        // index 3 should be the map, check if index 3 exists
        if(nodeId != 0L && this.appSectionsPagerAdapter.getCount() > 3) {
            this.viewPager.setCurrentItem(3, true);
            if(graph == null){
                pendingId = nodeId;
            }
            else{
                Node scannedNode = graph.findNodeById(nodeId);
                if(scannedNode.getBoothId() == -1){
                    mapController.animateCamera(scannedNode.getPosition(), 5);
                    this.updateUserLocation(nodeId);
                }
                else{
                    mapController.animateCameraToBooth(findBoothById(scannedNode.getBoothId()));
                    this.updateUserLocation(nodeId);
                }
            }
        }
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private void updateUserLocation(Long nodeId){

        sourceNode = graph.findNodeById(nodeId); //Set the new sourceNode
        mapController.removePreviousUserLocationerMarker(); //Remove the previous red marker

        if(sourceNode.getBoothId() == -1L){
            graph.setUserLocation(sourceNode.getPosition());
        }else{
            graph.setUserLocation(findBoothById(sourceNode.getBoothId()).getSquareCenter());
        }

        mapController.drawMarker(graph.getUserLocation(),"YOU ARE HERE!","",R.drawable.iamhere);

        //if destination reached
        /*if(nodeId == targetBooth.getBoothId()){
            targetBooth = null;
        }*/

        //draw the route from sourceNode to target
        calculateAndDrawRoute();
    }

    private static void calculateAndDrawRoute(){
        if(targetBooth != null && sourceNode != null){
            mapController.removePreviousRoutePath(); // Remove the previous path
            ArrayList<Node> bestWaypoints;
            if(sourceNode.getBoothId() == -1L){
                bestWaypoints = graph.bestWaypoint(sourceNode, targetBooth); //TODO should be between the sourceBooth and targetBooth
            }else{
                bestWaypoints = graph.bestWaypoint(findBoothById(sourceNode.getBoothId()), targetBooth); //TODO should be between the sourceBooth and targetBooth
            }
            ArrayList<Node> path = graph.shortestRoute(bestWaypoints.get(0).getId(), bestWaypoints.get(1).getId());
            mapController.drawPolyline(path, 5, Color.RED, 5);
        }
    }

    public static BoothItem findBoothById(long boothId){
        for(BoothItem b : boothItems){
            if(b.getBoothId() == boothId){
                return b;
            }
        }
        return null;
    }

    public static BoothItem findBoothByName(String name){
        for(BoothItem b : boothItems){
            if(b.getBoothName().equals(name)){
                return b;
            }
        }
        return null;
    }

    public long getExhibId() {
        return exhibId;
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_tab_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_categories:
                onActionCategorySelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActionCategorySelected() {
        Intent categoriesIntent = new Intent(this, CategoriesActivity.class);

        categoriesIntent.putExtra(MainActivity.EXHIB_ID, this.exhibId);
        categoriesIntent.putExtra(MainActivity.USER_ID, this.userId);

        this.startActivityForResult(categoriesIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.appSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(MainActivity.EXHIB_ID, this.exhibId);
        outState.putLong(MainActivity.USER_ID, this.userId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.exhibId = savedInstanceState.getLong(MainActivity.EXHIB_ID);
        this.userId = savedInstanceState.getLong(MainActivity.USER_ID);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        this.appSectionsPagerAdapter.onDestroy();
        this.appSectionsPagerAdapter = null;
        this.viewPager = null;
        boothItems = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Bundle extras = getIntent().getExtras();
        long nodeId = 0L;

        if (extras != null) {
            // getInt returns 0 if there isn't any mapping to them
            this.exhibId = extras.getLong(MainActivity.EXHIB_ID);
            this.userId = extras.getLong(MainActivity.USER_ID);
            nodeId = extras.getLong(MainActivity.BOOTH_ID);
        } else {
            this.exhibId = 1L;
            this.userId = 1L;
        }

        this.appSectionsPagerAdapter = new AppSectionsPagerAdapter(super.getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();
        /* Remove title bar etc. Doesn't work when applied to the style directly via the xml */
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
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

        new ServerSyncService(this).execute(
                new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_FLOORPLAN)),
                new BasicNameValuePair("Type", "GetFloorPlan"),
                new BasicNameValuePair("UserId", String.valueOf(this.getUserId())));

        if (nodeId != 0L) {
            // Then the initial NFC tag contained a boothId
            // Open the map and show the booth on the map and open a booth acitivty on top of it
            this.showOnMap(nodeId);
        }

        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.popup, null);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.snippet);
        this.infoImage = (ImageView) infoWindow.findViewById(R.id.icon);
        this.infoButton = (Button)infoWindow.findViewById(R.id.button);
        this.infoImage = (ImageView) infoWindow.findViewById(R.id.icon);
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

    public FloorFragment getFloorFragment() {
        return this.appSectionsPagerAdapter.floorFragment;
    }

    public ExhibitionInfoFragment getExhibitionInfoFragment() {
        return this.appSectionsPagerAdapter.exhibitionInfoFragment;
    }

    public void setFloorPlan(Graph newGraph, ArrayList<BoothItem> booths){
        graph = newGraph;
        boothItems = booths;
        if(this.getFloorFragment() != null){
            //this.mapController.setCustomInfoWindow(this.getLayoutInflater(), boothItems);
            mapController.drawBooths(boothItems);
            mapController.drawGraph(graph);
        }
        if(pendingId != 0L){
            showOnMap(pendingId);
            pendingId = 0;
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

        public ExhibitionInfoFragment exhibitionInfoFragment;
        public FeedFragment feedFragment;
        public aau.sw7.exhib.FloorFragment floorFragment;
        public ScheduleFragment scheduleFragment;

        public void onDestroy() {
            this.exhibitionInfoFragment = null;
            this.feedFragment = null;
            this.floorFragment = null;
            this.scheduleFragment = null;

            /*for (int i = 0; i < this.getCount(); i++) {
                this.destroyItem(TabActivity.this.viewPager, i, this.getItem(i)); // attempt to delete memory leakers
            }*/
        }

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
                    return this.floorFragment = new aau.sw7.exhib.FloorFragment();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
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

    public void onClickLockButton(View v) {
        if (this.locked == false) {
            this.viewPager.setPagingEnabled(false);
            this.locked = true;
        } else {
            this.viewPager.setPagingEnabled(true);
            this.locked = false;
        }
    }

}
