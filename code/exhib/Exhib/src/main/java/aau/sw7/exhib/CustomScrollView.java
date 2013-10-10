package aau.sw7.exhib;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import org.apache.http.message.BasicNameValuePair;

import java.util.Date;

/**
 * Created by jerian on 18-09-13.
 * A custom {@link ScrollView} which almost only overrides {@code onScrollChanged} in order to know when the bottom has been reached.
 */
public class CustomScrollView extends ScrollView {

    private MainActivity mainActivity;
    private boolean wait = false;
    private Handler handler = new Handler();

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mainActivity = (MainActivity) context;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            CustomScrollView.this.wait = false;
        }
    };

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = getChildAt(getChildCount() - 1);
        int diff = (view.getBottom() - (getHeight() + getScrollY())); //Calculate the scrolldiff

        // If the difference is 0 then bottom has been reached.
        // The state of the bottom should also be 'MoreItemsAvailable'
        if (diff == 0 && !this.wait && this.mainActivity.getFeedFragment().getBottomItemsState() == FeedFragment.BottomMessageState.MoreItemsAvailable) {
            this.wait = true;

            // Set the bottom state to 'Loading', it will then display a progress circle.
            this.mainActivity.getFeedFragment().setBottomMessageState(FeedFragment.BottomMessageState.Loading);

            // Request more items from the server. The server will change the bottom state accordingly.
            this.requestFeeds();

            // Make a runnable that allows 'bottom has been reached'-code to run again in a moment
            this.handler.postDelayed(this.runnable, 100);
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    /** A request for more feeds. */
    private void requestFeeds() {
        FeedLinearLayout feedLinearLayout = (FeedLinearLayout) this.mainActivity.findViewById(R.id.feed);

        BasicNameValuePair requestCode = new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_MORE_FEEDS_REQUEST));
        BasicNameValuePair getFeeds = new BasicNameValuePair("Type", "GetOldFeeds");
        BasicNameValuePair limit = new BasicNameValuePair("Limit", ServerSyncService.ITEMS_LIMIT);

        // Get the timestamp of the bottom most feed item
        long timestamp;

        if(feedLinearLayout.getSize() > 0) {
            timestamp = feedLinearLayout.get(feedLinearLayout.getSize() - 1).getFeedDateTime().getTime() / 1000; // get the timestamp of the last element in the list
        } else {
            timestamp = (new Date().getTime() / 1000) + 7200;
        }
        BasicNameValuePair timeStamp = new BasicNameValuePair("TimeStamp", String.valueOf(timestamp));
        BasicNameValuePair user = new BasicNameValuePair("UserId", "1");

        new ServerSyncService(this.mainActivity).execute(requestCode, getFeeds, limit, timeStamp, user);
    }
}
