package finder.myorg.com.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import java.io.Serializable;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends Activity implements ZXingScannerView.ResultHandler,Serializable {

    static final String SCANNER_ACTIVITY = "ScannerActivity";
    private ZXingScannerView mScannerView;
    private double lattitude = 0.0;
    private double langitude = 0.0;
    private String strBarCode;
    private int iZipCode;
    private LocationManager mLocationManager;
    private ArrayList<Address> addressList;


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            double dLat = location.getLatitude();
            double dLong = location.getLongitude();
            setLocation(dLat, dLong);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    public void onCreate(Bundle state) {
        //Log.d(SCANNER_ACTIVITY, " onCreate called: " + new Date());
        super.onCreate(state);
        final int LOCATION_REFRESH_DISTANCE = Integer.parseInt(getString(R.string.location_refresh_distance));
        final int LOCATION_REFRESH_TIME = Integer.parseInt(getString(R.string.location_refresh_interval));
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);// Set the scanner view as the content view

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (lattitude == 0.0) {
                    try {
                        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (null != location) {
                            lattitude = location.getLatitude();
                            langitude = location.getLongitude();
                            //Log.d(SCANNER_ACTIVITY, " Found the Lattitude and Langitude : " + lattitude + " : " + langitude);
                            setLocation(lattitude, langitude);
                        }
                    } catch (Exception ex) {
                        Log.d(SCANNER_ACTIVITY, " Exception in getting Location object: " + ex.getMessage());
                    }
                }


                try {
                   // Log.d(SCANNER_ACTIVITY, " Accessing Address with lattitude and langitude: " + lattitude + langitude);
                    addressList = Util.getFromLocation(lattitude, langitude, -1);
                    String[] strAddress = null;
                    if (null != addressList && addressList.size() > 0) {
                        //Log.d(SCANNER_ACTIVITY, "Received the Address " + addressList);
                        strAddress = addressList.get(0).getAddressLine(0).split(",");
                        if (null != strAddress && strAddress.length > 2) {
                            iZipCode = Integer.valueOf(strAddress[2].replaceAll("[^0-9]", ""));
                        }
                        //Log.d(SCANNER_ACTIVITY, "Found the Address " + strAddress + " : " + iZipCode);
                    }

                } catch (Exception e) {
                    Log.d(SCANNER_ACTIVITY, " Exception in getting Address with lattitude and langitude: " + e.getMessage());

                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.d(SCANNER_ACTIVITY, "onHandleRequest called");
        if (null != rawResult) {
            String strBarCode = rawResult.getText();
            if (null != strBarCode) {
                this.strBarCode = strBarCode;
            }
            Log.d(SCANNER_ACTIVITY, rawResult.getText()); // Prints scan results
            Log.d(SCANNER_ACTIVITY, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
            Log.d(SCANNER_ACTIVITY, this.langitude +" "+this.lattitude +" "+this.iZipCode);
            // If you would like to resume scanning, call this method below:
            //mScannerView.resumeCameraPreview(this);

            Intent mIntent = new Intent(this, ListViewActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt("ZIP_CODE",this.iZipCode);
            mBundle.putDouble("LATTITUDE", this.lattitude);
            mBundle.putDouble("LANGITUDE", this.langitude);
            mBundle.putString("BAR_CODE", this.strBarCode);
            mBundle.putParcelableArrayList("STORE_LIST", this.addressList);
            mIntent.putExtras(mBundle);
            startActivity(mIntent);


        }
    }

    private void setLocation(double dLat, double dLong) {
        this.lattitude = dLat;
        this.langitude = dLong;
    }
}