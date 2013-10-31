package aau.sw7.exhib;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by jerian on 23-10-13.
 */
public class Category implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.categoryId);
        out.writeString(this.categoryName);
        out.writeList(this.boothItems);
    }

    public static final Creator<Category> CREATER = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    private Category(Parcel in) {
        this.categoryId = in.readInt();
        this.categoryName = in.readString();
        in.readList(this.boothItems, BoothItem.class.getClassLoader());
    }
}
