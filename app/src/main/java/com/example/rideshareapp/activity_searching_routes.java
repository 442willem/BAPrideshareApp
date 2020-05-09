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
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activity_searching_routes extends AppCompatActivity {


    ListView listViewAllRoutes;

    Button buttonRefresh;

    TextView welcomeMessage;

    Gson json;

    String ACCESS_TOKEN;
    RequestQueue requestQueue;
    String url;

    String beginpunt;
    String eindpunt;
    String begintijd;
    String eindtijd;
    String username;

    List<Route> routeList;

    RouteListAdapter adapter;

    int soortZoeken;
    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_routes);

        //kijken voor wat de routen list gebruikt moet worden
        sp = getSharedPreferences("list",MODE_PRIVATE);
        soortZoeken = sp.getInt("soort",-1);

        if(soortZoeken == 0) {
            sp = getSharedPreferences("searchRoute", MODE_PRIVATE);


            beginpunt = sp.getString("vertrek", "");
            eindpunt = sp.getString("aankomst", "");

            begintijd = sp.getString("vertrektijd", "");
            eindtijd = sp.getString("eindtijd", "");
        }

        sp= getSharedPreferences("settings", Context.MODE_PRIVATE);
        spEditor=sp.edit();

        username = sp.getString("login",null);

        welcomeMessage=findViewById(R.id.textView_welcomeRouteList);

        buttonRefresh = findViewById(R.id.button_searchRoutes_refresh);
        listViewAllRoutes = findViewById(R.id.listViewSearchingRoutes);

        json = new Gson();
        routeList=new ArrayList<>();

        adapter = new RouteListAdapter(this, R.layout.adapter_view_layout, routeList);

        listViewAllRoutes.setAdapter(adapter);

        listViewAllRoutes.setOnItemClickListener((parent, view, position, id) -> {

            Log.d("positie", String.valueOf(position));
            Route selectedItem = routeList.get(position);

            Log.d("geselecteerde", String.valueOf(selectedItem));

            Intent myIntent = new Intent(view.getContext(), activity_route.class);
            myIntent.putExtra("route", selectedItem);
            startActivity(myIntent);
        });


         ACCESS_TOKEN = sp.getString("Token",null);
         requestQueue = Volley.newRequestQueue(this);

        //the different urls for every case : my routes , my rides, search rides
        Uri.Builder uriBuilder = new Uri.Builder();
        if(soortZoeken == 1){
            uriBuilder.scheme("http")
                    .encodedAuthority("192.168.0.184:8080")
                    .appendPath("G4REST")
                    .appendPath("restApp")
                    .appendPath("route_service")
                    .appendPath("myRoute")
                    .appendPath(username);

            welcomeMessage.setText("My Routes");
        }
        else {
            uriBuilder.scheme("http")
                    .encodedAuthority("192.168.0.184:8080")
                    .appendPath("G4REST")
                    .appendPath("restApp")
                    .appendPath("route_service")
                    .appendPath("searchRoutes")
                    .appendQueryParameter("beginpunt", beginpunt)
                    .appendQueryParameter("eindpunt", eindpunt)
                    .appendQueryParameter("begintijd", begintijd)
                    .appendQueryParameter("eindtijd", eindtijd);

            welcomeMessage.setText("Filtered Routes");
        }

        url = uriBuilder.build().toString();

        listViewAllRoutes.setAdapter(adapter);
        listViewAllRoutes.setOnItemClickListener((parent, view, position, id) -> {

            Log.d("positie", String.valueOf(position));
            Route selectedItem = adapter.getItem(position);

            Log.d("geselecteerde", String.valueOf(selectedItem));

            Intent myIntent = new Intent(view.getContext(), activity_myroute.class);
            myIntent.putExtra("route", selectedItem);

            startActivity(myIntent);
        });

        refreshList();


        buttonRefresh.setOnClickListener(v -> refreshList());

    }

    private void refreshList(){

        JsonArrayRequest requestAllRoutes = new JsonArrayRequest(Request.Method.GET, url,null, response -> {
            Gson json = new Gson();
            Log.d("TestRoute",response.toString());
            if(response.length()==0) Toast.makeText(activity_searching_routes.this,"There are no routes to show",Toast.LENGTH_LONG).show();
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
        }, error -> Toast.makeText(activity_searching_routes.this,"erorr:"+error.toString(),Toast.LENGTH_SHORT).show()) {
            //authorization header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", ACCESS_TOKEN);
                return params;
            }};

        requestQueue.add(requestAllRoutes);
        Log.d("Route", "grootte: "+routeList.size());
    }

    private void goToMainActivity(){
        Intent main = new Intent(this,MainActivity.class);
        startActivity(main);
    }
}
