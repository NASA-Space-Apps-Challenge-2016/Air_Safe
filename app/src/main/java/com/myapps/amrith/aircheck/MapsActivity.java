package com.myapps.amrith.aircheck;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.myapps.amrith.aircheck.models.co.ServerResponse;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    EditText loc;
    String locn;// Might be null if Google Play services APK is not available.
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void onSearch(View view) throws IOException {
        loc = (EditText) findViewById(R.id.editText7);
        locn = loc.getText().toString();
        List<Address> addressList = null;
        if (locn != null || locn.equals(" ")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(locn, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latlng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));

        }

    }

    public void savedata(View v) {
        List<Address> addressList = null;
        if (locn != null && !locn.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(locn, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
            RequestParams params = new RequestParams();
            params.put("Temperature", "AB");
            params.put("CO", "VC");
            params.put("NO2", "VD");
            params.put("O3", "SS");
            params.put("SO2", "SS");
            params.put("RH", "SF");

            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://192.168.99.14/db/register.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                    String sResponse = response.toString();
                    Log.d("Success", sResponse + "s");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    e.printStackTrace();
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    public void checksymptoms(View v) {
        try {
            if (locn != null && !locn.equals("")) {
                Geocoder geocoder = new Geocoder(this);
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocationName(locn, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(addressList.size()>0 && addressList.get(0)!=null){
                    Address address = addressList.get(0);
                    latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    Log.d("got latlong",latLng.latitude + " " + latLng.longitude);
                }
            }

            AsyncHttpClient ClientCO = new AsyncHttpClient();
            AsyncHttpClient ClientSO2 = new AsyncHttpClient();
            AsyncHttpClient ClientNO2 = new AsyncHttpClient();
            AsyncHttpClient ClientO3 = new AsyncHttpClient();
            DecimalFormat df = new DecimalFormat("#");
            ClientCO.get("http://api.openweathermap.org/pollution/v1/co/" + df.format(latLng.latitude) + "," + df.format(latLng.longitude) + "/current.json?appid=9f2dd8aa495c71eb721d465ea7e03eb1", new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                    String sresponse = new String(response);
                    Log.d("Response0", sresponse);
                    ServerResponse serverResponse = new Gson().fromJson(sresponse,ServerResponse.class);
                    Log.d("respnseClass",serverResponse.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });

            ClientSO2.get("http://api.openweathermap.org/pollution/v1/so2/" + df.format(latLng.latitude) + "," + df.format(latLng.longitude) + "/current.json?appid=9f2dd8aa495c71eb721d465ea7e03eb1", new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
        mMap.setMyLocationEnabled(true);
    }
}
