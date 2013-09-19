package aau.sw7.exhib;

import android.graphics.drawable.Drawable;

import java.util.Date;

/**
 * Created by Jesper on 17-09-13.
 */
public class FeedItem {

    private String feedHeader;
    private String feedText;
    private Date feedDateTime;
    private String author;
    private Drawable feedIcon;

    public FeedItem(String feedHeader, String feedText, String author) {
        this.feedHeader = feedHeader;
        this.feedText = feedText;
        this.feedDateTime = new Date();
    }

    public String getFeedHeader() {
        return this.feedHeader;
    }

    public String getFeedText() {
        return this.feedText;
    }

    public Date getFeedDateTime() {
        return this.feedDateTime;
    }
}
