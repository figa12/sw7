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
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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

    public enum TopItemsState {
        Loading, NewItemsAvailable, Neutral
    }

    public enum BottomItemsState {
        Loading, NoItemsAvailable, MoreItemsAvailable
    }

    private static String TAG = MainActivity.class.getSimpleName();

    private Handler handler = new Handler();
    private TopItemsState topItemsState = TopItemsState.Neutral;
    private BottomItemsState bottomItemsState = BottomItemsState.MoreItemsAvailable;

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    private FeedLinearLayout feedLinearLayout;

    /* Top information */
    private FrameLayout updateButtonFrameLayout;
    private Button updateButton;
    private ProgressBar topProgressCircle;

    /* Bottom information */
    private FrameLayout bottomMessageFrameLayout;
    private ProgressBar bottomProgressCircle;
    private TextView bottomMessageTextView;

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
        BasicNameValuePair limit = new BasicNameValuePair("Limit", ServerSyncService.ITEMS_LIMIT);
        new ServerSyncService(this).execute(requestCode, getFeeds, limit);

        this.feedLinearLayout = (FeedLinearLayout) super.findViewById(R.id.feed);

        /* Set up views for information at the top */
        this.updateButtonFrameLayout = (FrameLayout) super.findViewById(R.id.updateButtonContainer);

        this.topProgressCircle = new ProgressBar(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        this.topProgressCircle.setLayoutParams(params);

        this.updateButton = new Button(this);
        this.updateButton.setText("Click to load new items");

        this.updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BasicNameValuePair requestCode = new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_NEW_FEEDS_REQUEST));
                BasicNameValuePair getFeeds = new BasicNameValuePair("GetNewFeeds", "1");

                long ts = (MainActivity.this.feedLinearLayout.get(0).getFeedDateTime().getTime() / 1000) + 7200; //TODO fix server/client time difference
                BasicNameValuePair timeStamp = new BasicNameValuePair("TimeStamp", String.valueOf(ts));
                new ServerSyncService(MainActivity.this).execute(requestCode, getFeeds, timeStamp);

                MainActivity.this.setTopMessageState(TopItemsState.Loading);
            }
        });

        final Runnable checkForFeedsRunnable = new Runnable()
        {
            public void run()
            {
                BasicNameValuePair requestCode = new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.CHECK_NEW_FEEDS_REQUEST));
                BasicNameValuePair getFeeds = new BasicNameValuePair("CheckFeeds", "1");

                long ts = (MainActivity.this.feedLinearLayout.get(0).getFeedDateTime().getTime() / 1000) + 7200; //TODO fix server/client time difference
                BasicNameValuePair timeStamp = new BasicNameValuePair("TimeStamp", String.valueOf(ts));
                new ServerSyncService(MainActivity.this).execute(requestCode, getFeeds, timeStamp);

                MainActivity.this.handler.postDelayed(this, 5000);
            }
        };

        this.handler.postDelayed(checkForFeedsRunnable, 8000);

        /* Set up views for information at the bottom */
        this.bottomMessageFrameLayout = (FrameLayout) super.findViewById(R.id.bottomMessageContainer);

        this.bottomProgressCircle = new ProgressBar(this);
        this.bottomProgressCircle.setLayoutParams(params); // 'params' is intialised above

        this.bottomMessageTextView = new TextView(this);
        this.bottomMessageTextView.setLayoutParams(params);
        this.bottomMessageTextView.setText("No more news"); //TODO use strings from string.xml file

        /* Make the TextView the same height as the ProgressBar */
        this.bottomMessageTextView.measure(0, 0);
        this.bottomProgressCircle.measure(0, 0);
        int padding = (this.bottomProgressCircle.getMeasuredHeight() - this.bottomMessageTextView.getMeasuredHeight()) / 2;
        this.bottomMessageTextView.setPadding(0, padding, 0, padding);
    }

    public void setUpdateButtonText(String text) {
        this.updateButton.setText(text);
    }

    public TopItemsState getTopItemsState() {
        return this.topItemsState;
    }

    public BottomItemsState getBottomItemsState() {
        return this.bottomItemsState;
    }

    public void setTopMessageState(TopItemsState topItemsState) {
        this.topItemsState = topItemsState;
        this.updateButtonFrameLayout.removeAllViews();

        switch (topItemsState) {
            case Loading:
                this.updateButtonFrameLayout.addView(this.topProgressCircle);
                break;

            case NewItemsAvailable:
                this.updateButtonFrameLayout.addView(this.updateButton);
                break;

            case Neutral:
                // do nothing
                break;
        }
    }

    public void setBottomMessageState(BottomItemsState bottomItemsState) {
        this.bottomItemsState = bottomItemsState;
        this.bottomMessageFrameLayout.removeAllViews();

        switch (bottomItemsState) {
            case Loading:
                this.bottomMessageFrameLayout.addView(this.bottomProgressCircle);
                break;

            case NoItemsAvailable:
                this.bottomMessageFrameLayout.addView(this.bottomMessageTextView);
                break;

            case MoreItemsAvailable:
                // do nothing
                break;
        }
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
