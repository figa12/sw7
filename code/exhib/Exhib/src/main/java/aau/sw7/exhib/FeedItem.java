package aau.sw7.exhib;

import java.util.Date;

/**
 * Created by Jesper on 17-09-13.
 */
public class FeedItem {

    private String header;
    private String feedText;
    private Date feedDateTime;

    public FeedItem(String header, String feedText) {
        this.header = header;
        this.feedText = feedText;
    }

    public String getHeader() {
        return this.header;
    }

    public String getFeedText() {
        return this.feedText;
    }
}
