package aau.sw7.exhib;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by jerian on 23-09-13.
 */
public class FeedItemActivity extends Activity {

    private static final int LINES_TO_INDENT = 4;

    private FeedItem feedItem;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_feed_item);

        Bundle extras = super.getIntent().getExtras();

        if(extras != null) {
            this.feedItem = extras.getParcelable(FeedLinearLayout.FEED_ITEM);

            ImageView feedLogoImageView = (ImageView) super.findViewById(R.id.feedImage);
            this.imageLoader.displayImage(feedItem.getFeedLogoURL(), feedLogoImageView, this.imageLoaderOptions);

            //SpannableString spannableString = new SpannableString(this.feedItem.getFeedText());
            //spannableString.setSpan(new MyLeadingMarginSpan2(FeedItemActivity.LINES_TO_INDENT, 204), 0, spannableString.length(), 0);

            TextView feedDescriptionTextView = (TextView) super.findViewById(R.id.feedSummary);
            feedDescriptionTextView.setText(this.feedItem.getFeedText()); //can use spannableString

            TextView headerTextView = (TextView) super.findViewById(R.id.feedHeader);
            headerTextView.setText(this.feedItem.getFeedHeader());

            TextView dateTextView = (TextView) super.findViewById(R.id.feedTime);
            dateTextView.setText(this.feedItem.getDateTimeRepresentation());
        }
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