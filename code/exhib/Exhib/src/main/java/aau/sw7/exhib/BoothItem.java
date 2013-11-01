package aau.sw7.exhib;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;

/**
 * Created by jerian on 23-10-13.
 */
public class BoothItem implements Parcelable {

    private int boothId;
    private String boothName;
    private String description;
    private Coordinate boothCoordinate;
    private ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();

    private Category parentCategory; // is set in makeView()

    private CheckBox checkBox;

    public BoothItem(int boothId, String boothName, String description, Coordinate boothCoordinate, ArrayList<Coordinate> coordinates) {
        this.boothId = boothId;
        this.boothName = boothName;
        this.description = description;
        this.boothCoordinate = boothCoordinate;
        this.coordinates = coordinates;
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

    public String getBoothName() {
        return boothName;
    }

    public String getDescription() {
        return description;
    }

    public int getBoothId() {
        return boothId;
    }

    public Coordinate getBoothCoordinate() {
        return boothCoordinate;
    }

    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }

    public View makeView(Context context, Category category) {
        CheckBox boothCheckBox = new CheckBox(context);
        boothCheckBox.setChecked(true);
        boothCheckBox.setText(this.boothName);
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
        out.writeParcelable(this.boothCoordinate, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
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
        this.boothCoordinate = in.readParcelable(Coordinate.class.getClassLoader());
        in.readList(this.coordinates, Coordinate.class.getClassLoader());
    }
}
