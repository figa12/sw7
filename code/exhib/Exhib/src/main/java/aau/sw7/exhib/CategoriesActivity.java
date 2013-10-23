package aau.sw7.exhib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by jerian on 23-10-13.
 */
public class CategoriesActivity extends Activity {

    private LinearLayout categoryLinearlayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_categories);

        Bundle exhibIDS = getIntent().getExtras();

        Integer exhibID = exhibIDS.getInt("exhibID");
        Integer boothID = exhibIDS.getInt("boothID");

        this.categoryLinearlayout = (LinearLayout) super.findViewById(R.id.categoryLayout);

        new ServerSyncService(this).execute(
                new BasicNameValuePair("RequestCode", String.valueOf(ServerSyncService.GET_CATEGORIES)),
                new BasicNameValuePair("Type", "GetCategories"),
                new BasicNameValuePair("UserId", "1"));

        BoothItem booth1 = new BoothItem(2, "Microsoft Xbox", "Det er godt", null, null);
        ArrayList<BoothItem> booths = new ArrayList<BoothItem>();
        booths.add(booth1);
        booths.add(booth1);

        Category category1 = new Category(1, "Software", booths);
        Category category2 = new Category(1, "Software", booths);

        ArrayList<Category> categories = new ArrayList<Category>();
        categories.add(category1);
        categories.add(category2);

        this.setCategories(categories);
    }

    public void setCategories(ArrayList<Category> categories) {
        for (Category category : categories) {
            this.categoryLinearlayout.addView(category.makeView(this));
        }
    }

    //temp button click to open the tab acitivity
    public void onClicktemp (View v){

        super.startActivity(new Intent(this, TabActivity.class));

    }

}