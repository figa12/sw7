package map;
import com.google.android.gms.maps.model.UrlTileProvider;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jacob on 31-10-13.
 */
public class FloorTileProvider extends UrlTileProvider {
    private static final String TILEURL;
    private static final String ERRORURL;
    private String mapIdentifier;
    private URL ErrorURL;
    static {
        TILEURL = "http://figz.dk/dl/%s/%d/%d/%d.png";
        ERRORURL = "http://figz.dk/dl/%s/none.png";
    }


    public FloorTileProvider(String mapIdentifier) {
        super(256, 256);
        this.mapIdentifier = mapIdentifier;
        try {
            String sError = String.format(ERRORURL, this.mapIdentifier);
            this.ErrorURL = new URL(sError);
        }
        catch(MalformedURLException e) {
            this.ErrorURL = null;
        }
    }

    @Override
    public URL getTileUrl(int x, int y, int z) {
        try {
            int reversedY = (1 << z) - y - 1;
            String surl = String.format(TILEURL, this.mapIdentifier, z,x,reversedY);
            URL testurl = new URL(surl);
            return testurl ;
        }
        catch (MalformedURLException e) {
            return ErrorURL;
        }
    }


}
