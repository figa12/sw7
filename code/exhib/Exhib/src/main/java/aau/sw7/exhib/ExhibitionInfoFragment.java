package aau.sw7.exhib;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

/**
 * Created by jerian on 17-10-13.
 */
public class ExhibitionInfoFragment extends Fragment {

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();

    private boolean viewDestroyed = true;

    private ImageView exhibitionIcon;
    private TextView exhibitionNameTextView;
    private TextView descriptionTextView;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exhibtion_info, container, false);

        this.exhibitionIcon = (ImageView) rootView.findViewById(R.id.exhibitionIcon);
        this.exhibitionNameTextView = (TextView) rootView.findViewById(R.id.exhibitionName);
        this.descriptionTextView = (TextView) rootView.findViewById(R.id.description);

        this.viewDestroyed = false;

        BasicNameValuePair requestCode = new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_EXHIBITION_INFO));
        BasicNameValuePair getFeeds = new BasicNameValuePair("Type", "GetExhibitionInfo");
        BasicNameValuePair user = new BasicNameValuePair("UserId", "1");
        new ServerSyncService(super.getActivity()).execute(requestCode, getFeeds, user);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.viewDestroyed = true;
    }

    public void setExhibitionInfo(String imageURL, String exhibitionName, String exhibitionDescription) {
        if(!this.viewDestroyed) {
            this.imageLoader.displayImage("http://figz.dk/imageHere.png", this.exhibitionIcon, this.imageLoaderOptions);
            this.exhibitionNameTextView.setText(exhibitionName);
            this.descriptionTextView.setText(exhibitionDescription);
        }
    }
}