package aau.sw7.exhib;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.TextRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by figa on 13/09/13.
 */
public class NfcHandler {

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    private String TAG;
    private Context context;

    public NfcHandler(Context context) {
        this.context = context;
        this.TAG = this.getClass().getSimpleName();

        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this.context);
        this.nfcPendingIntent = PendingIntent.getActivity(this.context, 0, new Intent(this.context, ((Activity) this.context).getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public void enableForegroundMode() {
        Log.d(TAG, "enableForegroundMode");

        // foreground mode gives the current active application priority for reading scanned tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter[] writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcAdapter.enableForegroundDispatch((MainActivity) this.context, this.nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch((Activity) this.context);
    }

    public void readRecords(ArrayList<Record> records) {
        if (records != null) {

            Bundle bundle = new Bundle();

            for (int i = 0; i < records.size(); i++) {

                if (records.get(i) instanceof AndroidApplicationRecord) {
                    AndroidApplicationRecord appRecord = (AndroidApplicationRecord) records.get(i);
                } else if (records.get(i) instanceof TextRecord) {
                    TextRecord textRecord = (TextRecord) records.get(i);

                    if (i == 0) {
                        bundle.putInt("exhibID", Integer.parseInt(textRecord.getText()));
                    } else if (i == 1 && records.size() > 2) {
                        bundle.putInt("boothID", Integer.parseInt(textRecord.getText()));
                    }
                }
            }

            Intent categoryIntent = new Intent(this.context, CategoriesActivity.class);
            categoryIntent.putExtras(bundle);

            this.context.startActivity(categoryIntent);
        }
    }

    // Transform an array of NdefRecords to an ArrayList of records
    public ArrayList<Record> parseRecord(NdefRecord[] ndefRecords) {

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

    public ArrayList<Record> newIntentEvent(Intent intent) {
        Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        ArrayList<Record> foundRecords = new ArrayList<Record>();

        if (messages != null) {
            Log.d(TAG, "Found " + messages.length + " NDEF messages");

            vibrate(); // signal found messages

            // parse to records
            for (int i = 0; i < messages.length; i++) {
                try {
                    List<Record> records = new Message((NdefMessage)messages[i]);

                    Log.d(TAG, "Found " + records.size() + " records in message " + i);

                    for(int k = 0; k < records.size(); k++) {
                        Log.d(TAG, " Record #" + k + " is of class " + records.get(k).getClass().getSimpleName());

                        Record record = records.get(k);
                        foundRecords.add(record);

                    }
                } catch (Exception e) {
                    Log.e(TAG, "Problem parsing message", e);
                }

            }
        }
        return foundRecords;
    }

    private void vibrate() {
        Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(100);
    }
}
