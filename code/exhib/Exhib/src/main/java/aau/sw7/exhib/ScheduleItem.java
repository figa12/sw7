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
        return "Booth: " + this.boothName;
    }

    public String getCountdown() {
        long currentTime = new Date().getTime() / 1000;
        long eventTime = this.startDateTime.getTime() / 1000;
        long difference = eventTime - currentTime;

        long days =  difference / 86400;
        difference %= 86400;
        long hours = difference / 3600;
        difference %= 3600;
        long minutes = difference / 60;
        difference %= 60;

        long seconds = difference;

        // Has event started
        if(currentTime > eventTime) {
            return "";
        }
        // More than 24 hours
        else if(days > 0) {
            return String.format("in %d days %d hours", days, hours);
        }
        // More than 1 hour
        else if (hours > 0) {
            return String.format("in %d hours %d minutes", hours, minutes);
        }
        // More than 1 minute
        else if (minutes > 0) {
            return String.format("in %d minutes %d seconds", minutes, seconds);
        }
        // Less than a minute
        else {
            return "in" + String.valueOf(seconds);
        }
    }

    public String getTimeInterval() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(this.startDateTime) + "-" + simpleDateFormat.format(this.endDateTime);
    }
}
