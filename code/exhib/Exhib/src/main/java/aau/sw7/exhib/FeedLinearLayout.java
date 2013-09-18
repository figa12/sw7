package aau.sw7.exhib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Jesper on 17-09-13.
 */
public class FeedLinearLayout extends ListLinearLayout<FeedItem> {

    public FeedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        super.addViewAtBottom(new FeedItem("Nr 1", "Hest"));
        super.addViewAtBottom(new FeedItem("Nr 2", "Hest"));
        super.addViewAtBottom(new FeedItem("Nr 3", "Hest"));
    }

    @Override
    public View makeView(FeedItem feedItem) {
        TextView textView = new TextView(super.getContext());
        textView.setText(feedItem.getHeader());

        return textView;
    }
}
