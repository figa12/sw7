package NfcForeground;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;

import org.ndeftools.Record;

import java.util.ArrayList;

/**
 * Created by jerian on 25-10-13.
 */
public abstract class NfcForegroundActivity extends Activity {

    private NfcForeground nfcForeground;
    private boolean used = false;

    protected abstract void onNfcScanned(ArrayList<Record> records);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.used = savedInstanceState.getBoolean("USED");
        }

        this.nfcForeground = new NfcForeground(this);

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
                    this.onNfcScanned(new ArrayList<Record>(this.nfcForeground.parseRecord(message.getRecords())));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.nfcForeground.onDestroy();
        this.nfcForeground = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("USED", true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNewIntent(Intent intent) { // this method is called when an NFC tag is scanned
        super.onNewIntent(intent);

        ArrayList<Record> records = this.nfcForeground.newIntentEvent(intent);
        if(records.size() > 0) {
            this.onNfcScanned(records);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.nfcForeground.onResume();
    }

    @Override
    protected void onPause() {
        super.onResume();
        this.nfcForeground.onPause();
    }
}
