package aau.sw7.exhib;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;

/**
 * Created by jerian on 23-10-13.
 */
public class BoothItem {

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
}
