package aau.sw7.exhib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Jesper on 17-09-13.
 * An abstract class for making a {@link android.widget.LinearLayout} that works almost the same way as a {@link android.widget.ListView}.
 *
 * When inheriting the class you must specify an object which is the data representation of each view.
 * Classes must override the abstract method {@code makeView(ListObject object)} which should create a view representation of the given {@link ListObject}.
 *
 * @see aau.sw7.exhib.FeedLinearLayout
 */
public abstract class ListLinearLayout<ListObject> extends LinearLayout {

    /** List of {@link ListObject} */
    private ArrayList<ListObject> items = new ArrayList<ListObject>();

    public ListLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param index Index of a {@link ListObject}.
     * @return The {@link ListObject} at the specified index.
     */
    public ListObject get(int index) {
        return this.items.get(index);
    }

    /** @return The number of elements in this {@link aau.sw7.exhib.ListLinearLayout}.*/
    public int getSize() {
        return this.items.size();
    }

    /**
     * Creates a view for the specified {@link ListObject}
     * @param object The object to add.
     * @return A {@link View} representation of the data.
     */
    protected abstract View makeView(ListObject object);

    /**
     * Adda the specified {@link ListObject} at the bottom of this {@link aau.sw7.exhib.ListLinearLayout}.
     * @param listObject The object to add.
     */
    public void addViewAtBottom(ListObject listObject) {
        this.items.add(listObject);
        super.addView(this.makeView(listObject));
    }

    /**
     * Adda the specified {@link ListObject} at the top of this {@link aau.sw7.exhib.ListLinearLayout}.
     * @param listObject The object to add.
     */
    public void addViewAtTop(ListObject listObject) {
        //Add at index 0 in both
        this.items.add(0, listObject);
        this.addView(this.makeView(listObject), 0);
    }

    /**
     * Remove a {@link ListObject} at the specified index.
     * @param index The index in the {@link aau.sw7.exhib.ListLinearLayout} of the {@link ListObject}.
     */
    public void removeView(int index) {
        // Remove from both lists
        this.removeViewAt(index);
        this.items.remove(index);
    }

    /**
     * Searches the {@link aau.sw7.exhib.ListLinearLayout} for the {@link ListObject} and removes it.
     * @param listObject The {@link ListObject} to remove.
     */
    public void removeView(ListObject listObject) {
        // I think this will give an exception if the items doesn't exist. But you should not try to remove items that doesn't exist.
        this.removeView(this.items.indexOf(listObject));
    }
}
