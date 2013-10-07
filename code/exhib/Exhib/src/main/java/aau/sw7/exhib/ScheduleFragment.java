package aau.sw7.exhib;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jerian on 03-10-13.
 */
public class ScheduleFragment extends Fragment {

    private ArrayList<ScheduleLinearLayout> scheduleLinearLayouts = new ArrayList<ScheduleLinearLayout>();
    private LinearLayout scheduleContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.schedule_fragment, container, false);

        this.scheduleContainer = (LinearLayout) rootView.findViewById(R.id.scheduleContainer);

        // Server reqeust for schedule

        this.addDayHeader(inflater, "Today");
        ScheduleLinearLayout day = new ScheduleLinearLayout(super.getActivity());
        this.scheduleLinearLayouts.add(day);

        return rootView;
    }

    private void addDayHeader(LayoutInflater inflater, String header) {
        View scheduleDayItemView = inflater.inflate(R.layout.schedule_day_item, null);

        TextView dayTextView = (TextView) scheduleDayItemView.findViewById(R.id.dayText);
        dayTextView.setText(header);

        this.scheduleContainer.addView(scheduleDayItemView);
    }
}
