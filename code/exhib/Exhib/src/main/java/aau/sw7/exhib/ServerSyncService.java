package aau.sw7.exhib;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by figa on 9/16/13.
 */
public class ServerSyncService extends AsyncTask<NameValuePair, Integer, JSONObject> {

    private Context context;
    private String serverUrl = "http://figz.dk/api.php";

    public ServerSyncService(Context context) {
        this.context = context;
    }


    @Override
    protected JSONObject doInBackground(NameValuePair... pairs) {

        JSONObject toReturn = null;

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

            // Create a JSON object from the request response
            JSONObject jsonObject = new JSONObject(result);
            //Retrieve the data from the JSON object
            toReturn = jsonObject;



        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toReturn;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        try {

            Integer requestCode = Integer.parseInt(result.getString("RequestCode"));


            if(requestCode == 1) {
                TextView theText = (TextView)(((MainActivity)this.context).findViewById(R.id.thetext));
                theText.setText(result.getString("Text"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}