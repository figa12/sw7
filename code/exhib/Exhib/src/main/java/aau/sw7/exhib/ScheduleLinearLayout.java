package aau.sw7.exhib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by jerian on 03-10-13.
 */
public class ScheduleLinearLayout extends ListLinearLayout<ScheduleItem> {

    private LinearLayout scheduleContainer;

    public ScheduleLinearLayout(Context context) {
        super(context);

        //this.scheduleContainer = (LinearLayout) ((MainActivity) context).findViewById(R.id.scheduleContainer);
    }

    @Override
    protected View makeView(ScheduleItem scheduleItem) {
        LayoutInflater layoutInflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View scheduleItemView = layoutInflater.inflate(R.layout.schedule_event_item, null);



        return null;
    }
}
