package aau.sw7.exhib;

import android.os.Bundle;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.ndeftools.Record;

import java.util.ArrayList;

import NfcForeground.NfcForegroundActivity;

/**
 * Created by jerian on 31-10-13.
 */
public class BoothActivity extends NfcForegroundActivity {

    private BoothItem boothItem;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();

    @Override
    protected void onNfcScanned(ArrayList<Record> records) {
        //TODO
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booth);

        Bundle extras = super.getIntent().getExtras();

        if (extras != null) {

        }
    }
}