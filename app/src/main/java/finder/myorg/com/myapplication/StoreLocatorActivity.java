package finder.myorg.com.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by kranti_chintala@myorg.com on 1/11/16.
 */
public class StoreLocatorActivity extends Activity {

    Double sLattitude;
    Double sLongitude;
    String sStoreName;
    private static final String STORELOCATE_ACTIVITY = "StoreLocatorActivity";

    LatLng latLong = null;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Intent listViewIntent = getIntent();
        this.sLattitude = listViewIntent.getDoubleExtra("LATTITUDE", 0.0);
        this.sLongitude = listViewIntent.getDoubleExtra("LANGITUDE", 0.0);
        this.sStoreName = listViewIntent.getStringExtra("STORE_NAME");
        Log.d(STORELOCATE_ACTIVITY,this.sLattitude +" : "+this.sLongitude +" : "+this.sStoreName);
        try {
            if (null != this.sLattitude && null != this.sLongitude) {
                latLong = new LatLng(sLattitude, sLongitude);
                if (googleMap == null) {
                    googleMap = ((MapFragment) getFragmentManager().
                            findFragmentById(R.id.map)).getMap();
                }
                MarkerOptions mOptions = new MarkerOptions();
                mOptions.position(latLong).title(this.sStoreName);
                //mOptions.snippet(this.sStoreName);
                Marker marker = googleMap.addMarker(mOptions);
                marker.showInfoWindow();
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 5000, null);


            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.maps_loading_issue), Toast.LENGTH_SHORT).show();
            Log.d(STORELOCATE_ACTIVITY,e.getMessage());
        }
    }
}
