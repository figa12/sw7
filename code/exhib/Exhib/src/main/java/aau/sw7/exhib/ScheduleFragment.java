package aau.sw7.exhib;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jerian on 03-10-13.
 */
public class ScheduleFragment extends Fragment {

    private ArrayList<ScheduleLinearLayout> scheduleLinearLayouts = new ArrayList<ScheduleLinearLayout>();
    private LinearLayout scheduleContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        this.scheduleContainer = (LinearLayout) rootView.findViewById(R.id.scheduleContainer);

        BasicNameValuePair requestCode = new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_SCHEDULE));
        BasicNameValuePair getFeeds = new BasicNameValuePair("GetSchedule", "1");
        long ts = (new Date().getTime() / 1000) + 7200; //TODO fix server/client time difference
        BasicNameValuePair timeStamp = new BasicNameValuePair("TimeStamp", String.valueOf(ts));
        new ServerSyncService(super.getActivity()).execute(requestCode, getFeeds, timeStamp);

        return rootView;
    }

    public void setSchedule(ArrayList<ScheduleItem> scheduleItems) {
        LayoutInflater inflater = (LayoutInflater) super.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.addDayHeader(inflater, "Today");
        this.addDayEvents(scheduleItems);
    }

    private void addDayEvents(ArrayList<ScheduleItem> scheduleItems) {
        ScheduleLinearLayout scheduleLinearLayout = new ScheduleLinearLayout(super.getActivity(), scheduleItems);
        this.scheduleLinearLayouts.add(scheduleLinearLayout);
        this.scheduleContainer.addView(scheduleLinearLayout);
    }

    private void addDayHeader(LayoutInflater inflater, String header) {
        View scheduleDayItemView = inflater.inflate(R.layout.schedule_day_item, null);

        TextView dayTextView = (TextView) scheduleDayItemView.findViewById(R.id.dayText);
        dayTextView.setText(header);

        this.scheduleContainer.addView(scheduleDayItemView);
    }
}
