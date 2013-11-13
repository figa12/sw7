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

    public MapController(GoogleMap map) {
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
    public void drawMarker(LatLng latLng, String title, String snippet){ //TODO implement picture
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
        drawPolygon(boothItem.getSquareBounds(), 5, Color.DKGRAY, Color.GREEN, 2);
        //add add marker with
        if(!boothItem.getSquareCenter().equals(new LatLng(0.0,0.0))){
        drawMarker(boothItem.getSquareCenter(), boothItem.getBoothName(), boothItem.getDescription());
        }
    }

    public void drawGraph(Graph graph){
        ArrayList<Node> poly = new ArrayList<Node>();

        for(Edge edge : graph.getEdges()){
            if(poly.indexOf(edge.getFrom()) != -1 ){
                poly.add(poly.indexOf(edge.getFrom())+1,edge.getTo());
                poly.add(poly.indexOf(edge.getTo())+1,edge.getFrom());
            }
            else if(poly.indexOf(edge.getTo()) != -1){ //TODO is this needed?
                poly.add(poly.indexOf(edge.getTo())+1,edge.getFrom());
                poly.add(poly.indexOf(edge.getFrom())+1,edge.getTo());
            }
            else{
                poly.add(edge.getFrom());
                poly.add(edge.getTo());
            }
        }

        ArrayList<LatLng> polyLine = new ArrayList<LatLng>();
        for(Node n : poly){
            polyLine.add(n.getPosition());
        }
        drawPolyline(polyLine,5, Color.BLUE,2);

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
    public void moveCamera(LatLng latLng, int zoom){
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /***
     * initialize the map...
     */
    public void initialize(){
        this.moveCamera(new LatLng(0,0), 3);
    }

}