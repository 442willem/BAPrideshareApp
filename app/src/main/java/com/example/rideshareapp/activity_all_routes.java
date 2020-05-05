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
    List<Route> alleRoutes=new ArrayList<>();
    Button buttonBack;
    Button buttonRefresh;
    TextView error;

    List<Route> routeList;
    Gson json;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_routes);

        sp= getSharedPreferences("settings", Context.MODE_PRIVATE);
        spEditor=sp.edit();

        buttonBack = findViewById(R.id.button_alleRouten_back);
        buttonRefresh = findViewById(R.id.button_alleRouten_refresh);
        listViewAllRoutes = findViewById(R.id.listViewAllRoutes);

        final String ACCESS_TOKEN = sp.getString("Token",null);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);


        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .encodedAuthority("192.168.1.39:8080")
                .appendPath("G4REST")
                .appendPath("restApp")
                .appendPath("route_service")
                .appendPath("alleRoutes");

        final String url = uriBuilder.build().toString();

         json = new Gson();

        routeList=new ArrayList<>();

        JsonArrayRequest requestAllRoutes = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(org.json.JSONArray response) {
                Gson json = new Gson();
                if(response.length()==0) Toast.makeText(activity_all_routes.this,"There are no routes to show",Toast.LENGTH_LONG);
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
            public void onErrorResponse(VolleyError errore) {
                error.setText(errore.toString());
            }
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
