package aau.sw7.exhib;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

/**
 * Created by figa on 10/2/13.
 */
public class FeedFragment extends Fragment {

    public enum TopMessageState {
        Loading, NewItemsAvailable, Neutral
    }

    public enum BottomMessageState {
        Loading, NoItemsAvailable, MoreItemsAvailable
    }

    private boolean viewDestroyed = true;

    private Handler handler = new Handler(); // Android Runnable Handler
    private Runnable checkForFeedsRunnable = new Runnable()
    {
        public void run()
        {
            if(!FeedFragment.this.viewDestroyed) {
                new ServerSyncService(FeedFragment.this.getActivity()).execute(
                        new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.CHECK_NEW_FEEDS_REQUEST)),
                        new BasicNameValuePair("Type", "CheckFeeds"),
                        new BasicNameValuePair("UserId", String.valueOf(((TabActivity) FeedFragment.this.getActivity()).getUserId())),
                        new BasicNameValuePair("TimeStamp", String.valueOf(FeedFragment.this.feedLinearLayout.getTimestampForFeedRequest())));
            }
            // Set a delay on the Runnable for when it should be run again
            FeedFragment.this.handler.postDelayed(this, 5000);
        }
    };
    private TopMessageState topItemsState;
    private BottomMessageState bottomItemsState;

    private FeedLinearLayout feedLinearLayout;

    /* Top information */
    private FrameLayout topMessageFrameLayout;
    private Button topMessageUpdateButton;
    private ProgressBar topMessageProgressCircle;

    /* Bottom information */
    private FrameLayout bottomMessageFrameLayout;
    private ProgressBar bottomMessageProgressCircle;
    private TextView bottomMessageTextView;

    public ProgressDialog progressDialog;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        this.viewDestroyed = false;

        this.topItemsState = TopMessageState.Neutral;
        this.bottomItemsState = BottomMessageState.MoreItemsAvailable;

        this.feedLinearLayout = (FeedLinearLayout) rootView.findViewById(R.id.feed); // save the reference to the feed linear layout.

        // Set up views for information at the top
        this.topMessageFrameLayout = (FrameLayout) rootView.findViewById(R.id.updateButtonContainer);

        this.topMessageProgressCircle = new ProgressBar(super.getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        this.topMessageProgressCircle.setLayoutParams(params);

        this.topMessageUpdateButton = new Button(super.getActivity());
        this.topMessageUpdateButton.setText("Click to load new items");

        this.topMessageUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When the button is clicked, it request the new feeds from the server
                new ServerSyncService(FeedFragment.this.getActivity()).execute(
                        new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_NEW_FEEDS_REQUEST)),
                        new BasicNameValuePair("Type", "GetNewFeeds"),
                        new BasicNameValuePair("UserId", String.valueOf(((TabActivity) FeedFragment.this.getActivity()).getUserId())),
                        new BasicNameValuePair("TimeStamp", String.valueOf(FeedFragment.this.feedLinearLayout.getTimestampForFeedRequest())));

                FeedFragment.this.setTopMessageState(TopMessageState.Loading);
            }
        });

        // Post a Runnable (thread) that checks the server for new items
        this.handler.postDelayed(this.checkForFeedsRunnable, 8000); // The first check after 8000 ms

        // Set up views for information at the bottom
        this.bottomMessageFrameLayout = (FrameLayout) rootView.findViewById(R.id.bottomMessageContainer);

        this.bottomMessageProgressCircle = new ProgressBar(super.getActivity());
        this.bottomMessageProgressCircle.setLayoutParams(params); // 'params' is intialised above

        this.bottomMessageTextView = new TextView(super.getActivity());
        this.bottomMessageTextView.setLayoutParams(params);
        this.bottomMessageTextView.setText("No more news");

        // Make the TextView the same height as the ProgressBar
        this.bottomMessageTextView.measure(0, 0);
        this.bottomMessageProgressCircle.measure(0, 0);
        int padding = (this.bottomMessageProgressCircle.getMeasuredHeight() - this.bottomMessageTextView.getMeasuredHeight()) / 2;
        this.bottomMessageTextView.setPadding(0, padding, 0, padding);

        this.setBottomMessageState(BottomMessageState.Loading);

        /* Request feed items from the server */
        new ServerSyncService(super.getActivity()).execute(
                new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_FEEDS_REQUEST)),
                new BasicNameValuePair("Type", "GetFeeds"),
                new BasicNameValuePair("UserId", String.valueOf(((TabActivity) this.getActivity()).getUserId())),
                new BasicNameValuePair("Limit", ServerSyncService.ITEMS_LIMIT));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.viewDestroyed = true;
        this.handler.removeCallbacks(this.checkForFeedsRunnable);
    }

    /**
     * Set the text of top message button.
     * @param text The text on the button.
     */
    public void setUpdateButtonText(String text) {
        this.topMessageUpdateButton.setText(text);
    }

    /**
     * @return The current state of the top information field.
     * @see aau.sw7.exhib.FeedFragment.TopMessageState
     */
    public TopMessageState getTopItemsState() {
        return this.topItemsState;
    }

    /**
     * @return The current state of the bottom information field.
     * @see aau.sw7.exhib.FeedFragment.BottomMessageState
     */
    public BottomMessageState getBottomItemsState() {
        return this.bottomItemsState;
    }

    /**
     * Set the state of the top information field.
     * @param topItemsState The new state.
     * @see aau.sw7.exhib.FeedFragment.TopMessageState
     */
    public void setTopMessageState(TopMessageState topItemsState) {
        if(this.viewDestroyed) { return; }

        this.topItemsState = topItemsState;
        this.topMessageFrameLayout.removeAllViews();

        switch (topItemsState) {
            case Loading:
                this.topMessageFrameLayout.addView(this.topMessageProgressCircle);
                break;

            case NewItemsAvailable:
                this.topMessageFrameLayout.addView(this.topMessageUpdateButton);
                break;

            case Neutral:
                // do nothing
                break;
        }
    }

    /**
     * Set the state of the bottom information field.
     * @param bottomItemsState The new state.
     * @see aau.sw7.exhib.FeedFragment.BottomMessageState
     */
    public void setBottomMessageState(BottomMessageState bottomItemsState) {
        if(this.viewDestroyed) { return; }

        this.bottomItemsState = bottomItemsState;
        this.bottomMessageFrameLayout.removeAllViews();

        switch (bottomItemsState) {
            case Loading:
                this.bottomMessageFrameLayout.addView(this.bottomMessageProgressCircle);
                break;

            case NoItemsAvailable:
                this.bottomMessageFrameLayout.addView(this.bottomMessageTextView);
                break;

            case MoreItemsAvailable:
                // do nothing
                break;
        }
    }
}


