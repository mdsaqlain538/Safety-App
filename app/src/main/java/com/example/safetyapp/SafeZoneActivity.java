package com.example.safetyapp;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SafeZoneActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ProgressDialog progressDialog;

    RequestQueue requestQueue;

    private static  final int REQUEST_LOCATION=1;

    LocationManager locationManager;

    ListView listView;
    Location_Adapter adapter;

    String Url = "https://radiant-hamlet-85497.herokuapp.com";
    List<String> names;
    List<String> from;
    List<String> to;
    double Mylat;
    double Mylng;
    Polyline polyline;
    PolylineOptions polylineOptions;
    ArrayList<LatLng> coordinates = new ArrayList<LatLng>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_zone);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        requestQueue= Volley.newRequestQueue(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        names = new ArrayList<String>();
        from = new ArrayList<String>();
        to = new ArrayList<String>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listView = findViewById(R.id.safelist);
        coordinates  = callSApi();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(polyline != null)polyline.remove();
                TextView t = view.findViewById(R.id.locName);
                TextView cor1 = view.findViewById(R.id.from);
                TextView cor2 = view.findViewById(R.id.to);
                String na = t.getText().toString();
                polylineOptions = new PolylineOptions();
                double polyLat = Double.parseDouble(cor1.getText().toString());
                double polylong = Double.parseDouble(cor2.getText().toString());
                LatLng dest = new LatLng(polyLat, polylong);
                LatLng src = new LatLng(Mylat, Mylng);
                polyline = mMap.addPolyline(polylineOptions.add(src).add(dest).width(10f).color(R.color.colorblack));
                Toast.makeText(SafeZoneActivity.this, ""+na, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Mylat, Mylng);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).snippet("You"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f));
    }

    private ArrayList callSApi() {
        final ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
        progressDialog.show();
        progressDialog.setMessage("Loading....");


        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, Url+"/getsafezones", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    JSONArray data = response.getJSONArray("data");
                    for(int i=0; i<data.length(); i++) {
                        JSONObject loc = data.getJSONObject(i);
                        String name = loc.getString("name");
                        names.add(name);
                        JSONArray coord = loc.getJSONArray("coord");
                        double lat = Double.parseDouble(coord.getString(0));
                        double longi = Double.parseDouble(coord.getString(1));
                        from.add(coord.getString(0));
                        to.add(coord.getString(1));
                        LatLng latLng = new LatLng(lat, longi);
                        mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                        coordinates.add(latLng);
                    }
                    adapter = new Location_Adapter(SafeZoneActivity.this, names, from, to);
                    listView.setAdapter(adapter);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();

                String message = null;
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                    Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_SHORT).show();

                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                    Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_SHORT).show();

                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                    Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_SHORT).show();

                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                    Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_SHORT).show();

                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                    Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_SHORT).show();

                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                    Toast.makeText(getApplicationContext(),""+message,Toast.LENGTH_SHORT).show();

                }
                Toast.makeText(getApplicationContext(),""+volleyError,Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
        return coordinates;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(SafeZoneActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SafeZoneActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (locationGps != null) {
                Mylat = locationGps.getLatitude();
                Mylng = locationGps.getLongitude();
            } else if (locationNet != null) {
                Mylat = locationNet.getLatitude();
                Mylng = locationNet.getLongitude();
            } else if (locationPassive != null) {
                Mylat = locationPassive.getLatitude();
                Mylng = locationPassive.getLongitude();
            } else {
                Toast.makeText(this, "Can't get Location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
