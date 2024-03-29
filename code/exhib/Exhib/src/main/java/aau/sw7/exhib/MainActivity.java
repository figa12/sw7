package aau.sw7.exhib;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.message.BasicNameValuePair;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.wellknown.TextRecord;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import NfcForeground.NfcForegroundActivity;

/**
 * Created by Reedtz on 03-10-13.
 */

public class MainActivity extends NfcForegroundActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FILE_PATH = "exhibUserIds.txt";

    public static final String BOOTH_ID = "boothId";
    public static final String EXHIB_ID = "exhibId";
    public static final String USER_ID = "userId";

    private boolean waitingServerResponse = false;
    private long currentBoothId = 0L; // Called current because it consists of the last scanned booth id

    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(config);

        final ActionBar actionBar = getActionBar();
        /* Remove title bar etc. Doesn't work when applied to the style directly via the xml */
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);

        if(!super.isStartedFromTag()) {
            this.startRecent();
        }
    }

    private void startRecent() {
        String fileContents = this.readIdFile();

        if(fileContents == null || fileContents.equals(""))
            return;

        String[] idPairs = fileContents.split("\n");
        String[] ids = idPairs[idPairs.length-1].split(",");
        long exhibId = Long.valueOf(ids[0]);
        long userId = Long.valueOf(ids[1]);

        Intent intent = new Intent(this, TabActivity.class);
        intent.putExtra(MainActivity.EXHIB_ID, exhibId);
        intent.putExtra(MainActivity.USER_ID, userId);
        this.startActivity(intent);
    }

    @Override
    protected void onNfcScanned(ArrayList<Record> records) {
        if (this.waitingServerResponse) {
            //TODO give a 'warning' saying that we are waiting for response from the server. "Restart app if this persists"
            // maybe even remove nfc image and show progress circle
            return;
        }

        // ID 0 is not a valid ID
        long exhibId = 0L;
        this.currentBoothId = 0L;

        for (int i = 0; i < records.size(); i++) {

            if (records.get(i) instanceof AndroidApplicationRecord) {
                AndroidApplicationRecord appRecord = (AndroidApplicationRecord) records.get(i);
            } else if (records.get(i) instanceof TextRecord) {
                TextRecord textRecord = (TextRecord) records.get(i);

                if (i == 0) {
                    exhibId = Long.valueOf(textRecord.getText());
                } else if (i == 1 && records.size() > 2) {
                    this.currentBoothId = Long.valueOf(textRecord.getText());
                }
            }
        }

        String fileContents = this.readIdFile();
        Long userId = this.findUserId(fileContents, exhibId);

        if(exhibId == 0L) {
            // The tag does not contain valid data
            return;
        } else if(userId != null) {
            fileContents = this.changeRecent(fileContents, userId);
            this.writeIdFile(fileContents);

            Bundle bundle = new Bundle();
            bundle.putLong(MainActivity.EXHIB_ID, exhibId);
            bundle.putLong(MainActivity.BOOTH_ID, this.currentBoothId);
            bundle.putLong(MainActivity.USER_ID, userId);

            this.currentBoothId = 0L;

            Intent intent = new Intent(this, TabActivity.class);
            intent.putExtras(bundle);
            this.startActivity(intent);
        } else {
            this.requestCreateUser(exhibId);
        }
    }

    private void requestCreateUser(long exhibId) {
        this.waitingServerResponse = true;

        new ServerSyncService(this).execute(
                new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.CREATE_USER)),
                new BasicNameValuePair("Type", "CreateUser"),
                new BasicNameValuePair("ExhibId", String.valueOf(exhibId)));
        // server will respond in onUserCreated
    }

    private String changeRecent(String fileContents, long mostRecentUserId) {
        ArrayList<String> idPairs = new ArrayList(Arrays.asList(fileContents.split("\n")));

        if(!fileContents.contains(",")) {
            return fileContents;
        }

        boolean changed = false;

        for (int i = 0; i < idPairs.size(); i++) {
            String[] ids = idPairs.get(i).split(",");
            if (Long.valueOf(ids[1]) == mostRecentUserId) {
                String mostRecent =idPairs.remove(i);
                idPairs.add(mostRecent);
                changed = true;
                break;
            }
        }

        if(changed) {
            fileContents = "";
            for (String pair : idPairs) {
                fileContents += pair + "\n";
            }
        }

        return fileContents;
    }

    public void onUserCreated(long exhibId, long userId) {
        // Save the userId in the file
        String registration = exhibId + "," + userId + "\n";
        String fileContents = this.readIdFile();
        fileContents = this.changeRecent(fileContents, userId);
        this.writeIdFile(fileContents + registration);

        // Ready to scan tags again
        this.waitingServerResponse = false;

        // and then open categories activity
        Bundle bundle = new Bundle();
        bundle.putLong(MainActivity.EXHIB_ID, exhibId);
        bundle.putLong(MainActivity.USER_ID, userId);

        Intent categoriesIntent = new Intent(this, CategoriesActivity.class);
        categoriesIntent.putExtras(bundle);

        this.startActivityForResult(categoriesIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) {
            return;
        }
        Bundle extras = data.getExtras();
        if(extras == null) {
            return;
        }

        // it is silly to get the id's as a result when we could just save them in this class, but why not
        long exhibId = extras.getLong(MainActivity.EXHIB_ID);
        long userId = extras.getLong(MainActivity.USER_ID);
        long nodeId = extras.getLong(MainActivity.BOOTH_ID);

        // If the result contained a nodeId, then fetch it (shouldn't currently happen)
        this.currentBoothId = nodeId != 0L ? nodeId : this.currentBoothId;

        Bundle bundle = new Bundle();
        bundle.putLong(MainActivity.EXHIB_ID, exhibId);
        bundle.putLong(MainActivity.USER_ID, userId);
        bundle.putLong(MainActivity.BOOTH_ID, this.currentBoothId); // save the booth id in bundle

        this.currentBoothId = 0L; // then overwrite the value

        Intent intent = new Intent(this, TabActivity.class);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

    private String readIdFile() {
        FileInputStream fileInputStream = null;
        StringWriter stringWriter = new StringWriter(1024);

        try {
            fileInputStream = this.openFileInput(MainActivity.FILE_PATH);

            int content;
            while ((content = fileInputStream.read()) != -1) {
                // convert to char and append to string
                stringWriter.append((char) content);
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stringWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return stringWriter.toString();
    }

    private Long findUserId(String fileContents, long searchExhibId) {
        if (fileContents == "") {
            return null;
        }
        String[] idPairs = fileContents.split("\n");

        // For each configuration
        for (int i = 0; i < idPairs.length; i++) {

            String[] ids = idPairs[i].split(",");

            long exhibId = Long.valueOf(ids[0]);
            long userId = Long.valueOf(ids[1]);

            if(exhibId == searchExhibId) {
                // the user is already registred
                return userId;
            }
        }
        // the user is not registred
        return null;
    }

    private void writeIdFile(String fileContents) {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = this.openFileOutput(MainActivity.FILE_PATH, Context.MODE_PRIVATE);
            fileOutputStream.write(fileContents.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
