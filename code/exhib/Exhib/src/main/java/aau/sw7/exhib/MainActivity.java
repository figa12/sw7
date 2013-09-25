package aau.sw7.exhib;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.TextRecord;

import java.util.List;

public class MainActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

    private static String TAG = MainActivity.class.getSimpleName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    private FeedLinearLayout feedLinearLayout;
    private FrameLayout updateButtonContainer;
    private Button updateButton;
    private ProgressBar topProgressCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize NFC
        /*
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        */

        BasicNameValuePair requestCode = new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_FEEDS_REQUEST));
        BasicNameValuePair getFeeds = new BasicNameValuePair("GetFeeds", "1");
        BasicNameValuePair limit = new BasicNameValuePair("Limit", "1");
        BasicNameValuePair timeStamp = new BasicNameValuePair("TimeStamp", "1");
        new ServerSyncService(this).execute(requestCode, getFeeds, limit, timeStamp);

        this.feedLinearLayout = (FeedLinearLayout) super.findViewById(R.id.feed);
        this.topProgressCircle = (ProgressBar) super.findViewById(R.id.topProgressCircle);

        this.updateButton = new Button(this);
        this.updateButton.setText("Click to load new items");

        this.updateButtonContainer = (FrameLayout) super.findViewById(R.id.updateButtonContainer);
        this.updateButtonContainer.addView(this.updateButton);

        this.updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //MainActivity.this.feedLinearLayout.addViewAtTop(new FeedItem("New item", "Tekst...", "BannedNexus"));
                MainActivity.this.updateButtonContainer.removeView(MainActivity.this.updateButton);
                MainActivity.this.topProgressCircle.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //enableForegroundMode();
    }

    @Override
    protected void onPause() {
        super.onResume();

        //disableForegroundMode();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        Log.d(TAG, "createNdefMessage");

        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        Log.d(TAG, "onNdefPushComplete");


        throw new IllegalArgumentException("Not implemented");
    }

    public void enableForegroundMode() {
        Log.d(TAG, "enableForegroundMode");

        // foreground mode gives the current active application priority for reading scanned tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) { // this method is called when an NFC tag is scanned
        NfcHandler handler = new NfcHandler(this.TAG, intent, this);
        List<Record> records = handler.newIntentEvent();

        if (records != null) {
            for(Record record : records) {

                if(record instanceof AndroidApplicationRecord) {
                    AndroidApplicationRecord aar = (AndroidApplicationRecord)record;
                    Log.d(TAG, "Package is " + aar.getPackageName());
                }

                if(record instanceof TextRecord) {
                    TextRecord hest = (TextRecord)record;
                    Log.d(TAG, "Teksten er " + hest.getText());
                    TextView textView = (TextView) findViewById(R.id.thetext);
                    textView.append(hest.getText() + "\n");
                }
            }
        }
    }
    
}
