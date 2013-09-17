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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by figa on 9/16/13.
 */
public class ServerSyncService extends AsyncTask<URL, Integer, String> {

    private Context context;
    private String result;

    public ServerSyncService(Context context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(URL... params) {

        // REST test
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("TextToSearch", "Hello World"));

        //Create the HTTP request
        HttpParams httpParameters = new BasicHttpParams();

        //Setup timeouts
        HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
        HttpConnectionParams.setSoTimeout(httpParameters, 15000);

        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpPost httppost = new HttpPost("http://figz.dk/login.php");

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpResponse response = null;

        try {
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            // Create a JSON object from the request response
            JSONObject jsonObject = new JSONObject(result);

            //Retrieve the data from the JSON object
            String theText = jsonObject.getString("Text");
            return theText;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        TextView textView = (TextView) ((MainActivity)this.context).findViewById(R.id.thetext);
        textView.append(result + "\n");
    }
}