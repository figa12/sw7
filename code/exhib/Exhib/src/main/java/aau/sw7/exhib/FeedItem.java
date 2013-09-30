package aau.sw7.exhib;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

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
    private String author;
    private Bitmap feedIcon;

    public FeedItem(String feedHeader, String feedText, String author, Date feedDateTime) {
        this.feedHeader = feedHeader;
        this.feedText = feedText;
        this.author = author;
        this.feedDateTime = feedDateTime;
    }

    /** @return The header of the feed item. */
    public String getFeedHeader() {
        return this.feedHeader;
    }

    /** @return The description of the feed item. */
    public String getFeedText() {
        return this.feedText;
    }

    /** @return The date when the feed item was created. */
    public Date getFeedDateTime() {
        return this.feedDateTime;
    }

    /** @return The string representation of the date and time. */
    public String getDateTimeRepresentation() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");

        return "Today at " + simpleDateFormat.format(this.feedDateTime);
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
        out.writeString(this.author);
        out.writeParcelable(this.feedIcon, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
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
        this.feedDateTime = (Date) in.readSerializable(); //TODO test
        this.author = in.readString();
        this.feedIcon = in.readParcelable(Bitmap.class.getClassLoader()); //TODO test
    }
}
