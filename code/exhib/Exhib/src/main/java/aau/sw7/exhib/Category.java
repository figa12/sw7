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

    private CheckBox checkBox;

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<BoothItem> getBoothItems() {
        return boothItems;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setBoothItems(ArrayList<BoothItem> boothItems) {
        this.boothItems = boothItems;
    }

    @SuppressWarnings("ConstantConditions")
    public View makeView(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View categoryView = layoutInflater.inflate(R.layout.category_item, null);

        this.checkBox = (CheckBox) categoryView.findViewById(R.id.categoryName);
        this.checkBox.setText(this.categoryName);
        this.checkBox.setChecked(true);
        this.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Category.this.checkBox.isChecked()) {
                    for (BoothItem boothItem : Category.this.boothItems) {
                        boothItem.setChecked(true);
                    }
                } else {
                    for (BoothItem boothItem : Category.this.boothItems) {
                        boothItem.setChecked(false);
                    }
                }
            }
        });

        LinearLayout boothLinearLayout = (LinearLayout) categoryView.findViewById(R.id.boothLayout);
        for (BoothItem boothItem : this.boothItems) {
            boothLinearLayout.addView(boothItem.makeView(context, this));
        }

        return categoryView;
    }
}
