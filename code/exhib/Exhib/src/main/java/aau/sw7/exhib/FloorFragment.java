package aau.sw7.exhib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.*;

/**
 * Created by figa on 10/3/13.
 */
public class FloorFragment extends SupportMapFragment {
    private TabActivity parent;

    public static SupportMapFragment create()
    {
        SupportMapFragment fragment = newInstance();

        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then disable lock
            if (!isVisibleToUser) {
                this.parent.getViewPager().setPagingEnabled(true);
                this.parent.setLock(false);
            }
        }
    }

}
