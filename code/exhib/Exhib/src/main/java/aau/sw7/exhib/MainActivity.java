package aau.sw7.exhib;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.TextRecord;

import java.util.ArrayList;

/**
 * Created by Reedtz on 03-10-13.
 */

public class MainActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {


    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    private static String TAG = MainActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);

        final ActionBar actionBar = getActionBar();
        /* Remove title bar etc. Doesn't work when applied to the style directly via the xml */
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        // initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    //temp button click to open the tab acitivity
    public void onClicktemp (View v){

        super.startActivity(new Intent(this, CategoriesActivity.class));

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = super.getIntent();
        NdefMessage[] messages;


        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }

                // Each message contains several records
                for (NdefMessage message : messages) {
                    this.readRecords(new ArrayList<Record>(parseRecord(message.getRecords())));
                }
            }

        }

        enableForegroundMode();
    }

    @Override
    protected void onPause() {
        super.onResume();

        disableForegroundMode();
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
        IntentFilter[] writeTagFilters = new IntentFilter[]{tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
    }

    public void readRecords(ArrayList<Record> records){

        AndroidApplicationRecord appRecord;
        TextRecord textRecord;

        if (records != null) {
            for (int i = 0; i < records.size(); i++) {

                if (records.get(i) instanceof AndroidApplicationRecord) {
                    appRecord = (AndroidApplicationRecord) records.get(i);
                    Log.d(TAG, "Package is " + appRecord.getPackageName());

                }

                else if (records.get(i) instanceof TextRecord) {
                    textRecord = (TextRecord) records.get(i);
                    Log.d(TAG, "Text is " + textRecord.getText());

                    ArrayList<String> exhibString = new ArrayList<String>();
                    exhibString.add(textRecord.getText());

                    Integer exhibID = Integer.parseInt(exhibString.get(0));
                 //   Integer boothID = Integer.parseInt(exhibString.get(1));

                    Intent startCategory = new Intent(this, CategoriesActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putInt("exhibID", exhibID);
                //    bundle.putInt("boothID", boothID);

                    startCategory.putExtras(bundle);

                    startActivity(startCategory);

                }
            }
        }
    }


    // Transform an array of NdefRecords to an ArrayList of records
    public ArrayList<Record> parseRecord(NdefRecord[] ndefRecords){

        ArrayList<Record> records = new ArrayList<Record>();

        for(NdefRecord ndefRecord : ndefRecords){

            try {
                records.add(Record.parse(ndefRecord));
            } catch (FormatException e) {
                e.printStackTrace();
            }
        }

        return records;

    }

    @Override
    public void onNewIntent(Intent intent) { // this method is called when an NFC tag is scanned
        NfcHandler handler = new NfcHandler(this.TAG, intent, this);
        ArrayList<Record> records = (ArrayList<Record>) handler.newIntentEvent();

        this.readRecords(records);

    }
}
