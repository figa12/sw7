package aau.sw7.exhib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by jerian on 18-09-13.
 */
public class CustomScrollView extends ScrollView {

    private FeedLinearLayout feedLinearLayout;

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFeedLinearLayout(FeedLinearLayout feedLinearLayout) {
        this.feedLinearLayout = feedLinearLayout;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = getChildAt(getChildCount()-1);
        int diff = (view.getBottom()-(getHeight()+getScrollY()));// Calculate the scrolldiff
        if( diff == 0 ){  // if diff is zero, then the bottom has been reached
            Log.d(this.getClass().getName(), "ScrollView: Bottom has been reached");
            this.feedLinearLayout.addThirty();
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }
}
