package NfcForeground;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;

import org.ndeftools.Message;
import org.ndeftools.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerian on 25-10-13.
 */
public class NfcForeground {

    private Context context;

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    public NfcForeground(Context context) {
        this.context = context;

        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this.context);
        this.nfcPendingIntent = PendingIntent.getActivity(this.context, 0, new Intent(this.context, ((Activity) this.context).getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    protected void onResume() {
        // foreground mode gives the current active application priority for reading scanned tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter[] writeTagFilters = new IntentFilter[] { tagDetected };
        this.nfcAdapter.enableForegroundDispatch((Activity) this.context, this.nfcPendingIntent, writeTagFilters, null);
    }

    protected void onPause() {
        this.nfcAdapter.disableForegroundDispatch((Activity) this.context);
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
            Log.d(this.getClass().getSimpleName(), "Found " + messages.length + " NDEF messages");

            this.vibrate(); // signal found messages

            // parse to records
            for (int i = 0; i < messages.length; i++) {
                try {
                    List<Record> records = new Message((NdefMessage)messages[i]);

                    Log.d(this.getClass().getSimpleName(), "Found " + records.size() + " records in message " + i);

                    for(int k = 0; k < records.size(); k++) {
                        Log.d(this.getClass().getSimpleName(), " Record #" + k + " is of class " + records.get(k).getClass().getSimpleName());

                        Record record = records.get(k);
                        foundRecords.add(record);

                    }
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), "Problem parsing message", e);
                }

            }
        }
        return foundRecords;
    }

    private void vibrate() {
        Log.d(this.getClass().getSimpleName(), "vibrate");

        Vibrator vibe = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(100);
    }
}
