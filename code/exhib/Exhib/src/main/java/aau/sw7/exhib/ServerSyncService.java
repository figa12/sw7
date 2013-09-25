package aau.sw7.exhib;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
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

    public static final int GET_FEEDS_REQUEST = 10;
    private Context context;
    private String serverUrl = "http://figz.dk/api.php";

    public ServerSyncService(Context context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(NameValuePair... pairs) {

        String toReturn = null;

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(Arrays.asList(pairs));
        //Create the HTTP request
        HttpParams httpParameters = new BasicHttpParams();

        //Setup timeouts
        HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
        HttpConnectionParams.setSoTimeout(httpParameters, 15000);

        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpPost httppost = new HttpPost(serverUrl);

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = null;

            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            toReturn = result;

            /*
            // Create a JSON object from the request response
            JSONObject jsonObject = new JSONObject(result);
            //Retrieve the data from the JSON object
            toReturn = jsonObject;
            */



        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toReturn;
    }

    @Override
    protected void onPostExecute(String result) {
        /*
        try {


            Integer requestCode = Integer.parseInt(result.getString("RequestCode"));


            if(requestCode == 1) {
                TextView theText = (TextView)(((MainActivity)this.context).findViewById(R.id.thetext));
                theText.setText(result.getString("Text"));
            }

            if(requestCode == ServerSyncService.GET_FEEDS_REQUEST) {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

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
        try {
            switch (requestCode) {
                case ServerSyncService.GET_FEEDS_REQUEST:
                    ((FeedLinearLayout) ((MainActivity) this.context).findViewById(R.id.feed)).addFeedItems((ArrayList<FeedItem>) readFeedItemsArray(reader));
            }
        }
        finally {
            reader.close();
        }
    }

    public List readFeedItemsArray(JsonReader reader) throws IOException {
        List feedItems = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            feedItems.add(readFeedItem(reader));
        }
        reader.endArray();
        return feedItems;
    }

    public FeedItem readFeedItem(JsonReader reader) throws IOException {
        String header = "";
        String text = "";
        String author = "BannedNexus";
        Date feedTime = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("header")) {
                header = reader.nextString();
            } else if (name.equals("description")) {
                text = reader.nextString();
            } else if (name.equals("feedtime")) {
                try {
                    feedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(reader.nextString()); // Convert from seconds to millisconds
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new FeedItem(header, text, author, feedTime);
    }
}