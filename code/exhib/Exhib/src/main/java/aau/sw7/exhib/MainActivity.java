package aau.sw7.exhib;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.ndeftools.Record;

import java.util.ArrayList;

/**
 * Created by Reedtz on 03-10-13.
 */

public class MainActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private NfcHandler nfcHandler;
    private boolean used = false;

    private static String TAG = MainActivity.class.getSimpleName();

    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            this.used = savedInstanceState.getBoolean("USED");
        }

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
        this.nfcHandler = new NfcHandler(this);

        Intent intent = super.getIntent();
        NdefMessage[] messages;

        if (!this.used && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMessages != null) {
                messages = new NdefMessage[rawMessages.length];

                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }

                // Each message contains several records
                for (NdefMessage message : messages) {
                    this.nfcHandler.readRecords(new ArrayList<Record>(this.nfcHandler.parseRecord(message.getRecords())));
                }
            }
        }
    }

    //temp button click to open the tab acitivity
    public void onClickOpenCategory (View v) {
        super.startActivity(new Intent(this, CategoriesActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.nfcHandler.enableForegroundMode();
    }

    @Override
    protected void onPause() {
        super.onResume();

        this.nfcHandler.disableForegroundMode();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("USED", true);
        super.onSaveInstanceState(outState);
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

    @Override
    public void onNewIntent(Intent intent) { // this method is called when an NFC tag is scanned
        ArrayList<Record> records = this.nfcHandler.newIntentEvent(intent);

        this.nfcHandler.readRecords(records);
    }
}
