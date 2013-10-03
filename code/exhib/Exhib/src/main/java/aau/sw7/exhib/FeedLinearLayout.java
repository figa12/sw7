package aau.sw7.exhib;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jerian on 17-09-13.
 */
public class FeedLinearLayout extends ListLinearLayout<FeedItem> {

    /** Enum used to specify where the items should be added. Can be either {@code Top} or {@code Bottom}. */
    public enum AddAt {
        Top, Bottom
    }

    /* Get the ImageLoader instance */
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();

    /** Key string to get data from bundle. */
    public static final String FEED_ITEM = "FeedItem";

    private Intent feedIntent;

    public FeedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.feedIntent = new Intent(context, FeedItemActivity.class);
    }

    /**
     * Adds the list of feed items to this {@link aau.sw7.exhib.FeedLinearLayout}.
     * @param feedItems List of feed items.
     * @param addAt Specify where to add the items.
     * @see aau.sw7.exhib.FeedLinearLayout.AddAt
     */
    public void addFeedItems(ArrayList<FeedItem> feedItems, AddAt addAt) {
        if(addAt == AddAt.Top) {
            Collections.reverse(feedItems);
        }

        for (FeedItem feedItem : feedItems) {
            this.addFeedItem(feedItem, addAt);
        }
    }

    /**
     * Adds the feed item to this {@link aau.sw7.exhib.FeedLinearLayout}.
     * @param feedItem The feed item.
     * @param addAt Specify where to add the item.
     * @see aau.sw7.exhib.FeedLinearLayout.AddAt
     */
    public void addFeedItem(FeedItem feedItem, AddAt addAt) {
        switch(addAt) {
            case Bottom:
                super.addViewAtBottom(feedItem);
                break;
            case Top:
                super.addViewAtTop(feedItem);
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View makeView(FeedItem feedItem) {
        // Get inflater and inflate feed_list_item.xml
        LayoutInflater layoutInflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View feedView = layoutInflater.inflate(R.layout.feed_list_item, null);

        // Load the the feed logo asynchronously with universal-image-loader
        ImageView feedLogoImageView = (ImageView) feedView.findViewById(R.id.feedImage);
        this.imageLoader.displayImage(feedItem.getFeedLogoURL(), feedLogoImageView, this.imageLoaderOptions);

        TextView headerTextView = (TextView) feedView.findViewById(R.id.feedHeader);
        headerTextView.setText(feedItem.getFeedHeader());

        TextView summaryTextView = (TextView) feedView.findViewById(R.id.feedSummary);
        summaryTextView.setText(feedItem.getFeedText());

        TextView feedTextView = (TextView) feedView.findViewById(R.id.feedTime);
        feedTextView.setText(feedItem.getDateTimeRepresentation());

        // Put a click listener on the activity_main layout
        feedView.findViewById(R.id.mainLayout).setOnClickListener(new FeedItemClickListener(feedItem));

        return feedView;
    }

    /**
     * A class implementing {@link android.view.View.OnClickListener}.
     * The class contains the data of the {@link android.view.View} representing a {@link aau.sw7.exhib.FeedItem}.
     */
    private class FeedItemClickListener implements OnClickListener {
        private FeedItem feedItem;

        public FeedItemClickListener(FeedItem feeditem) {
            this.feedItem = feeditem;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onClick(View v) {
            FeedLinearLayout.this.feedIntent.putExtra(FeedLinearLayout.FEED_ITEM, this.feedItem);
            FeedLinearLayout.this.getContext().startActivity(FeedLinearLayout.this.feedIntent);
        }
    }
}
