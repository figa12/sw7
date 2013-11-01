package aau.sw7.exhib;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jacob on 31-10-13.
 */
public class FloorTileProvider extends UrlTileProvider {
    private static final String FORMAT;
    private String mMapIdentifier;
    static {
        FORMAT = "http://figz.dk/dl/%s/%d/%d/%d.png";
    }

    public FloorTileProvider(String mapIdentifier) {
        super(256, 256);

        this.mMapIdentifier = mapIdentifier;
    }

    @Override
    public URL getTileUrl(int x, int y, int z) {
        try {
            return new URL(String.format(FORMAT, this.mMapIdentifier, z, x, y));
        }
        catch (MalformedURLException e) {
            return null;
        }
    }
}
