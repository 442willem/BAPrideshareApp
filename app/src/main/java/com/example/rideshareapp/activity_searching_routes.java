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

    Button buttonBack;
    Button buttonRefresh;

    List<Route> routeList;
    Gson json;

    String beginpunt;
    String eindpunt;
    String begintijd;
    String eindtijd;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_routes);

        sp= getSharedPreferences("searchRoute",MODE_PRIVATE);


        beginpunt=sp.getString("vertrek","");
        eindpunt=sp.getString("aankomst","");

        begintijd=sp.getString("vertrektijd","");
        eindtijd=sp.getString("eindtijd","");


        sp= getSharedPreferences("settings", Context.MODE_PRIVATE);
        spEditor=sp.edit();

        buttonBack = findViewById(R.id.button_searchRoutes_back);
        buttonRefresh = findViewById(R.id.button_searchRoutes_refresh);
        listViewAllRoutes = findViewById(R.id.listViewSearchingRoutes);

        final String ACCESS_TOKEN = sp.getString("Token",null);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);


        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .encodedAuthority("192.168.0.184:8080")
                .appendPath("G4REST")
                .appendPath("restApp")
                .appendPath("route_service")
                .appendPath("searchRoutes")
                .appendQueryParameter("beginpunt",beginpunt)
                .appendQueryParameter("eindpunt",eindpunt)
                .appendQueryParameter("begintijd",begintijd)
                .appendQueryParameter("eindtijd",eindtijd);

        final String url = uriBuilder.build().toString();

        json = new Gson();

        routeList=new ArrayList<>();

        JsonArrayRequest requestAllRoutes = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(org.json.JSONArray response) {
                Gson json = new Gson();
                if(response.length()==0) Toast.makeText(activity_searching_routes.this,"There are no routes to show",Toast.LENGTH_LONG).show();
                else {
                    for(int i=0;i<response.length();i++) {
                        Route r = new Route();
                        try {
                            r.setProfiel();
                            r=json.fromJson(response.getJSONObject(i).toString(),Route.class);
                            Log.d("Route",r.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Route","added");
                        routeList.add(r);

                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Toast.makeText(activity_searching_routes.this,"erorr:"+error.toString(),Toast.LENGTH_SHORT).show();
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
        Log.d("Route", "grootte: "+routeList.size());
        showList();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList();
            }
        });

    }

    public void showList(){
        RouteListAdapter adapter = new RouteListAdapter(this,R.layout.adapter_view_layout,routeList);
        listViewAllRoutes.setAdapter(adapter);
        listViewAllRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("positie", String.valueOf(position));
                Route selectedItem = routeList.get(position);
                Log.d("geselecteerde", String.valueOf(selectedItem));
                Intent myIntent = new Intent(view.getContext(), activity_route.class);
                myIntent.putExtra("route", selectedItem);
                startActivity(myIntent);
            }
        });
    }

    private void goToMainActivity(){
        Intent main = new Intent(this,MainActivity.class);
        startActivity(main);
    }
}
