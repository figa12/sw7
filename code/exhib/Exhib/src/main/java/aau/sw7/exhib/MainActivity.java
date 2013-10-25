package aau.sw7.exhib;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.TextRecord;

import java.util.ArrayList;

import NfcForeground.NfcForegroundActivity;

/**
 * Created by Reedtz on 03-10-13.
 */

public class MainActivity extends NfcForegroundActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);

        final ActionBar actionBar = getActionBar();
        /* Remove title bar etc. Doesn't work when applied to the style directly via the xml */
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
    }

    //temp button click to open the tab acitivity
    public void onClickOpenCategory (View v) {
        super.startActivity(new Intent(this, CategoriesActivity.class));
    }

    @Override
    protected void onNfcScanned(ArrayList<Record> records) {
        Bundle bundle = new Bundle();

        for (int i = 0; i < records.size(); i++) {

            if (records.get(i) instanceof AndroidApplicationRecord) {
                AndroidApplicationRecord appRecord = (AndroidApplicationRecord) records.get(i);
            } else if (records.get(i) instanceof TextRecord) {
                TextRecord textRecord = (TextRecord) records.get(i);

                if (i == 0) {
                    bundle.putInt("exhibID", Integer.parseInt(textRecord.getText()));
                } else if (i == 1 && records.size() > 2) {
                    bundle.putInt("boothID", Integer.parseInt(textRecord.getText()));
                }
            }
        }

        Intent categoryIntent = new Intent(this, CategoriesActivity.class);
        categoryIntent.putExtras(bundle);

        this.startActivity(categoryIntent);
    }
}
