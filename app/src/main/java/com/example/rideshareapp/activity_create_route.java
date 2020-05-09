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
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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
import java.util.Locale;
import java.util.Map;

public class activity_create_route extends AppCompatActivity {


    Button vertrektijd;
    Button eindtijd;


    Button createRoute;

    String beginpunt;
    String eindpunt;

    EditText aantalPersonen;

    TextView vertrektijdString;
    TextView aankomsttijdString;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    JSONObject route;

    Gson json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        sp= getSharedPreferences("createRoute",MODE_PRIVATE);
        spEditor=sp.edit();

        String apiKey = getString(R.string.google_maps_key);
        String TAG="autocompleetfragments";



        vertrektijd = (Button) findViewById(R.id.btn_createRoute_begintijd);
        eindtijd = (Button) findViewById(R.id.btn_createRoute_eindtijd);
        aantalPersonen = (EditText) findViewById(R.id.editText_createRoute_passengers);

        createRoute = (Button) findViewById(R.id.btn_createRoute);

        vertrektijdString = (TextView) findViewById(R.id.tv_createRoute_begintijd);
        aankomsttijdString = (TextView) findViewById(R.id.tv_createRoute_eindtijd);

        //checking if the previous route was created or cancelled
        if(sp.getBoolean("created",false)){
            spEditor.clear().apply();
        }
        //if the user choose a date then fill the other data back in
        else if(sp.getBoolean("tijdenVerandert",false)){

            vertrektijdString.setText(sp.getString("vertrektijd",""));
            aankomsttijdString.setText(sp.getString("eindtijd",""));

            aantalPersonen.setText(sp.getString("aantalPersonen",""));
            spEditor.putBoolean("tijdenVerandert",false).apply();
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey, Locale.forLanguageTag("en"));
        }
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentCreateRouteBegin);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                beginpunt=place.getAddress();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+", "+place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

                Log.i(TAG, "An error occurred: " + status);
            }
        });

        AutocompleteSupportFragment autocompleteFragment1 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentCreateRouteEnd);
        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS));
        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                eindpunt=place.getAddress();
                Log.i(TAG, "Place: " + place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //saving all the route info and going to vertrektijd activity to choose a date
        vertrektijd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                spEditor.putString("aantalPersonen",aantalPersonen.getText().toString()).apply();
                spEditor.putString("vertrektijd",vertrektijdString.getText().toString()).apply();

                Intent eindtijd = new Intent(activity_create_route.this, activity_eindtijd.class);
                startActivity(eindtijd);
            }
        });



        //sending JSON object to server for creating route
        createRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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

                     route = new JSONObject();

                    int maxPers = 0;

                    try {
                        maxPers = Integer.parseInt(maxPersonen);
                    } catch(NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }

                    try {
                        route.put("beginpunt",beginpunt);
                        route.put("eindpunt",eindpunt);
                        route.put("maxPersonen",maxPers);
                        route.put("eindtijdString",eindtijd);
                        route.put("vertrektijdString",begintijd);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JsonObjectRequest requestAllRoutes = new JsonObjectRequest(Request.Method.POST, url,route, new Response.Listener<JSONObject>() {
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

                    spEditor.putBoolean("created",true);
                    spEditor.putBoolean("succesCreate",true);
                    Intent created = new Intent(activity_create_route.this,MainActivity.class);
                    startActivity(created);

                }
            }
        });



    }

}
