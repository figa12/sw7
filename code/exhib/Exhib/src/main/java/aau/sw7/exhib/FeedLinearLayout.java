package aau.sw7.exhib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Jesper on 17-09-13.
 */
public class FeedLinearLayout extends ListLinearLayout<FeedItem> {

    public FeedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.addThirty();
    }

    private int i = 1;

    public void addThirty() {
        int start = i;
        for (i = start; i < 30 + start; i++) {
            super.addViewAtBottom(new FeedItem("Nr " + String.valueOf(i), "Bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla",
                    "Author here"));
        }
    }

    @Override
    public View makeView(FeedItem feedItem) {
        LayoutInflater layoutInflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View feedView = layoutInflater.inflate(R.layout.feed_list_item, null);

        TextView headerTextView = (TextView) feedView.findViewById(R.id.feedHeader);
        headerTextView.setText(feedItem.getFeedHeader());

        TextView summaryTextView = (TextView) feedView.findViewById(R.id.feedSummary);
        summaryTextView.setText(feedItem.getFeedText());

        TextView feedTextView = (TextView) feedView.findViewById(R.id.feedTime);
        feedTextView.setText("Today at " + String.valueOf(feedItem.getFeedDateTime().getTime()));

        return feedView;
    }
}
