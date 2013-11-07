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
     * Does something...
     * @param latLng
     * @param title
     */
    public void addMarker(LatLng latLng, String title){
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
    public void addPolyline(List<LatLng> latLngs, float strokeWidth, int color, int zIndex){
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(latLngs)
                .width(strokeWidth)
                .color(color)
                .zIndex(zIndex);
        Polyline polyline = this.googleMap.addPolyline(polylineOptions);
        //this.polylineList.add(polyline);
    }

    public void addPolygone(List<LatLng> latLngs, float strokeWidth, int strokeColor, int fillColor, int zIndex){
        PolygonOptions polygonOptions = new PolygonOptions()
                .addAll(latLngs)
                .strokeWidth(strokeWidth)
                .strokeColor(strokeColor)
                .fillColor(fillColor)
                .zIndex(zIndex);
        Polygon polygon =  this.googleMap.addPolygon(polygonOptions);
        //polygonList.add(polygon);
    }

    /***
     * Moves the camera on the map, no animation
     * @param latLng
     * @param zoom
     */
    public void moveCamera(LatLng latLng, int zoom){
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /***
     * initialize the map...
     */
    public void initialize(){
        this.addMarker(new LatLng(0,0), "0,0");
        this.addPolyline(new ArrayList<LatLng>(Arrays.asList(new LatLng(0,0), new LatLng(0,10), new LatLng(10,10), new LatLng(10,0))), 5, Color.RED, 2);
        this.moveCamera(new LatLng(0,0), 3);
        this.addPolygone(new ArrayList<LatLng>(Arrays.asList(new LatLng(20,20), new LatLng(20,25), new LatLng(25,25),new LatLng(25,20)) ), 5, Color.BLACK, Color.GREEN, 2);
    }

}
