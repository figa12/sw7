package aau.sw7.exhib;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jerian on 03-10-13.
 */
public class ScheduleItem {
    private String eventName;
    private String boothName;
    private Date startDateTime;
    private Date endDateTime;

    public ScheduleItem(String eventName, String boothName, Date startDateTime, Date endDateTime) {
        this.eventName = eventName;
        this.boothName = boothName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public String getEventName() {
        return this.eventName;
    }

    public Date getEndDateTime() {
        return this.endDateTime;
    }

    public Date getStartDateTime() {
        return this.startDateTime;
    }

    public String getBoothName() {
        return this.boothName;
    }

    public String getCountdown() {
        return "in 10 min"; //TODO: Return string saying how much time to event starts
    }

    public String getTimeInterval() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        return simpleDateFormat.format(this.startDateTime) + "-" + simpleDateFormat.format(this.endDateTime);
    }
}
