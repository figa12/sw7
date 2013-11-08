package aau.sw7.exhib;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by jerian on 23-10-13.
 */
public class BoothItem implements Parcelable {

    private int boothId;
    private String boothName;
    private String description;
    private String companyLogo;
    private boolean subscribed;
    private LatLng boothCoordinateCenter;
    private ArrayList<LatLng> coordinates = new ArrayList<LatLng>();

    private Category parentCategory; // is set in makeView()

    private CheckBox checkBox;

    /***
     * boothCoordinate should be: bottomLeft, bottomRight, upperRight, upperLeft
     * @param boothId
     * @param boothName
     * @param description
     * @param companyLogo
     * @param subscribed
     * @param coordinates
     */
    public BoothItem(int boothId, String boothName, String description, String companyLogo, boolean subscribed, ArrayList<LatLng> coordinates) {
        this.boothId = boothId;
        this.boothName = boothName;
        this.description = description;
        this.companyLogo = companyLogo;
        this.subscribed = subscribed;
        this.coordinates = coordinates;
        this.boothCoordinateCenter = calculateCenter(this.coordinates.get(0), this.coordinates.get(2));
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
        return boothName;
    }

    public String getDescription() {
        return description;
    }

    public int getBoothId() {
        return boothId;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public LatLng getBoothCoordinate() {
        return boothCoordinateCenter;
    }

    public ArrayList<LatLng> getCoordinates() {
        return coordinates;
    }

    private LatLng calculateCenter(LatLng p1, LatLng p2){
        if(this.coordinates.size() == 4){
            LatLng difference = new LatLng(p2.latitude - p1.latitude, p2.longitude - p2.longitude);
            LatLng offset = new LatLng(difference.latitude/2, difference.longitude/2);
            return new LatLng(this.getCoordinates().get(0).latitude + offset.latitude, this.getCoordinates().get(0).longitude + offset.latitude); //bottomRight.latitude + offset.latitude, bottomright.longitude + offset.longitude
        }
        else{
            return null;
        }
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
        out.writeParcelable(this.boothCoordinateCenter, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        out.writeList(this.coordinates);
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
        this.boothCoordinateCenter = in.readParcelable(LatLng.class.getClassLoader());
        in.readList(this.coordinates, LatLng.class.getClassLoader());
    }
}
