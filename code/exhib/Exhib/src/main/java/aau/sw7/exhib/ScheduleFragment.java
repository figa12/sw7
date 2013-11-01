package aau.sw7.exhib;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jerian on 03-10-13.
 */
public class ScheduleFragment extends Fragment {

    private ArrayList<ScheduleLinearLayout> scheduleLinearLayouts = new ArrayList<ScheduleLinearLayout>();
    private LinearLayout scheduleContainer;
    private boolean viewDestroyed = true;

    private Handler handler = new Handler(); // Android Runnable Handler
    private Runnable updateCountdownRunnable = new Runnable()
    {
        public void run()
        {
            if(!ScheduleFragment.this.viewDestroyed) {
                for (ScheduleLinearLayout scheduleLinearLayout : ScheduleFragment.this.scheduleLinearLayouts) {
                    scheduleLinearLayout.updateTextViews();
                }
            }
            // Set a delay on the Runnable for when it should be run again
            ScheduleFragment.this.handler.postDelayed(this, 10000);
        }
    };

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        this.scheduleContainer = (LinearLayout) rootView.findViewById(R.id.scheduleContainer);
        this.viewDestroyed = false;

        long ts = (new Date().getTime() / 1000) + 7200; //TODO fix server/client time difference
        new ServerSyncService(super.getActivity()).execute(
                new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_SCHEDULE)),
                new BasicNameValuePair("Type", "GetSchedule"),
                new BasicNameValuePair("UserId", String.valueOf(((TabActivity) this.getActivity()).getUserId())),
                new BasicNameValuePair("TimeStamp", String.valueOf(ts)));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.viewDestroyed = true;
        this.handler.removeCallbacks(this.updateCountdownRunnable);
    }

    public void setSchedule(ArrayList<ScheduleItem> scheduleItems) {
        if(this.viewDestroyed) { return; }

        LayoutInflater inflater = (LayoutInflater) super.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(scheduleItems.size() == 0) {
            return;
        }

        // initial values
        ScheduleItem previousScheduleItem = scheduleItems.get(0);
        ArrayList<ScheduleItem> sameDayScheduleItems = new ArrayList<ScheduleItem>();
        sameDayScheduleItems.add(previousScheduleItem);

        for (int i = 1; i < scheduleItems.size(); i++) {
            if(!this.sameDay(previousScheduleItem.getStartDateTime(), scheduleItems.get(i).getStartDateTime())) {
                this.addDayHeader(inflater, this.dayString(previousScheduleItem));
                this.addDayEvents(sameDayScheduleItems);
                sameDayScheduleItems.clear();
            }

            sameDayScheduleItems.add(scheduleItems.get(i));
            previousScheduleItem = scheduleItems.get(i);
        }

        this.addDayHeader(inflater, this.dayString(previousScheduleItem));
        this.addDayEvents(sameDayScheduleItems);

        // now start updating countdown
        this.handler.postDelayed(this.updateCountdownRunnable, 10000);
    }

    private boolean sameDay(Date date1, Date date2) {
        // if the dates are not before or after each other, then it is the same day
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(date1).equals(simpleDateFormat.format(date2));
    }

    private String dayString(ScheduleItem scheduleItem) {
        Date date = scheduleItem.getStartDateTime();
        if(this.sameDay(date, new Date())) {
            return "Today";
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd. MMM");
            return simpleDateFormat.format(date);
        }
    }

    private void addDayEvents(ArrayList<ScheduleItem> scheduleItems) {
        if(this.viewDestroyed) { return; }

        ScheduleLinearLayout scheduleLinearLayout = new ScheduleLinearLayout(super.getActivity(), scheduleItems);
        this.scheduleLinearLayouts.add(scheduleLinearLayout);
        this.scheduleContainer.addView(scheduleLinearLayout);
    }

    @SuppressWarnings("ConstantConditions")
    private void addDayHeader(LayoutInflater inflater, String header) {
        if(this.viewDestroyed) { return; }

        View scheduleDayItemView = inflater.inflate(R.layout.schedule_day_item, null);

        TextView dayTextView = (TextView) scheduleDayItemView.findViewById(R.id.dayText);
        dayTextView.setText(header);

        this.scheduleContainer.addView(scheduleDayItemView);
    }
}
