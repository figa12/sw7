package aau.sw7.exhib;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.ndeftools.Record;

import java.util.ArrayList;

import NfcForeground.NfcForegroundActivity;

/**
 * Created by jerian on 31-10-13.
 */
public class BoothActivity extends NfcForegroundActivity {

    public static final String BOOTH_ITEM = "boothItem";

    private ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();

    private BoothItem boothItem;

    @Override
    protected void onNfcScanned(ArrayList<Record> records) {
        //TODO
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booth);

        Bundle extras = super.getIntent().getExtras();
        if (extras != null) {
            this.boothItem = extras.getParcelable(BoothActivity.BOOTH_ITEM);
        } else {
            return;
        }

        ImageView logoImageView = (ImageView) super.findViewById(R.id.companyLogo);
        this.imageLoader.displayImage(this.boothItem.getCompanyLogo(), logoImageView, this.imageLoaderOptions);

        TextView nameTextView = (TextView) super.findViewById(R.id.boothName);
        nameTextView.setText("Booth: " + this.boothItem.getBoothName());

        TextView descriptionTextView = (TextView) super.findViewById(R.id.description);
        descriptionTextView.setText(this.boothItem.getDescription());
    }
}