package aau.sw7.exhib;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

/**
 * Created by Jacob on 07-11-13.
 */
public class MapController {
    private GoogleMap googleMap;
    private TileOverlay floorPlanOverlay;

    public MapController(GoogleMap map ) {
        this.googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
        tileOverlayOptions.tileProvider(new FloorTileProvider("FloorPlan"));
        floorPlanOverlay = map.addTileOverlay(tileOverlayOptions);
    }

    public void addMarker(LatLng latLng, String title){
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
    }

    public void addPolyline(){

    }

    public void moveCamera(LatLng latLng, int zoom){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

}
