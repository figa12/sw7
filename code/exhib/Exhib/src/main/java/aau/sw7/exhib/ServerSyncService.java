package aau.sw7.exhib;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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

import map.Edge;
import map.Graph;
import map.Node;
import map.Square;

/**
 * Created by figa on 9/16/13.
 */
public class ServerSyncService extends AsyncTask<NameValuePair, Integer, String> {

    // numbers doesn't matter, as long as they are unique
    public static final int GET_FEEDS_REQUEST = 1;
    public static final int GET_NEW_FEEDS_REQUEST = 2;
    public static final int CHECK_NEW_FEEDS_REQUEST = 3;
    public static final int GET_MORE_FEEDS_REQUEST = 4;
    public static final int GET_SCHEDULE = 5;
    public static final int GET_EXHIBITION_INFO = 6;
    public static final int GET_CATEGORIES = 7;
    public static final int CREATE_USER = 8;
    public static final int SET_CATEGORIES = 9;
    public static final int GET_FLOORPLAN = 10;

    public static final String ITEMS_LIMIT = "6";

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
        HttpPost httppost = new HttpPost(this.serverUrl);

        String result = "Timeout"; // will be changed by

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
        if (result == null || result.equals("")) {
            Log.e(ServerSyncService.class.getName(), "No connection found-ish.");
            return;
        } else if (result.equals("Could not complete query. Missing type") || result.equals("Missing request code!")) {
            Log.e(ServerSyncService.class.getName(), result);
            return;
        } else if(result.equals("Timeout")) {
            Log.e(ServerSyncService.class.getName(), result);
            return;
        }

        InputStream stream = null;
        try {
            stream = new ByteArrayInputStream(result.getBytes("UTF-8"));
            this.readJsonStream(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void readJsonStream(InputStream stream) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));

        MainActivity mainActivity = null;
        TabActivity tabActivity = null;
        FeedLinearLayout feedLinearLayout = null;
        CategoriesActivity categoriesActivity = null;

        // determine the origin of the context and cast it appropriately
        if (this.context instanceof TabActivity) {
            tabActivity = (TabActivity) this.context;
            feedLinearLayout = ((FeedLinearLayout) ((TabActivity) this.context).findViewById(R.id.feed));
        } else if (this.context instanceof CategoriesActivity) {
            categoriesActivity = (CategoriesActivity) this.context;
        } else if (this.context instanceof MainActivity) {
            mainActivity = (MainActivity) this.context;
        } else {
            return; // abort
        }

        int requestCode = 0;

