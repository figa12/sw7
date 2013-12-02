package aau.sw7.exhib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;

import map.MapWrapperLayout;

/**
 * Created by jacob on 10/3/13.
 */
public class FloorFragment extends SupportMapFragment {
    private static final String SUPPORT_MAP_BUNDLE_KEY = "MapOptions";
    private MapWrapperLayout mapWrapperLayout;
    public static interface OnFloorFragmentListener {
        void onMapReady(GoogleMap map);
    }

    public static FloorFragment newInstance() {
        return new FloorFragment();
    }

    public MapWrapperLayout getMapWrapperLayout() {
        return mapWrapperLayout;
    }

    public static FloorFragment newInstance(GoogleMapOptions options) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(SUPPORT_MAP_BUNDLE_KEY, options);

        FloorFragment fragment = new FloorFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnFloorFragmentListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getName() + " must implement OnGoogleMapFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        MapWrapperLayout mapWrapperLayout = new MapWrapperLayout(this.getActivity());
        mapWrapperLayout.addView(view);
        this.mapWrapperLayout = mapWrapperLayout;

        if (mCallback != null) {
            mCallback.onMapReady(getMap());
        }
        return mapWrapperLayout;
    }



    private OnFloorFragmentListener mCallback;
}
