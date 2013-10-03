package aau.sw7.exhib;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jerian on 17-09-13.
 *
 * A class holding all information about a feed item.
 * The feeditem is presented in {@link aau.sw7.exhib.FeedLinearLayout}
 */
public class FeedItem implements Parcelable {

    private String feedHeader;
    private String feedText;
    private Date feedDateTime;
    private String feedLogo;

    public FeedItem(String feedHeader, String feedText, Date feedDateTime, String feedLogo) {
        this.feedHeader = feedHeader;
        this.feedText = feedText;
        this.feedDateTime = feedDateTime;
        this.feedLogo = feedLogo;
    }

    /** @return The header of the feed item. */
    public String getFeedHeader() {
        return this.feedHeader;
    }

    /** @return The description of the feed item. */
    public String getFeedText() {
        return this.feedText;
    }

    public String getFeedLogoURL() {
        return this.feedLogo;
    }

    /** @return The date when the feed item was created. */
    public Date getFeedDateTime() {
        return this.feedDateTime;
    }

    /** @return The string representation of the date and time. */
    public String getDateTimeRepresentation() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

        if(DateUtils.isToday(this.feedDateTime.getTime())) {
            simpleDateFormat.applyPattern("h:mm a");
            return "Today at " + simpleDateFormat.format(this.feedDateTime);
        } else {
            String result;
            simpleDateFormat.applyPattern("d-M-yyyy");
            result = simpleDateFormat.format(this.feedDateTime);
            result += " at ";
            simpleDateFormat.applyPattern("h:mm a");
            result += simpleDateFormat.format(this.feedDateTime);
            return result;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.feedHeader);
        out.writeString(this.feedText);
        out.writeSerializable(this.feedDateTime);
        out.writeString(this.feedLogo);
    }

    public static final Parcelable.Creator<FeedItem> CREATOR = new Parcelable.Creator<FeedItem>() {
        @Override
        public FeedItem createFromParcel(Parcel in) {
            return new FeedItem(in);
        }

        @Override
        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };

    private FeedItem(Parcel in) {
        this.feedHeader = in.readString();
        this.feedText = in.readString();
        this.feedDateTime = (Date) in.readSerializable();
        this.feedLogo = in.readString();
    }
}
