package map;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.List;

import aau.sw7.exhib.BoothItem;
import aau.sw7.exhib.R;

/**
 * Created by Jacob on 07-11-13.
 */
public class MapController {
    private GoogleMap googleMap;
    private TileOverlay floorPlanOverlay;
    private List<Marker> markerList;
    private List<Polygon> polygonList;
    private List<Polyline> polylineList;
    private Activity parentActivity;

    public MapController(GoogleMap map, Activity parent) {
        this.googleMap = map;
        this.parentActivity = parent;
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
    public void drawMarker(LatLng latLng, String title, String snippet, int picture){ //TODO implement picture
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(picture));
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

    public void drawPolyline(ArrayList<Node> nodes, float strokeWidth, int color, int zIndex){
        ArrayList<LatLng> polyPoints = new ArrayList<LatLng>();
        for(Node n : nodes){
            polyPoints.add(n.getPosition());
        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(polyPoints)
                .width(strokeWidth)
                .color(color)
                .zIndex(zIndex);
        Polyline polyline = this.googleMap.addPolyline(polylineOptions);
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
        drawPolygon(boothItem.getSquareBounds(), 5, Color.DKGRAY, Color.GREEN, 2);
        //add add marker with
        if(!boothItem.getSquareCenter().equals(new LatLng(0.0,0.0))){
            drawMarker(boothItem.getSquareCenter(), boothItem.getBoothName(), boothItem.getDescription(), R.drawable.info);
        }

    }

    public void drawGraph(Graph graph){
        ArrayList<Node> poly = new ArrayList<Node>(graph.getPolylinePath());
        drawPolyline(poly, 5, Color.BLUE, 2);

        for(Node n : graph.getNodes()){
            if(!n.getPosition().equals(new LatLng(0.0,0.0))){
                drawMarker(n.getPosition(), "" + n.getID());
            }
        }
    }

    /***
     * Moves the camera on the map, no animation
     * @param latLng
     * @param zoom
     */
    public void animateCamera(LatLng latLng, int zoom){
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 2000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                googleMap.getUiSettings().setScrollGesturesEnabled(true);
            }

            @Override
            public void onCancel() {
                googleMap.getUiSettings().setAllGesturesEnabled(true);
            }
        });
    }

    public void animateCameraToBooth(BoothItem boothItem){
        animateCamera(boothItem.getSquareCenter(),5);
    }

    public void setCustomInfoWindow(LayoutInflater layoutInflater, ArrayList<BoothItem> booths){
        PopupAdapter customInfoWindow = new PopupAdapter(layoutInflater, booths);
        this.googleMap.setInfoWindowAdapter(customInfoWindow);

    }

    /***
     * initialize the map...
     */
    public void initialize(){
        this.animateCamera(new LatLng(0, 0), 2);
    }

}
