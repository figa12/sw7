package aau.sw7.exhib;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jerian on 17-09-13.
 */
public class FeedLinearLayout extends ListLinearLayout<FeedItem> {

    public static final String FEED_ITEM = "FeedItem";

    private Intent feedIntent;

    public FeedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.feedIntent = new Intent(context, FeedItemActivity.class);

        this.addThirty();
    }

    private int i = 1;

    public void addThirty() {
        int start = i;
        for (i = start; i < 30 + start; i++) {
            String str = "";
            for (int j = 0; j < 350; j += 2)
            {
                str += "lang nyhed ";
            }
            str += "hest";
            FeedItem feedItem = new FeedItem("Nr " + String.valueOf(i), str,
                    "Author here");
            super.addViewAtBottom(feedItem);
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

        feedView.findViewById(R.id.mainLayout).setOnClickListener(new FeedItemClickListener(feedItem));

        return feedView;
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
