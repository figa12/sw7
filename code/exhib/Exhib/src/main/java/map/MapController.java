package map;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aau.sw7.exhib.BoothItem;

/**
 * Created by Jacob on 07-11-13.
 */
public class MapController {
    private GoogleMap googleMap;
    private TileOverlay floorPlanOverlay;
    private List<Marker> markerList;
    private List<Polygon> polygonList;
    private List<Polyline> polylineList;

    public MapController(GoogleMap map ) {
        this.googleMap = map;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
        tileOverlayOptions.tileProvider(new FloorTileProvider("FloorPlan"));
        this.floorPlanOverlay = map.addTileOverlay(tileOverlayOptions);
    }

    /***
     * Draws a marker on the map, with title, snippet and picture
     * @param latLng
     * @param title
     * @param snippet
     */
    public void drawMarker(LatLng latLng, String title, String snippet){
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet);
        Marker marker = this.googleMap.addMarker(markerOptions);
        //this.markerList.add(marker);
    }

    /***
     * Draws a marker on the map, with title.
     * @param latLng
     * @param title
     */
    public void drawMarker(LatLng latLng, String title){
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        Marker marker = this.googleMap.addMarker(markerOptions);
        //this.markerList.add(marker);
    }

    /***
     * adds a polyline to the map given array containing latlngs
     * @param latLngs
     */
    public void drawPolyline(List<LatLng> latLngs, float strokeWidth, int color, int zIndex){
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(latLngs)
                .width(strokeWidth)
                .color(color)
                .zIndex(zIndex);
        Polyline polyline = this.googleMap.addPolyline(polylineOptions);
        //this.polylineList.add(polyline);
    }

    public void drawPolygon(List<LatLng> latLngs, float strokeWidth, int strokeColor, int fillColor, int zIndex){
        PolygonOptions polygonOptions = new PolygonOptions()
                .addAll(latLngs)
                .strokeWidth(strokeWidth)
                .strokeColor(strokeColor)
                .fillColor(fillColor)
                .zIndex(zIndex);
        Polygon polygon =  this.googleMap.addPolygon(polygonOptions);
        //polygonList.add(polygon);
    }

    public void drawBooths(List<BoothItem> boothItems) {
        for(BoothItem b : boothItems){
            drawBooth(b);
        }
    }

    public void drawBooth(BoothItem boothItem) {
        //add shape
        drawPolygon(boothItem.getCoordinates(), 5, Color.DKGRAY, Color.GREEN, 2);
        drawMarker(boothItem.getBoothCoordinate(), boothItem.getBoothName(), boothItem.getDescription());
        //add add marker with
    }

    /***
     * Moves the camera on the map, no animation
     * @param latLng
     * @param zoom
     */
    public void moveCamera(LatLng latLng, int zoom){
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public LatLng vector(LatLng p1, LatLng p2){
        return new LatLng(p2.latitude - p1.latitude, p2.longitude - p2.longitude);
    }

    /***
     * initialize the map...
     */
    public void initialize(){
        this.drawMarker(new LatLng(0, 0), "0,0", "snippet");
        this.drawPolyline(new ArrayList<LatLng>(Arrays.asList(new LatLng(0, 0), new LatLng(0, 10), new LatLng(10, 10), new LatLng(10, 0))), 5, Color.RED, 2);
        this.moveCamera(new LatLng(0,0), 3);
        //this.drawPolygon(new ArrayList<LatLng>(Arrays.asList(new LatLng(20,20), new LatLng(20,25), new LatLng(25,25),new LatLng(25,20)) ), 5, Color.BLACK, Color.GREEN, 2);
        ArrayList<LatLng> hest  = new ArrayList<LatLng>(Arrays.asList(new LatLng(20,20), new LatLng(25,20), new LatLng(25,25), new LatLng(20,25)));
        BoothItem booth = new BoothItem(12, "Tha Shit Booth", "We got it all you name it", "don't know yet", false, hest);
        this.drawBooth(booth);
    }

}
