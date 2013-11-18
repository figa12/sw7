package map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import aau.sw7.exhib.BoothItem;
import aau.sw7.exhib.R;

/**
 * Created by Jacob on 18-11-13.
 */
public class PopupAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater = null;
    ArrayList<BoothItem> boothItems;


    public PopupAdapter(LayoutInflater layoutInflater, ArrayList<BoothItem> boothItems){
        this.inflater = layoutInflater;
        this.boothItems = boothItems;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
        Bitmap bit = getBitmapFromURL(boothItem.getCompanyLogo());
        if(bit != null){
            imgv.setImageBitmap(bit);
        }
        else{
            imgv.setImageResource(R.drawable.ic_launcher);
        }

        TextView tv = (TextView)popup.findViewById(R.id.title);
        tv.setText(marker.getTitle());

        tv = (TextView)popup.findViewById(R.id.snippet);
        tv.setText(marker.getSnippet());

        return(popup);
    }
}
