package aau.sw7.exhib;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jerian on 17-09-13.
 */
public class FeedLinearLayout extends ListLinearLayout<FeedItem> {

    public enum AddAt {
        Top, Bottom
    }

    /** Key string to get data from bundle. */
    public static final String FEED_ITEM = "FeedItem";

    private Intent feedIntent;

    public FeedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.feedIntent = new Intent(context, FeedItemActivity.class);
    }

    private int i = 1;

    public void addFeedItems(ArrayList<FeedItem> feedItems, AddAt addAt) {
        if(addAt == AddAt.Top) {
            Collections.reverse(feedItems);
        }

        for (FeedItem feedItem : feedItems) {
            switch(addAt) {
                case Bottom:
                    super.addViewAtBottom(feedItem);
                    break;
                case Top:
                    super.addViewAtTop(feedItem);
                    break;
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View makeView(FeedItem feedItem) {
        LayoutInflater layoutInflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View feedView = layoutInflater.inflate(R.layout.feed_list_item, null);

        TextView headerTextView = (TextView) feedView.findViewById(R.id.feedHeader);
        headerTextView.setText(feedItem.getFeedHeader());

        TextView summaryTextView = (TextView) feedView.findViewById(R.id.feedSummary);
        summaryTextView.setText(feedItem.getFeedText());

        TextView feedTextView = (TextView) feedView.findViewById(R.id.feedTime);
        feedTextView.setText(feedItem.getDateTimeRepresentation());

        /* Put a click listener on the main layout */
        feedView.findViewById(R.id.mainLayout).setOnClickListener(new FeedItemClickListener(feedItem));

        ImageView iconImageView = (ImageView) feedView.findViewById(R.id.feedImage);
        // TODO fix drawable
        //iconImageView.setImageDrawable(this.loadImageFromWebOperations("http://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Microsoft_logo.svg/439px-Microsoft_logo.svg.png"));

        return feedView;
    }

    public Drawable loadImageFromWebOperations(String url) {
        try {
            InputStream inputStream = (InputStream) new URL(url).getContent();
            Drawable drawable = Drawable.createFromStream(inputStream, "src name");

            return drawable;
        } catch (Exception e) {
            return null;
        }
    }

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
