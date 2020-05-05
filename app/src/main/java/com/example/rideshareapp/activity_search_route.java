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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class activity_search_route extends AppCompatActivity {

    EditText vertrek;
    EditText aankomst;

    Button vertrektijd;
    Button eindtijd;

    Button cancel;
    Button searchRoute;

    TextView vertrektijdString;
    TextView aankomsttijdString;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    JSONObject route;

    Gson json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_route);

        sp= getSharedPreferences("searchRoute",MODE_PRIVATE);
        spEditor=sp.edit();


        vertrek = (EditText) findViewById(R.id.editText_searchRoute_beginpunt);
        aankomst = (EditText) findViewById(R.id.editText_searchRoute_aankomstpunt);
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
                spEditor.putString("eindtijd",aankomsttijdString.getText().toString()).apply();

                Intent vertrektijd = new Intent(activity_search_route.this, activity_vertrektijd.class);
                startActivity(vertrektijd);
            }
        });

        //saving all the route info and going to eindtijd activity to choose a date
        eindtijd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spEditor.putString("vertrek",vertrek.getText().toString()).apply();
                spEditor.putString("aankomst",aankomst.getText().toString()).apply();
                spEditor.putString("vertrektijd",vertrektijdString.getText().toString()).apply();

                Intent eindtijd = new Intent(activity_search_route.this, activity_eindtijd.class);
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


        //sending JSON object to server for creating route
        searchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String beginpunt = vertrek.getText().toString();
                String eindpunt = aankomst.getText().toString();
                String begintijd = vertrektijdString.getText().toString();
                String eindtijd = aankomsttijdString.getText().toString();


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
