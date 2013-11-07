package aau.sw7.exhib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jerian on 03-10-13.
 */
public class ScheduleLinearLayout extends ListLinearLayout<ScheduleItem> {

    private LinearLayout scheduleContainer;
    private ArrayList<TextView> countdownTextView = new ArrayList<TextView>();

    public ScheduleLinearLayout(Context context, ArrayList<ScheduleItem> scheduleItems) {
        super(context);

        super.setOrientation(LinearLayout.VERTICAL);

        for (ScheduleItem scheduleItem : scheduleItems) {
            super.addViewAtBottom(scheduleItem);
        }
    }

    public void updateTextViews() {
        for (TextView textView : this.countdownTextView) {
            ScheduleItem scheduleItem = (ScheduleItem) textView.getTag();
            textView.setText(scheduleItem.getCountdown());
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected View makeView(ScheduleItem scheduleItem) {
        LayoutInflater layoutInflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View scheduleItemView = layoutInflater.inflate(R.layout.schedule_event_item, null);

        TextView timeTextView = (TextView) scheduleItemView.findViewById(R.id.timeText);
        timeTextView.setText(scheduleItem.getTimeInterval());

        TextView eventTextView = (TextView) scheduleItemView.findViewById(R.id.eventName);
        eventTextView.setText(scheduleItem.getEventName());

        TextView locationTextView = (TextView) scheduleItemView.findViewById(R.id.locationText);
        locationTextView.setText(scheduleItem.getBoothName());

        TextView countdownTextView = (TextView) scheduleItemView.findViewById(R.id.countdownText);
        countdownTextView.setText(scheduleItem.getCountdown());
        countdownTextView.setTag(scheduleItem);

        this.countdownTextView.add(countdownTextView);

        return scheduleItemView;
    }
}
