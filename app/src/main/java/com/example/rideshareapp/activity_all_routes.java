package com.example.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class activity_all_routes extends AppCompatActivity {

    ListView listViewAllRoutes;

    Button buttonRefresh;

    String url;
    String ACCESS_TOKEN;
    RequestQueue requestQueue;

    List<Route> routeList;
    Gson json;

    RouteListAdapter adapter ;


            SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_routes);

        sp= getSharedPreferences("settings", Context.MODE_PRIVATE);
        spEditor=sp.edit();

        buttonRefresh = findViewById(R.id.button_alleRouten_refresh);
        listViewAllRoutes = findViewById(R.id.listViewAllRoutes);
        routeList=new ArrayList<>();

        adapter = new RouteListAdapter(this,R.layout.adapter_view_layout,routeList);

         ACCESS_TOKEN = sp.getString("Token",null);
         requestQueue = Volley.newRequestQueue(this);


        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .encodedAuthority("192.168.0.184:8080")
                .appendPath("G4REST")
                .appendPath("restApp")
                .appendPath("route_service")
                .appendPath("alleRoutes");

        url = uriBuilder.build().toString();

         json = new Gson();


        listViewAllRoutes.setAdapter(adapter);
        listViewAllRoutes.setOnItemClickListener((parent, view, position, id) -> {

            Log.d("positie", String.valueOf(position));
            Route selectedItem = adapter.getItem(position);

            Log.d("geselecteerde", String.valueOf(selectedItem));

            Intent myIntent = new Intent(view.getContext(), activity_route.class);
            myIntent.putExtra("route", selectedItem);

            startActivity(myIntent);
        });

        refreshList();


        buttonRefresh.setOnClickListener(v -> refreshList());

    }

    private void refreshList(){

        JsonArrayRequest requestAllRoutes = new JsonArrayRequest(Request.Method.GET, url,null, response -> {
            Gson json = new Gson();
            if(response.length()==0) Toast.makeText(activity_all_routes.this,"There are no routes to show",Toast.LENGTH_LONG).show();
            else {
                for(int i=0;i<response.length();i++) {
                    Route r = new Route();
                    try {
                        r.setProfiel();
                        r=json.fromJson(response.getJSONObject(i).toString(),Route.class);
                        adapter.add(r);
                        adapter.notifyDataSetChanged();
                        Log.d("Route",r.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Route","added");

                }
            }
        }, error ->  {
            Log.d("Route",error.toString());
            Toast.makeText(activity_all_routes.this,"there was an error getting the routes",Toast.LENGTH_SHORT).show();
        }) {
            //authorization header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", ACCESS_TOKEN);
                return params;
            }};

        requestQueue.add(requestAllRoutes);


    }

    private void goToMainActivity(){
        Intent main = new Intent(this,MainActivity.class);
        startActivity(main);
    }

}
