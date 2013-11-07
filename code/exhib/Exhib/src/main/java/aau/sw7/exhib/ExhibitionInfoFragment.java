package aau.sw7.exhib;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.message.BasicNameValuePair;

/**
 * Created by jerian on 17-10-13.
 */
public class ExhibitionInfoFragment extends Fragment {

    private String address;
    private int zip;
    private String country;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();

    private boolean viewDestroyed = true;

    private ImageView exhibitionIcon;
    private TextView exhibitionNameTextView;
    private TextView descriptionTextView;
    private ProgressBar progressCircle;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exhibtion_info, container, false);

        this.exhibitionIcon = (ImageView) rootView.findViewById(R.id.exhibitionIcon);
        this.exhibitionNameTextView = (TextView) rootView.findViewById(R.id.exhibitionName);
        this.descriptionTextView = (TextView) rootView.findViewById(R.id.description);
        this.progressCircle = (ProgressBar) rootView.findViewById(R.id.progressCircle);

        this.viewDestroyed = false;

        new ServerSyncService(super.getActivity()).execute(
                new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_EXHIBITION_INFO)),
                new BasicNameValuePair("Type", "GetExhibitionInfo"),
                new BasicNameValuePair("ExhibId", String.valueOf(((TabActivity) this.getActivity()).getExhibId())));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.viewDestroyed = true;
        this.progressCircle.setVisibility(View.VISIBLE);

        // Clean up references
        this.exhibitionIcon = null;
        this.exhibitionNameTextView = null;
        this.descriptionTextView = null;
        this.progressCircle = null;
    }

    public void setExhibitionInfo(String imageURL, String exhibitionName, String exhibitionDescription, String address, int zip, String country) {
        if(!this.viewDestroyed) {
            this.progressCircle.setVisibility(View.GONE);

            this.imageLoader.displayImage(imageURL, this.exhibitionIcon, this.imageLoaderOptions);
            this.exhibitionNameTextView.setText(exhibitionName);
            this.descriptionTextView.setText(exhibitionDescription);

            this.address = address;
            this.zip = zip;
            this.country = country;
        }
    }
}