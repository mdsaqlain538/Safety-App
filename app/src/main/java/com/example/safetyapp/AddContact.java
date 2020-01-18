package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddContact extends AppCompatActivity {
    String[] str = {"", "", "", ""};

    EditText cnt1, cnt2, cnt3, cnt4;
    Button add;
    TextView text;
    String Url = "https://radiant-hamlet-85497.herokuapp.com";
    String uid;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        text = findViewById(R.id.text);

        cnt1 = findViewById(R.id.cnt_1);
        cnt2 = findViewById(R.id.cnt_2);
        cnt3 = findViewById(R.id.cnt_3);
        cnt4 = findViewById(R.id.cnt_4);

        requestQueue= Volley.newRequestQueue(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);

        add = findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                str[0] = cnt1.getText().toString();
                str[1] = cnt2.getText().toString();
                str[2] = cnt3.getText().toString();
                str[3] = cnt4.getText().toString();

                if (str[0].length() < 1 || str[1].length() < 1 || str[2].length() < 1 || str[3].length() < 1) {
                    Toast.makeText(AddContact.this, "dhjhk", Toast.LENGTH_SHORT).show();
                } else {
                    upload();
                }

            }
        });
    }

    public void upload() {
        JSONObject post = new JSONObject();
        try{
            post.put("num", uid);
            post.put("contact1", str[0]);
            post.put("contact2", str[1]);
            post.put("contact3", str[2]);
            post.put("contact4", str[3]);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Url+"/safetywomen/addcontacts/", post, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                String msg = null;
                try{
                    msg = (String) response.get("message");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),""+msg, Toast.LENGTH_SHORT).show();
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