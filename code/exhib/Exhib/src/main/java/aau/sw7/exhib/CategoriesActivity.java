package aau.sw7.exhib;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.ndeftools.Record;

import java.util.ArrayList;

import NfcForeground.NfcForegroundActivity;

/**
 * Created by jerian on 23-10-13.
 */
public class CategoriesActivity extends NfcForegroundActivity implements ICategoriesReceiver {

    private AlertDialog backAlertDialog;

    private ProgressDialog progressDialog;
    private LinearLayout categoryLinearlayout;
    private ArrayList<Category> categories;

    // id 0 should not exist!
    private long exhibId = 0;
    private long userId = 0;

    @Override
    protected void onNfcScanned(ArrayList<Record> records) {
        Toast.makeText(this, "Please select booths", Toast.LENGTH_SHORT).show(); //TODO change message
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_categories);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            // getInt returns 0 if there isn't any mapping to them
            this.exhibId = extras.getLong(MainActivity.EXHIB_ID);
            this.userId = extras.getLong(MainActivity.USER_ID);
        } else {
            // Error, we should always have id's
            ((Button) super.findViewById(R.id.submitButton)).setEnabled(false);
        }

        this.categoryLinearlayout = (LinearLayout) super.findViewById(R.id.categoryLayout);

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Getting categories");
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();

        new ServerSyncService(this).execute(
                new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_CATEGORIES)),
                new BasicNameValuePair("Type", "GetCategories"),
                new BasicNameValuePair("ExhibId", String.valueOf(this.exhibId)));

        this.backAlertDialog = this.createAlertDialog();
    }

    private AlertDialog createAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //myAlertDialog.setTitle("Title");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage("Your booth selection will not be saved");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent result = new Intent();
                result.putExtra(MainActivity.USER_ID, CategoriesActivity.this.userId);
                result.putExtra(MainActivity.EXHIB_ID, CategoriesActivity.this.exhibId);
                result.putExtra(TabActivity.BOOTH_ITEMS, CategoriesActivity.this.getAllBoothItems());

                CategoriesActivity.super.setResult(Activity.RESULT_CANCELED, result);
                CategoriesActivity.super.finish();
                CategoriesActivity.super.finish();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // do nothing
            }
        });
        return alertDialogBuilder.create();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Stop the user from unexpected back presses
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.backAlertDialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;

        for (Category category : this.categories) {
            this.categoryLinearlayout.addView(category.makeView(this));
        }
        this.progressDialog.dismiss();
    }

    private ArrayList<BoothItem> getCheckedBooths() {
        ArrayList<BoothItem> boothItems = new ArrayList<BoothItem>();

        for (Category category : this.categories) {
            for (BoothItem boothItem : category.getBoothItems()) {
                if (boothItem.isChecked()) {
                    boothItems.add(boothItem);
                }
            }
        }
        return boothItems;
    }

    public void onClickSubmitButton(View v) {
        String boothIds = "";

        ArrayList<BoothItem> checkedBoothItems = this.getCheckedBooths();
        for (int i = 0; i < checkedBoothItems.size(); i++) {
            boothIds += checkedBoothItems.get(i).getBoothId();
            boothIds += i != checkedBoothItems.size()-1 ? "," : "";
        }

        new ServerSyncService(this).execute(
                new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.SET_CATEGORIES)),
                new BasicNameValuePair("Type", "SetCategories"),
                new BasicNameValuePair("BoothIds", boothIds),
                new BasicNameValuePair("UserId", String.valueOf(this.userId)));
    }

    public void onServerBoothResponse() {
        Intent result = new Intent();
        result.putExtra(MainActivity.USER_ID, this.userId);
        result.putExtra(MainActivity.EXHIB_ID, this.exhibId);
        result.putExtra(TabActivity.BOOTH_ITEMS, this.getAllBoothItems());

        super.setResult(Activity.RESULT_OK, result);
        super.finish();
    }

    private ArrayList<BoothItem> getAllBoothItems() {
        ArrayList<BoothItem> boothItems = new ArrayList<BoothItem>();

        for (Category category : this.categories) {
            boothItems.addAll(category.getBoothItems());
        }

        return boothItems;
    }
}