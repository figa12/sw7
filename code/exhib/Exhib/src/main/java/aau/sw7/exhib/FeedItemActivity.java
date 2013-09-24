package aau.sw7.exhib;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jerian on 23-09-13.
 */
public class FeedItemActivity extends Activity {

    private static final int LINES_TO_INDENT = 4;

    private FeedItem feedItem;

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_feed_item);

        Bundle extras = super.getIntent().getExtras();
        if(extras != null) {
            this.feedItem = extras.getParcelable(FeedLinearLayout.FEED_ITEM);
        }

        Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
        ImageView feedImageView = (ImageView) findViewById(R.id.feedImage);
        feedImageView.setBackgroundDrawable(drawable);

        int leftMargin = feedImageView.getMeasuredWidth() + 204; //TODO fix raw number, getwidth() returns 0

        SpannableString spannableString = new SpannableString(this.feedItem.getFeedText());
        spannableString.setSpan(new MyLeadingMarginSpan2(FeedItemActivity.LINES_TO_INDENT, leftMargin), 0, spannableString.length(), 0);

        TextView feedDescription = (TextView) super.findViewById(R.id.feedSummary);
        feedDescription.setText(spannableString);

        TextView headerTextView = (TextView) super.findViewById(R.id.feedHeader);
        headerTextView.setText(this.feedItem.getFeedHeader());

        TextView dateTextView = (TextView) super.findViewById(R.id.feedTime);
        dateTextView.setText(this.feedItem.getDateTimeRepresentation());
    }

    private class MyLeadingMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2 {
        private int margin;
        private int lines;

        MyLeadingMarginSpan2(int lines, int margin) {
            this.margin = margin;
            this.lines = lines;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            if (first) {
                return margin;
            } else {
                return 0;
            }
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                      int top, int baseline, int bottom, CharSequence text,
                                      int start, int end, boolean first, Layout layout) {}

        @Override
        public int getLeadingMarginLineCount() {
            return lines;
        }
    }
}