        reader.beginObject();
        while(reader.hasNext())
        {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("RequestCode")) {
                requestCode = reader.nextInt();
            } else if(name.equals("Data") && requestCode != 0) {
                switch (requestCode) {
                    // Initial call to populate feed list, is only called in the start of the program
                    case ServerSyncService.GET_FEEDS_REQUEST:
                        if (feedLinearLayout == null || tabActivity.getFeedFragment() == null) {
                            break;
                        }

                        tabActivity.getFeedFragment().setBottomMessageState(FeedFragment.BottomMessageState.MoreItemsAvailable);
                        this.addFeedItems(readFeedItemsArray(reader), feedLinearLayout, FeedLinearLayout.AddAt.Bottom);
                        break;

                    case ServerSyncService.GET_NEW_FEEDS_REQUEST:
                        if (feedLinearLayout == null || tabActivity.getFeedFragment() == null) {
                            break;
                        }

                        this.addFeedItems(readFeedItemsArray(reader), feedLinearLayout, FeedLinearLayout.AddAt.Top);
                        tabActivity.getFeedFragment().setTopMessageState(FeedFragment.TopMessageState.Neutral);
                        break;

                    case ServerSyncService.CHECK_NEW_FEEDS_REQUEST:
                        int result = this.readNumberOfNewFeeds(reader);

                        if(tabActivity.getFeedFragment() == null) {
                            break;
                        }
                        if (result != 0) {
                            tabActivity.getFeedFragment().setUpdateButtonText("Click to load " + String.valueOf(result) + " new items");

                            if (tabActivity.getFeedFragment().getTopItemsState() != FeedFragment.TopMessageState.NewItemsAvailable) {
                                tabActivity.getFeedFragment().setTopMessageState(FeedFragment.TopMessageState.NewItemsAvailable);
                            }
                        }
                        break;

                    case ServerSyncService.GET_MORE_FEEDS_REQUEST:
                        if (feedLinearLayout == null || tabActivity.getFeedFragment() == null) {
                            break;
                        }

                        ArrayList<FeedItem> feedItems = readFeedItemsArray(reader);

                        if (feedItems.size() > 0) {
                            // As long we get feeds from the server, we assume there are more
                            feedLinearLayout.addFeedItems(feedItems, FeedLinearLayout.AddAt.Bottom);
                            tabActivity.getFeedFragment().setBottomMessageState(FeedFragment.BottomMessageState.MoreItemsAvailable);
                        } else {
                            // If it returned 0, then there are no more feeds available from the server
                            tabActivity.getFeedFragment().setBottomMessageState(FeedFragment.BottomMessageState.NoItemsAvailable);
                        }
                        break;

                    case ServerSyncService.GET_SCHEDULE:
                        if (tabActivity.getScheduleFragment() == null) {
                            break;
                        }

                        tabActivity.getScheduleFragment().setSchedule(this.readScheduleItemsArray(reader));
                        break;

                    case ServerSyncService.GET_EXHIBITION_INFO:
                        if (tabActivity.getExhibitionInfoFragment() == null) {
                            break;
                        }
                        this.readExhibitionInformation(reader, tabActivity);
                        break;

                    case ServerSyncService.GET_CATEGORIES:
                        categoriesActivity.setCategories(this.readCategories(reader));
                        break;

                    case ServerSyncService.CREATE_USER:
                        this.readUserCreated(reader, mainActivity);
                        break;

                    case ServerSyncService.SET_CATEGORIES:
                        // Notify activity
                        reader.beginObject();
                        reader.endObject();
                        categoriesActivity.onServerBoothResponse();
                        break;
                    case ServerSyncService.GET_FLOORPLAN:
                        this.readFloorPlan(reader, tabActivity);
                        break;
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        this.context = null;
    }

    private void addFeedItems(ArrayList<FeedItem> feedItems, FeedLinearLayout feedLinearLayout, FeedLinearLayout.AddAt addAt) {
        if (feedItems.size() > 0) {
            feedLinearLayout.addFeedItems(feedItems, addAt);

            // We need to save the timestamp of the newest feed
            long timestamp = (feedItems.get(0).getFeedDateTime().getTime() / 1000) + 7200; //TODO fix server/client time difference
            feedLinearLayout.setTimestampForFeedRequest(timestamp);
        } else {
            // If the initial call didn't give any feeds we need to save the current time as the timestamp
            long timestamp = (new Date().getTime() / 1000) + 7200; //TODO fix server/client time difference
            feedLinearLayout.setTimestampForFeedRequest(timestamp);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private int readNumberOfNewFeeds(JsonReader reader) throws IOException {
        int result = 0;

        reader.beginArray();
        reader.beginObject();
        String name = reader.nextName();
        if (name != null && name.equals("num")) {
            result = reader.nextInt();
        } else {
            result = 0;
        }
        reader.endObject();
        reader.endArray();

        return result;
    }

    private void readUserCreated(JsonReader reader, MainActivity mainActivity) throws IOException {
        long userId = 0;
        long exhibId = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("userId")) {
                userId = reader.nextLong();
            } else if (name.equals("exhibId")) {
                exhibId = reader.nextLong();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        mainActivity.onUserCreated(exhibId, userId);
    }

    private void readFloorPlan(JsonReader reader, TabActivity tabActivity) throws IOException {
        ArrayList<Node> nodes = null;
        ArrayList<Edge> edges = null;
        ArrayList<BoothItem> booths = null;

        //reader.beginArray();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("nodes")) {
                nodes = this.readNodes(reader);
            } else if (name.equals("edges")) {
                edges = this.readEdges(reader, nodes);
            } else if (name.equals("booths")) {
                booths = this.readBoothItems(reader, nodes);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        //reader.endArray();

        Graph graph = new Graph(nodes, edges);
        tabActivity.setFloorPlan(graph,booths);
    }

    private ArrayList<Node> readNodes(JsonReader reader)throws IOException{
        ArrayList<Node> nodes = new ArrayList<Node>();

        reader.beginArray();
        while (reader.hasNext()) {
            nodes.add(this.readNode(reader));
        }
        reader.endArray();

        return nodes;
    }

    private Node readNode(JsonReader reader) throws IOException{
        long nodeID = 0;
        double positionX = 0.0;
        double positionY = 0.0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("id")) {
                nodeID = reader.nextLong();
            } else if (name.equals("x")){
                positionX = reader.nextDouble();
            } else if(name.equals("y")){
                positionY = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new Node(new LatLng(positionY, positionX), nodeID);
    }

    private ArrayList<Edge> readEdges(JsonReader reader, ArrayList<Node> nodes) throws IOException{
        ArrayList<Edge> edges = new ArrayList<Edge>();

        reader.beginArray();
        while (reader.hasNext()) {
            edges.add(this.readEdge(reader, nodes));
        }
        reader.endArray();

        return edges;
    }

    private Edge readEdge(JsonReader reader, ArrayList<Node> nodes) throws IOException{
        double weight = 0.0;
        long nodeFromID = 0L;
        long nodeToID = 0L;


        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("weight")) {
                weight = reader.nextDouble();
            } else if (name.equals("vertexA")){
                nodeFromID = reader.nextLong();
            } else if(name.equals("vertexB")){
                nodeToID = reader.nextLong();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        Node from = this.findNode(nodes, nodeFromID);
        Node to = this.findNode(nodes, nodeToID);

        return new Edge(weight, from, to);
    }

    private Node findNode(ArrayList<Node> nodes, long id){
        for(Node n : nodes){
            if(n.getID() == id){
                return n;
            }
        }
        return null;
    }

    private ArrayList<BoothItem> readBoothItems(JsonReader reader, ArrayList<Node> nodes )throws IOException{
        ArrayList<BoothItem> boothItems = new ArrayList<BoothItem>();

        reader.beginArray();
        while (reader.hasNext()) {
            boothItems.add(this.readBoothItem(reader, nodes));
        }
        reader.endArray();

        return boothItems;
    }

    private void readExhibitionInformation(JsonReader reader, TabActivity tabActivity) throws IOException {
        String imageUrl = "";
        String exhibitionName = "";
        String exhibitionDescription = "";
        String address = "";
        int zip = 0;
        String country = "";

        reader.beginArray();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("logo")) {
                imageUrl = "http://figz.dk/images/" + reader.nextString();
            } else if (name.equals("name")) {
                exhibitionName = reader.nextString();
            } else if (name.equals("description")) {
                exhibitionDescription = reader.nextString();
            } else if (name.equals("address")) {
                address = reader.nextString();
            } else if (name.equals("zip")) {
                zip = reader.nextInt();
            } else if (name.equals("country")) {
                country = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.endArray();

        tabActivity.getExhibitionInfoFragment().setExhibitionInfo(imageUrl, exhibitionName, exhibitionDescription, address, zip, country);
    }

    private ArrayList<ScheduleItem> readScheduleItemsArray(JsonReader reader) throws IOException {
        ArrayList<ScheduleItem> scheduleItems = new ArrayList<ScheduleItem>();

        reader.beginArray();
        while (reader.hasNext()) {
            scheduleItems.add(this.readScheduleItem(reader));
        }
        reader.endArray();
        return scheduleItems;
    }

    private ArrayList<Category> readCategories(JsonReader reader) throws IOException {
        ArrayList<Category> categories = new ArrayList<Category>();

        reader.beginArray();
        while (reader.hasNext()) {
            categories.add(this.readCategory(reader));
        }
        reader.endArray();

        return categories;
    }

    private Category readCategory(JsonReader reader) throws IOException {
        int id = 0;
        String categoryName = "";
        ArrayList<BoothItem> boothItems = new ArrayList<BoothItem>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("name")) {
                categoryName = reader.nextString();
            } else if (name.equals("booths")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    boothItems.add(this.readBoothItem(reader));
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        Category category = new Category(id, categoryName);
        category.setBoothItems(boothItems);
        return category;
    }

    private BoothItem readBoothItem(JsonReader reader) throws IOException {
        int id = 0;
        String boothName = "";
        String description = "";
        String companyLogo = "";
        boolean sub = true;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("name")) {
                boothName = reader.nextString();
            } else if (name.equals("description")) {
                description = reader.nextString();
            } else if(name.equals("logo")) {
                companyLogo = reader.nextString();
            } else if(name.equals("sub")) {
                sub = reader.nextBoolean();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new BoothItem(id, boothName, description, companyLogo, sub);
    }

    private BoothItem readBoothItem(JsonReader reader, ArrayList<Node> nodes) throws IOException {
        int id = 0;
        String boothName = "";
        String description = "";
        String companyLogo = "";
        ArrayList<Node> boothsEntryNodes = new ArrayList<Node>();
        double top = 0.0;
        double right = 0.0;
        double left = 0.0;
        double bottom = 0.0;

        boolean sub = true;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("name")) {
                boothName = reader.nextString();
            } else if (name.equals("description")) {
                description = reader.nextString();
            } else if(name.equals("logo")) {
                companyLogo = reader.nextString();
            } else if(name.equals("sub")) {
                sub = reader.nextBoolean();
            } else if(name.equals("nodeIds")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    boothsEntryNodes.add(this.findNode(nodes, reader.nextLong()));
                }
                reader.endArray();
            } else if(name.equals("top")) {
                top = reader.nextDouble();
            } else if(name.equals("right")) {
                right = reader.nextDouble();
            } else if(name.equals("left")) {
                left = reader.nextDouble();
            } else if(name.equals("bottom")) {
                bottom = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new BoothItem(id, boothName, description, companyLogo, sub,
                new Square(top, left, bottom, right),
                boothsEntryNodes);
    }

    private ArrayList<FeedItem> readFeedItemsArray(JsonReader reader) throws IOException {
        ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();

        reader.beginArray();
        while (reader.hasNext()) {
            feedItems.add(this.readFeedItem(reader));
        }
        reader.endArray();
        return feedItems;
    }

    private ScheduleItem readScheduleItem(JsonReader reader) throws IOException {
        String eventName = "";
        String boothName = "";
        Date startTime = null;
        Date endTime = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("eventname")) {
                eventName = reader.nextString();
            } else if (name.equals("boothname")) {
                boothName = reader.nextString();
            } else if (name.equals("starttime")) {
                try {
                    startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(reader.nextString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (name.equals("endtime")) {
                try {
                    endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(reader.nextString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new ScheduleItem(eventName, boothName, startTime, endTime);
    }

    private FeedItem readFeedItem(JsonReader reader) throws IOException {
        String header = "";
        String text = "";
        Date feedTime = null;
        String feedlogo = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name == null) {
                reader.skipValue();
            } else if (name.equals("name")) {
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