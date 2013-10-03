package aau.sw7.exhib;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by figa on 9/16/13.
 */
public class ServerSyncService extends AsyncTask<NameValuePair, Integer, String> {

    public static final int GET_FEEDS_REQUEST = 1;
    public static final int GET_NEW_FEEDS_REQUEST = 2;
    public static final int CHECK_NEW_FEEDS_REQUEST = 3;
    public static final int GET_MORE_FEEDS_REQUEST = 4;

    public static final String ITEMS_LIMIT = "7";

    private Context context;
    private String serverUrl = "http://figz.dk/api.php";

    public ServerSyncService(Context context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(NameValuePair... pairs) {

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(Arrays.asList(pairs));
        //Create the HTTP request
        HttpParams httpParameters = new BasicHttpParams();

        //Setup timeouts
        HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
        HttpConnectionParams.setSoTimeout(httpParameters, 15000);

        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpPost httppost = new HttpPost(serverUrl);

        String result = null;

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        int startIndex = result.indexOf('[');

        int requestCode = Integer.valueOf(result.substring(0, startIndex));
        result = result.substring(startIndex);

        try {
            readJsonStream(new ByteArrayInputStream(result.getBytes("UTF-8")), requestCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readJsonStream(InputStream stream, int requestCode) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
        MainActivity mainActivity = (MainActivity) this.context;
        FeedLinearLayout feedLinearLayout = ((FeedLinearLayout) mainActivity.findViewById(R.id.feed));

        try {
            switch (requestCode) {
                case ServerSyncService.GET_FEEDS_REQUEST:
                    feedLinearLayout.addFeedItems((ArrayList<FeedItem>) readFeedItemsArray(reader), FeedLinearLayout.AddAt.Bottom);
                    break;
                case ServerSyncService.GET_NEW_FEEDS_REQUEST:
                    feedLinearLayout.addFeedItems((ArrayList<FeedItem>) readFeedItemsArray(reader), FeedLinearLayout.AddAt.Top);
                    mainActivity.getFeedFragment().setTopMessageState(FeedFragment.TopMessageState.Neutral);
                    break;
                case ServerSyncService.CHECK_NEW_FEEDS_REQUEST:
                    int result = this.readNumberOfNewFeeds(reader);

                    if(result != 0) {
                        mainActivity.getFeedFragment().setUpdateButtonText("Click to load " + String.valueOf(result) + " new items");

                        if(mainActivity.getFeedFragment().getTopItemsState() != FeedFragment.TopMessageState.NewItemsAvailable) {
                            mainActivity.getFeedFragment().setTopMessageState(FeedFragment.TopMessageState.NewItemsAvailable);
                        }
                    }
                    break;
                case ServerSyncService.GET_MORE_FEEDS_REQUEST:
                    ArrayList<FeedItem> feedItems = (ArrayList<FeedItem>) readFeedItemsArray(reader);
                    if(feedItems.size() > 0) {
                        feedLinearLayout.addFeedItems(feedItems, FeedLinearLayout.AddAt.Bottom);
                        mainActivity.getFeedFragment().setBottomMessageState(FeedFragment.BottomMessageState.MoreItemsAvailable);
                    } else {
                        mainActivity.getFeedFragment().setBottomMessageState(FeedFragment.BottomMessageState.NoItemsAvailable);
                    }
                    break;
            }
        }
        finally {
            reader.close();
        }
    }

    private int readNumberOfNewFeeds(JsonReader reader) throws IOException {
        int result = 0;

        reader.beginArray();
        reader.beginObject();
        String name = reader.nextName();
        if(name.equals("num")) {
            result = reader.nextInt();
        }
        reader.endObject();
        reader.endArray();

        return result;
    }

    private List readFeedItemsArray(JsonReader reader) throws IOException {
        List feedItems = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            feedItems.add(readFeedItem(reader));
        }
        reader.endArray();
        return feedItems;
    }

    private FeedItem readFeedItem(JsonReader reader) throws IOException {
        String header = "";
        String text = "";
        Date feedTime = null;
        String feedlogo = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                header = reader.nextString();
            } else if (name.equals("description")) {
                text = reader.nextString();
            } else if (name.equals("feedtime")) {
                try {
                    feedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(reader.nextString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (name.equals("logo")) {
                feedlogo = "http://figz.dk/images/" + reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new FeedItem(header, text, feedTime, feedlogo);
    }
}