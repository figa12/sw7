package aau.sw7.exhib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.TextRecord;

import java.util.ArrayList;

import NfcForeground.NfcForegroundActivity;

/**
 * Created by jerian on 23-09-13.
 */
public class FeedItemActivity extends NfcForegroundActivity {

    private static final int LINES_TO_INDENT = 4;

    private FeedItem feedItem;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();

    @Override
    protected void onNfcScanned(ArrayList<Record> records) {
        long exhibId = 0L;
        long nodeId = 0L;

        for (int i = 0; i < records.size(); i++) {

            if (records.get(i) instanceof AndroidApplicationRecord) {
                AndroidApplicationRecord appRecord = (AndroidApplicationRecord) records.get(i);
            } else if (records.get(i) instanceof TextRecord) {
                TextRecord textRecord = (TextRecord) records.get(i);

                if (i == 0) {
                    exhibId = Long.valueOf(textRecord.getText());
                } else if (i == 1 && records.size() > 2) {
                    nodeId = Long.valueOf(textRecord.getText()); //this can either be a node ID
                }
            }
        }

        if(nodeId != 0L || exhibId != 0L) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(MainActivity.EXHIB_ID, exhibId);
            resultIntent.putExtra(MainActivity.BOOTH_ID, nodeId);

            super.setResult(Activity.RESULT_OK, resultIntent);
            super.finish();
        }
    }

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_feed_item);

        Bundle extras = super.getIntent().getExtras();

        if (extras != null) {
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
                                      int start, int end, boolean first, Layout layout) {
        }

        @Override
        public int getLeadingMarginLineCount() {
            return lines;
        }
    }
}