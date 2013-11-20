package map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;

import aau.sw7.exhib.BoothItem;
import aau.sw7.exhib.R;

/**
 * Created by Jacob on 18-11-13.
 */
public class PopupAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater = null;
    ArrayList<BoothItem> boothItems;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    DisplayImageOptions imageLoaderOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();


    public PopupAdapter(LayoutInflater layoutInflater, ArrayList<BoothItem> boothItems){
        this.inflater = layoutInflater;
        this.boothItems = boothItems;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }


    private BoothItem findBoothByName(String name){
        for(BoothItem b : boothItems){
            if(b.getBoothName().equals(name)){
                return b;
            }
        }
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View popup = inflater.inflate(R.layout.popup, null);
        BoothItem boothItem = findBoothByName(marker.getTitle());

        ImageView imgv = (ImageView)popup.findViewById(R.id.icon);
        if(boothItem != null){
            this.imageLoader.displayImage(boothItem.getCompanyLogo(), imgv, this.imageLoaderOptions);
        }else{
            //imgview none
        }

        TextView tv = (TextView)popup.findViewById(R.id.title);
        tv.setText(marker.getTitle());

        tv = (TextView)popup.findViewById(R.id.snippet);
        tv.setText(marker.getSnippet());

        return(popup);
    }
}
