package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    Button button,emergency, safezone, start;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    AutoCompleteTextView fromAuto, toAuto;
    ArrayAdapter arrayAdapter1, arrayAdapter2;
    RequestQueue requestQueue;
    ProgressDialog progressDialog1;
    String Url = "https://radiant-hamlet-85497.herokuapp.com";
    String uid;

    String[] fromLoc = { "balanagar", "bowenpally","secunderabad", "medchal", "yousoufguda", "ameerpet"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        emergency=findViewById(R.id.emergency);
        safezone = findViewById(R.id.btnSafeZone);
        fromAuto = findViewById(R.id.fromAuto);
        toAuto = findViewById(R.id.toAuto);
        start = findViewById(R.id.startService);
        requestQueue= Volley.newRequestQueue(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fr = fromAuto.getText().toString();
                String too = toAuto.getText().toString();
                if(fr != "" && too != "") {
                    upload(fr, too);
                }
            }
        });
        arrayAdapter1 = new ArrayAdapter(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, fromLoc);
        arrayAdapter2 = new ArrayAdapter(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, fromLoc);
        fromAuto.setThreshold(1);
        fromAuto.setAdapter(arrayAdapter1);
        toAuto.setThreshold(1);
        toAuto.setAdapter(arrayAdapter2);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        button =findViewById(R.id.btnLogout);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getApplicationContext(),AddContact.class);
                startActivity(i);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                mAuth.signOut();
                progressDialog.cancel();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        safezone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SafeZoneActivity.class);
                startActivity(intent);
            }
        });
    }

    public void upload(String from, String to) {
        JSONObject post = new JSONObject();
        try{
            post.put("num", uid);
            post.put("from",from);
            post.put("to",to);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Url+"/addRoute", post, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try{
                    Intent intent = new Intent(ProfileActivity.this, LiveLocation.class);
                    String fromlat = response.getString("fromlat");
                    String fromlong = response.getString("fromlong");
                    String tolat = response.getString("tolat");
                    String tolong = response.getString("tolong");
                    intent.putExtra("fromlat",fromlat);
                    intent.putExtra("fromlong", fromlong);
                    intent.putExtra("tolat", tolat);
                    intent.putExtra("tolong",tolong);
                    startActivity(intent);
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
    }
}
