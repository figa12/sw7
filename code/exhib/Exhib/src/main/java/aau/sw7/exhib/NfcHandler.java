package aau.sw7.exhib;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.content.Intent;

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
    private String TAG;
    private Intent intent;
    private Context context;

    public NfcHandler(String TAG, Intent intent, Context context) {
        this.TAG = TAG;
        this.intent = intent;
        this.context = context;
    }

    public List<Record> newIntentEvent() {
        Parcelable[] messages = this.intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        List<Record> foundRecords = new ArrayList<Record>();
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
        vibe.vibrate(500);
    }
}
