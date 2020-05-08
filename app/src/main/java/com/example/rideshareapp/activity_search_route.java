package com.example.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class activity_search_route extends AppCompatActivity {


    Button vertrektijd;
    Button eindtijd;

    Button cancel;
    Button searchRoute;

    TextView vertrektijdString;
    TextView aankomsttijdString;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_route);

        sp= getSharedPreferences("searchRoute",MODE_PRIVATE);
        spEditor=sp.edit();

        String apiKey = getString(R.string.google_maps_key);
        String TAG="autocompleetfragments";


        vertrektijd = (Button) findViewById(R.id.btn_searchRoute_begintijd);
        eindtijd = (Button) findViewById(R.id.btn_searchRoute_eindtijd);

        cancel = (Button) findViewById(R.id.btn_searchRoute_cancel);
        searchRoute = (Button) findViewById(R.id.btn_searchRoute);

        vertrektijdString = (TextView) findViewById(R.id.tv_searchRoute_begintijd);
        aankomsttijdString = (TextView) findViewById(R.id.tv_searchRoute_eindtijd);

        //checking if the previous route was created or cancelled
        if(sp.getBoolean("searched",false)){
            spEditor.clear().apply();
        }

        //if the user choose a date then fill the other data back in
        else if(sp.getBoolean("tijdenVerandert",false)){


            vertrektijdString.setText(sp.getString("vertrektijd",null));
            aankomsttijdString.setText(sp.getString("eindtijd",null));

            spEditor.putBoolean("tijdenVerandert",false).apply();
        }



        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey, Locale.forLanguageTag("en"));
        }
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentSearchRouteBegin);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                spEditor.putString("vertrek",place.getAddress()).apply();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+", "+place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

                Log.i(TAG, "An error occurred: " + status);
            }
        });

        AutocompleteSupportFragment autocompleteFragment1 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentSearchRouteEnd);
        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS));
        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                spEditor.putString("aankomst",place.getAddress()).apply();
                Log.i(TAG, "Place: " + place.getAddress() + ", " + place.getId()+", "+place.getAddress());
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
                spEditor.putBoolean("searching",true).apply();
                spEditor.putString("eindtijd",aankomsttijdString.getText().toString()).apply();

                Intent vertrektijd = new Intent(activity_search_route.this, activity_vertrektijd.class);
                startActivity(vertrektijd);
            }
        });

        //saving all the route info and going to eindtijd activity to choose a date
        eindtijd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spEditor.putBoolean("searching",true).apply();
                spEditor.putString("vertrektijd",vertrektijdString.getText().toString()).apply();

                Intent eindtijd = new Intent(activity_search_route.this, activity_eindtijd.class);
                startActivity(eindtijd);
            }
        });

        //cancel button to go back to main menu
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spEditor.putBoolean("searched",true).apply();
                Intent cancel = new Intent(activity_search_route.this,MainActivity.class);
                startActivity(cancel);
            }
        });


        //saving all the values for the search route query
        searchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spEditor.putString("eindtijd",aankomsttijdString.getText().toString()).apply();
                spEditor.putString("vertrektijd",vertrektijdString.getText().toString()).apply();


                    sp= getSharedPreferences("settings",MODE_PRIVATE);

                    spEditor.putBoolean("searched",true).apply();

                    Intent created = new Intent(activity_search_route.this,activity_searching_routes.class);
                    startActivity(created);

            }
        });



    }

}
