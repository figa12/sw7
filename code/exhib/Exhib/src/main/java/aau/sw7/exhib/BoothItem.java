package aau.sw7.exhib;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import map.Node;
import map.Square;

/**
 * Created by jerian on 23-10-13.
 */
public class BoothItem implements Parcelable {

    private int boothId;
    private String boothName;
    private String description;
    private String companyLogo;
    private boolean subscribed;
    private Square square;
    private ArrayList<Node> boothWaypoints;

    private Category parentCategory; // is set in makeView()

    private CheckBox checkBox;

    /***
     * boothCoordinate should be: BottomRight, TopLeft
     * @param boothId
     * @param boothName
     * @param description
     * @param companyLogo
     * @param subscribed
     * @param square
     */
    public BoothItem(int boothId, String boothName, String description, String companyLogo, boolean subscribed, Square square) {
        this.boothId = boothId;
        this.boothName = boothName;
        this.description = description;
        this.companyLogo = companyLogo;
        this.subscribed = subscribed;
        this.square = square;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public void setChecked(boolean checked) {
        this.checkBox.setChecked(checked);
    }

    public boolean isSubscribed() {
        return this.subscribed;
    }

    public String getBoothName() {
        return this.boothName;
    }

    public String getDescription() {
        return this.description;
    }

    public int getBoothId() {
        return this.boothId;
    }

    public String getCompanyLogo() {
        return this.companyLogo;
    }

    public LatLng getSquareCenter() {
        return this.square.getCenter();
    }

    public ArrayList<LatLng> getSquareBounds(){
        return this.square.getSqaureBounds();
    }

    public Square getSquare() {
        return this.square;
    }

    public View makeView(Context context, Category category) {
        CheckBox boothCheckBox = new CheckBox(context);
        boothCheckBox.setChecked(this.isSubscribed());
        boothCheckBox.setText(this.boothName);
        boothCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BoothItem.this.subscribed = isChecked;
            }
        });
        this.parentCategory = category;
        return this.checkBox = boothCheckBox;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.boothId);
        out.writeString(this.boothName);
        out.writeString(this.description);
        out.writeList(this.square.toList());
        // don't save category, results in stackOverflow
    }

    public static final Creator<BoothItem> CREATOR = new Creator<BoothItem>() {
        @Override
        public BoothItem createFromParcel(Parcel in) {
            return new BoothItem(in);
        }

        @Override
        public BoothItem[] newArray(int size) {
            return new BoothItem[size];
        }
    };

    private BoothItem(Parcel in) {
        this.boothId = in.readInt();
        this.boothName = in.readString();
        this.description = in.readString();
        in.readList(this.square.toList(), LatLng.class.getClassLoader());
    }
}
