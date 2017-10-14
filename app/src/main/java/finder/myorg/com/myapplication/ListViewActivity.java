package finder.myorg.com.myapplication;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import finder.myorg.com.myapplication.finder.myorg.com.model.MainPojo;

/**
 * Created by kranti_chintala@myorg.com on 1/11/16.
 */

public class ListViewActivity extends android.app.ListActivity {

    private static final String LIST_ACTICITY = "ListActivity";
    private String sBarCode;
    private int iZipCode;
    private Double sLattitude;
    private Double sLongitude;
    private ArrayList<Address> addressList;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    private ArrayList<String> listItems = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private ArrayAdapter<String> adapter;
    private MainPojo mainPojo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liststore);
        final EditText zipCodeTxt = (EditText) findViewById(R.id.txtItem);

        Button btn = (Button) findViewById(R.id.btnAdd);

        Intent scannerIntent = getIntent();
        this.sLattitude = scannerIntent.getDoubleExtra("LATTITUDE", 0.0);
        this.sLongitude = scannerIntent.getDoubleExtra("LANGITUDE", 0.0);
        this.sBarCode = scannerIntent.getStringExtra("BAR_CODE");
        this.addressList = scannerIntent.getParcelableArrayListExtra("STORE_LIST");
        this.iZipCode = scannerIntent.getIntExtra("ZIP_CODE", 00000);
        zipCodeTxt.setText("" + this.iZipCode);


        /** Defining the ArrayAdapter to set items to ListView */
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        this.getListView().setAdapter(adapter);

        getListOfStoresFromAPI();


        //ListView listView = (ListView) findViewById(R.id.list);
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LIST_ACTICITY, "Item CLicked " + position);
                Intent mIntent = new Intent(ListViewActivity.this, StoreLocatorActivity.class);
                mIntent.putExtra("LATTITUDE", (Double) mainPojo.getStoreInfo().get(position).getLatitude());
                mIntent.putExtra("LANGITUDE", (Double) mainPojo.getStoreInfo().get(position).getLongitude());
                mIntent.putExtra("STORE_NAME", (String) mainPojo.getStoreInfo().get(position).getName());
                startActivity(mIntent);
            }
        });


        /** Defining a click event listener for the button "Add" */
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(LIST_ACTICITY, "Button is cilicked with zip");
                if (null != zipCodeTxt.getText() && zipCodeTxt.getText().length() > 0){
                    iZipCode = Integer.parseInt(zipCodeTxt.getText().toString());
                    if (isValidZipcode(iZipCode)) {
                        //Call to API to list the Address
                        getListOfStoresFromAPI();
                    }
                }
            }
        };
        /** Setting the event listener for the add button */
        btn.setOnClickListener(listener);
        //Log.d(LIST_ACTICITY, this.sLattitude + " : " + this.sLongitude + " : " + this.sBarCode);
    }

    private boolean isValidZipcode(int iZipCode) {
        if ((int) (Math.log10(iZipCode) + 1) == 5) {
            this.adapter.clear();
            return true;
        } else {
            this.adapter.clear();
            Toast.makeText(this, getString(R.string.zipcode_length), Toast.LENGTH_SHORT).show();
            return false;
        }

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                if (null != mainPojo) {
                    listItems.clear();
                    for (int i = 0; i < mainPojo.getStoreInfo().size(); i++) {
                        //Log.d(LIST_ACTICITY, "The Updating record is : " + mainPojo.getStoreInfo().get(i).getName());
                        listItems.add(mainPojo.getStoreInfo().get(i).getName());
                    }
                    if(listItems.size() < 2){
                        listItems.set(0,"No Stores Found at "+iZipCode);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            super.handleMessage(msg);

        }
    }; //end of handler



    private void getListOfStoresFromAPI() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Start from Here tomorrow
                try {
                    //Log.d(LIST_ACTICITY, sBarCode + "" + iZipCode);
                    //Log.d(LIST_ACTICITY, "Before Calling API " + sBarCode + " : " + iZipCode);
                    mainPojo = Util.getListOfStoreWithUPC(sBarCode, iZipCode, Integer.parseInt(getString(R.string.API_radius)));
                    if (mainPojo == null) {
                        showToastMessage(getString(R.string.stores_list_failed));
                    } else {
                        mHandler.sendEmptyMessage(100);
                    }
                } catch (IOException ioe) {
                    showToastMessage(getString(R.string.stores_list_failed));
                    //Toast.makeText(ListViewActivity.this, getString(R.string.stores_list_failed), Toast.LENGTH_SHORT).show();
                    Log.d(LIST_ACTICITY, "Unable to retrieve Stores List. Exception caused " + ioe.getMessage());

                } catch (Exception ex) {
                    showToastMessage(getString(R.string.stores_list_failed));
                    Log.d(LIST_ACTICITY, "Unable to retrieve Stores List. Exception caused " + ex.getMessage());

                }

            }
        }).start();
    }


    protected void showToastMessage(String sMessage){
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ListViewActivity.this, getString(R.string.stores_list_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
