package aau.sw7.exhib;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import org.apache.http.message.BasicNameValuePair;

/**
 * Created by jerian on 18-09-13.
 */
public class CustomScrollView extends ScrollView {

    private MainActivity mainActivity;

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mainActivity = (MainActivity) context;
    }

    private boolean wait = false;
    private Handler handler = new Handler();

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
        int diff = (view.getBottom() - (getHeight() + getScrollY()));// Calculate the scrolldiff
        if (diff == 0 && !this.wait && this.mainActivity.getBottomItemsState() == MainActivity.BottomItemsState.MoreItemsAvailable) {  // if diff is zero, then the bottom has been reached
            this.wait = true;

            Log.d(this.getClass().getName(), "ScrollView: Bottom has been reached");

            this.mainActivity.setBottomMessageState(MainActivity.BottomItemsState.Loading);
            this.requestFeeds();

            this.handler.postDelayed(this.runnable, 100);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void requestFeeds() {
        FeedLinearLayout feedLinearLayout = (FeedLinearLayout) this.mainActivity.findViewById(R.id.feed);

        BasicNameValuePair requestCode = new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_MORE_FEEDS_REQUEST));
        BasicNameValuePair getFeeds = new BasicNameValuePair("GetOldFeeds", "1");
        BasicNameValuePair limit = new BasicNameValuePair("Limit", ServerSyncService.ITEMS_LIMIT);

        long timestamp = feedLinearLayout.get(feedLinearLayout.getSize() - 1).getFeedDateTime().getTime() / 1000; // get the timestamp of the last element in the list
        BasicNameValuePair timeStamp = new BasicNameValuePair("TimeStamp", String.valueOf(timestamp));

        new ServerSyncService(this.mainActivity).execute(requestCode, getFeeds, limit, timeStamp);
    }
}
