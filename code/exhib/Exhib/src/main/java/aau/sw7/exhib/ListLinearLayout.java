package aau.sw7.exhib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Jesper on 17-09-13.
 */
public abstract class ListLinearLayout<ListObject> extends LinearLayout {

    private ArrayList<ListObject> items = new ArrayList<ListObject>();

    public ListLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract View makeView(ListObject object);

    public void addViewAtBottom(ListObject listObject) {
        this.items.add(listObject);
        super.addView(this.makeView(listObject));
    }

    public void addViewAtTop(ListObject listObject) {
        //Add at index 0 in both
        this.items.add(0, listObject);
        this.addView(this.makeView(listObject), 0);
    }

    public void removeView(int index) {
        // Remove from both lists
        this.removeViewAt(index);
        this.items.remove(index);
    }

    public void removeView(ListObject listObject) {
        this.removeView(this.items.indexOf(listObject));
    }
}
