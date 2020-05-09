package com.example.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activity_passagiers extends AppCompatActivity {

    ListView listViewAllRoutes;

    Button buttonRefresh;



    Gson json;

    String ACCESS_TOKEN;
    RequestQueue requestQueue;
    String url;

    int routeID;
    Route route;

    List<Rit> ridesList;

    PassagierListAdapter adapter;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passagiers);

        route = (Route) getIntent().getSerializableExtra("route");
        routeID=route.getId();


        sp= getSharedPreferences("settings", Context.MODE_PRIVATE);


        buttonRefresh = findViewById(R.id.btn_passagiers_refresh);
        listViewAllRoutes = findViewById(R.id.listViewPassagiers);

        json = new Gson();
        ridesList=new ArrayList<>();

        adapter = new PassagierListAdapter(this, R.layout.adapter_view_passagier, ridesList);

        listViewAllRoutes.setAdapter(adapter);


        ACCESS_TOKEN = sp.getString("Token",null);
        requestQueue = Volley.newRequestQueue(this);

        //the different urls for every case : my routes , my rides, search rides
        Uri.Builder uriBuilder = new Uri.Builder();

        uriBuilder.scheme("http")
                .encodedAuthority("192.168.0.184:8080")
                .appendPath("G4REST")
                .appendPath("restApp")
                .appendPath("rit_service")
                .appendPath("getPassagiers")
                .appendPath(String.valueOf(routeID));

        url = uriBuilder.build().toString();


        listViewAllRoutes.setAdapter(adapter);
        listViewAllRoutes.setOnItemClickListener((parent, view, position, id) -> {

            Log.d("positie", String.valueOf(position));
            Rit selectedItem = adapter.getItem(position);

            Log.d("geselecteerde", String.valueOf(selectedItem));

            Intent myIntent = new Intent(view.getContext(), activity_viewrit.class);      //aanpassen nr nieuwe
            myIntent.putExtra("rit", selectedItem);

            startActivity(myIntent);
        });

        refreshList();

        buttonRefresh.setOnClickListener(v -> refreshList());

    }


    private void refreshList(){


        adapter.clear();
        ridesList.clear();

        JsonArrayRequest requestRides = new JsonArrayRequest(Request.Method.GET, url,null, response -> {
            Gson json = new Gson();
            Log.d("TestRoute",response.toString());
            if(response.length()==0) Toast.makeText(activity_passagiers.this,"There are no routes to show",Toast.LENGTH_LONG).show();
            else {
                for(int i=0;i<response.length();i++) {
                    try {
                        Rit r=json.fromJson(response.getJSONObject(i).toString(),Rit.class);
                        adapter.add(r);
                        adapter.notifyDataSetChanged();
                        Log.d("Rit",r.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Rit","added");

                }
            }
        }, error -> Toast.makeText(activity_passagiers.this,"erorr:"+error.toString(),Toast.LENGTH_SHORT).show()) {
            //authorization header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", ACCESS_TOKEN);
                return params;
            }};

        requestQueue.add(requestRides);
        Log.d("Rit", "grootte: "+ridesList.size());
    }

}
