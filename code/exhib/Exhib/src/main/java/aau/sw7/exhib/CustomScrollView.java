package aau.sw7.exhib;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by jerian on 18-09-13.
 */
public class CustomScrollView extends ScrollView {

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        if (diff == 0 && !this.wait) {  // if diff is zero, then the bottom has been reached
            this.wait = true;

            Log.d(this.getClass().getName(), "ScrollView: Bottom has been reached");

            MainActivity mainActivity = (MainActivity) super.getContext();

            //((FeedLinearLayout) (mainActivity.findViewById(R.id.feed))).addThirty();
            ((ProgressBar) mainActivity.findViewById(R.id.bottomProgressCircle)).setVisibility(View.VISIBLE);
            ((TextView) mainActivity.findViewById(R.id.scrollMessage)).setVisibility(View.INVISIBLE);

            this.handler.postDelayed(this.runnable, 100); //TODO test kode
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
