package map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jacob on 10/11/13.
 */
public class Square {
    private LatLng bottomLeft;
    private LatLng bottomRight;
    private LatLng topRight;
    private LatLng topLeft;
    private LatLng center;

    public Square(LatLng topLeft, LatLng bottomRight){
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
        this.bottomLeft = new LatLng(bottomRight.latitude, topLeft.longitude);
        this.topRight = new LatLng(topLeft.latitude, bottomRight.longitude);
        this.center = this.calculateCenter(bottomLeft, topRight);
    }

    public Square(LatLng bottomLeft, LatLng bottomRight, LatLng topRight, LatLng topLeft){
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.topRight = topRight;
        this.topLeft = topLeft;
        this.center = this.calculateCenter(bottomLeft, topRight);
    }

    public Square(double top, double left, double bottom, double right){
        this(new LatLng(top,left), new LatLng(bottom,right));
    }

    public LatLng getCenter(){
        return this.center;
    }

    public LatLng getBottomLeft() {
        return this.bottomLeft;
    }

    public LatLng getBottomRight() {
        return this.bottomRight;
    }

    public LatLng getTopRight() {
        return this.topRight;
    }

    public LatLng getTopLeft(){
        return this.topLeft;
    }

    private LatLng calculateCenter(LatLng bottomLeft, LatLng topRight) {
        double lng = bottomLeft.longitude + (Math.abs(topRight.longitude - bottomLeft.longitude) / 2.0);
        double lat = bottomLeft.latitude  + (Math.abs(topRight.latitude - bottomLeft.latitude) / 2.0);
        return new LatLng(lat, lng);
    }

    public ArrayList<LatLng> getSquareBounds(){
        return new ArrayList<LatLng>(Arrays.asList(this.bottomLeft,this.bottomRight,this.topRight,this.topLeft));
    }

    public ArrayList<LatLng> toList(){
        return new ArrayList<LatLng>(Arrays.asList(this.bottomLeft,this.bottomRight,this.topRight,this.topLeft,this.center));
    }
}
