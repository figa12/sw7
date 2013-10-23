package aau.sw7.exhib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by jerian on 23-10-13.
 */
public class Category {

    private int categoryId;
    private String categoryName;
    private ArrayList<BoothItem> boothItems = new ArrayList<BoothItem>();

    public Category( int categoryId, String categoryName, ArrayList<BoothItem> boothItems) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.boothItems = boothItems;
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void addBooth(BoothItem boothItem) {
        this.boothItems.add(boothItem);
    }

    @SuppressWarnings("ConstantConditions")
    public View makeView(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View categoryView = layoutInflater.inflate(R.layout.category_item, null);

        CheckBox nameTextView = (CheckBox) categoryView.findViewById(R.id.categoryName);
        nameTextView.setText(this.categoryName);

        LinearLayout boothLinearLayout = (LinearLayout) categoryView.findViewById(R.id.boothLayout);
        for (BoothItem boothItem : this.boothItems) {
            boothLinearLayout.addView(boothItem.makeView(context));
        }

        return categoryView;
    }
}
