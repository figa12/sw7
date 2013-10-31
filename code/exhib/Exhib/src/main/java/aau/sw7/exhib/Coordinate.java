package aau.sw7.exhib;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jerian on 23-10-13.
 */
public class Coordinate implements Parcelable {
    private int x;
    private int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.x);
        out.writeInt(this.y);
    }

    public static final Creator<Coordinate> CREATER = new Creator<Coordinate>() {
        @Override
        public Coordinate createFromParcel(Parcel in) {
            return new Coordinate(in);
        }

        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };

    private Coordinate(Parcel in) {
        this.x = in.readInt();
        this.y = in.readInt();
    }
}
