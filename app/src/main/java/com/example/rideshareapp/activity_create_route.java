package com.example.rideshareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class activity_create_route extends AppCompatActivity {

    EditText vertrek;
    EditText aankomst;

    Button vertrektijd;
    Button eindtijd;

    Button cancel;
    Button createRoute;

    EditText aantalPersonen;

    TextView vertrektijdString;
    TextView aankomsttijdString;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    Gson json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        sp= getSharedPreferences("createRoute",MODE_PRIVATE);
        spEditor=sp.edit();


        vertrek = (EditText) findViewById(R.id.editText_createRoute_beginpunt);
        aankomst = (EditText) findViewById(R.id.editText_createRoute_aankomstpunt);
        vertrektijd = (Button) findViewById(R.id.btn_createRoute_begintijd);
        eindtijd = (Button) findViewById(R.id.btn_createRoute_eindtijd);
        aantalPersonen = (EditText) findViewById(R.id.editText_createRoute_passengers);

        cancel = (Button) findViewById(R.id.btn_createRoute_cancel);
        createRoute = (Button) findViewById(R.id.btn_createRoute);

        vertrektijdString = (TextView) findViewById(R.id.tv_createRoute_begintijd);
        aankomsttijdString = (TextView) findViewById(R.id.tv_createRoute_eindtijd);

        //checking if the previous route was created or cancelled
        if(sp.getBoolean("created",false)){
            spEditor.clear().apply();
        }
        //if the user choose a date then fill the other data back in
        else if(sp.getBoolean("tijdenVerandert",false)){
            vertrek.setText(sp.getString("vertrek",""));
            aankomst.setText(sp.getString("aankomst",""));
            vertrektijdString.setText(sp.getString("vertrektijd",""));
            aankomsttijdString.setText(sp.getString("eindtijd",""));
            vertrek.setText(sp.getString("vertrek",""));
            spEditor.putBoolean("tijdenVerandert",false).apply();
        }

        //saving all the route info and going to vertrektijd activity to choose a date
        vertrektijd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spEditor.putString("vertrek",vertrek.getText().toString()).apply();
                spEditor.putString("aankomst",aankomst.getText().toString()).apply();
                spEditor.putString("aantalPersonen",aantalPersonen.getText().toString()).apply();
                spEditor.putString("eindtijd",aankomsttijdString.getText().toString()).apply();

                Intent vertrektijd = new Intent(activity_create_route.this, activity_vertrektijd.class);
                startActivity(vertrektijd);
            }
        });

        //saving all the route info and going to eindtijd activity to choose a date
        eindtijd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spEditor.putString("vertrek",vertrek.getText().toString()).apply();
                spEditor.putString("aankomst",aankomst.getText().toString()).apply();
                spEditor.putString("aantalPersonen",aantalPersonen.getText().toString()).apply();
                spEditor.putString("vertrektijd",vertrektijdString.getText().toString()).apply();

                Intent eindtijd = new Intent(activity_create_route.this, activity_eindtijd.class);
                startActivity(eindtijd);
            }
        });

        //cancel button to go back to main menu
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spEditor.putBoolean("created",true);
                Intent cancel = new Intent(activity_create_route.this,MainActivity.class);
                startActivity(cancel);
            }
        });


        createRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String beginpunt = vertrek.getText().toString();
                String eindpunt = aankomst.getText().toString();
                String begintijd = vertrektijdString.getText().toString();
                String eindtijd = aankomsttijdString.getText().toString();
                String maxPersonen = aantalPersonen.getText().toString();

                if(beginpunt == null || eindpunt == null || begintijd == null || eindtijd == null){
                    Toast.makeText(activity_create_route.this,"Not everything is chosen",Toast.LENGTH_SHORT).show();
                }
                else {
                    sp= getSharedPreferences("settings",MODE_PRIVATE);

                    final String ACCESS_TOKEN = sp.getString("Token",null);
                    final RequestQueue requestQueue = Volley.newRequestQueue(activity_create_route.this);


                    Uri.Builder uriBuilder = new Uri.Builder();
                    uriBuilder.scheme("http")
                            .encodedAuthority("192.168.0.184:8080")
                            .appendPath("G4REST")
                            .appendPath("restApp")
                            .appendPath("route_service")
                            .appendPath("createRoute")
                            .appendPath(sp.getString("login",null));

                    final String url = uriBuilder.build().toString();

                    json = new Gson();

                    Route route = new Route();
                    route.setBeginpunt(beginpunt);
                    route.setEindpunt(eindpunt);

                    Timestamp timestampEindtijd;
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        Date parsedDateEind = dateFormat.parse(eindpunt);
                        timestampEindtijd = new java.sql.Timestamp(parsedDateEind.getTime());
                    } catch(Exception e) {
                        Log.e("timestampError",e.toString());
                        timestampEindtijd =null;
                    }
                    route.setEindtijd(timestampEindtijd);

                    Timestamp timestampVertrektijd;
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        Date parsedDateVertrek = dateFormat.parse(eindpunt);
                        timestampVertrektijd = new java.sql.Timestamp(parsedDateVertrek.getTime());
                    } catch(Exception e) {
                        Log.e("timestampError",e.toString());
                        timestampVertrektijd =null;
                    }
                    route.setEindtijd(timestampVertrektijd);
                    //route.setMaxPersonen(maxPersonen);


                    JsonObjectRequest requestAllRoutes = new JsonObjectRequest(Request.Method.POST, url,null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(org.json.JSONObject response) {
                            Gson json = new Gson();
                            Log.d("CreateRoute",response.toString());
                            }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError errore) {
                            Toast.makeText(activity_create_route.this,"An error happened while creating your route",Toast.LENGTH_LONG).show();
                        }
                    }) {
                        //authorization header
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json; charset=UTF-8");
                            params.put("Authorization", ACCESS_TOKEN);
                            return params;
                        }};

                    requestQueue.add(requestAllRoutes);

                }
            }
        });



    }


}
