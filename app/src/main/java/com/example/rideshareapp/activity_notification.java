package com.example.rideshareapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.BigPictureStyle;
import androidx.core.app.NotificationCompat.BigTextStyle;
import androidx.core.app.NotificationCompat.InboxStyle;
import androidx.core.app.NotificationCompat.MessagingStyle;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activity_notification extends AppCompatActivity {

    ListView listViewAllNotifications;
    Button buttonRefresh;

    String url;
    String url2;
    String ACCESS_TOKEN;
    RequestQueue requestQueue;

    List<Notificatie> notificatieList;
    Gson json;

    String username;
    NotificatieListAdapter adapter;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaties);

        sp= getSharedPreferences("settings", Context.MODE_PRIVATE);
        spEditor=sp.edit();



        buttonRefresh = findViewById(R.id.button_alleNotificaties_refresh);
        listViewAllNotifications = findViewById(R.id.listViewAllNotificaties);
        notificatieList=new ArrayList<>();

        adapter = new NotificatieListAdapter(this,R.layout.adapter_view_notificaties,notificatieList);

        ACCESS_TOKEN = sp.getString("Token",null);
        requestQueue = Volley.newRequestQueue(this);
        username = sp.getString("login",null);


        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .encodedAuthority("192.168.0.184:8080")
                .appendPath("G4REST")
                .appendPath("restApp")
                .appendPath("notification_service")
                .appendPath("alleNotificaties")
                .appendPath(username);

        url = uriBuilder.build().toString();

        json = new Gson();


        listViewAllNotifications.setAdapter(adapter);
        listViewAllNotifications.setOnItemClickListener((parent, view, position, id) -> {

            Log.d("positie", String.valueOf(position));
            Notificatie selectedItem = adapter.getItem(position);

            Log.d("geselecteerde", String.valueOf(selectedItem));
            Log.d("type", selectedItem.getMessage());
            Log.d("id", String.valueOf(selectedItem.getId()));


            Uri.Builder uriBuilder2 = new Uri.Builder();
            uriBuilder2.scheme("http")
                    .encodedAuthority("192.168.0.184:8080")
                    .appendPath("G4REST")
                    .appendPath("restApp")
                    .appendPath("notification_service")
                    .appendPath("setGelezen")
                    .appendPath(String.valueOf(selectedItem.getId()));

            url2 = uriBuilder2.build().toString();

            JsonObjectRequest createProfiel = new JsonObjectRequest(Request.Method.POST, url2,null,
                    response -> Log.d("notificatie",response.toString()),
                    error -> Log.d("notificatieError",error.toString())){
                //authorization header
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", ACCESS_TOKEN);
                    return params;
                }};


            requestQueue.add(createProfiel);

            //per notificatie type een andere intent opzetten
            switch(selectedItem.getType()) {
                case "betaling":
                    Toast.makeText(activity_notification.this,"Payment happens on the website!",Toast.LENGTH_SHORT).show();
                    break;
                case "review":
                    Toast.makeText(activity_notification.this,"Reviews happen on the website!",Toast.LENGTH_SHORT).show();
                    break;
                case "routeHerinnering":
                    Intent myIntent = new Intent(view.getContext(), activity_searching_routes.class);
                    myIntent.putExtra("soort",1);
                    startActivity(myIntent);
                    break;
                case "ritHerinnering":
                    Intent myIntent5 = new Intent(view.getContext(), activity_rit_list.class);

                    startActivity(myIntent5);
                    break;
                case "ritAccepted":
                case "ritChange":
                    Intent myIntent2 = new Intent(view.getContext(), activity_viewrit_p.class);
                    myIntent2.putExtra("rit", selectedItem.getRit());
                    startActivity(myIntent2);
                    break;
                case "ritRequest":
                    Intent myIntent3 = new Intent(view.getContext(), activity_viewrit.class);
                    myIntent3.putExtra("rit", selectedItem.getRit());
                    startActivity(myIntent3);
                    break;
                case "bericht":
                    Intent myIntent4 = new Intent(view.getContext(), activity_conversation_menu.class);

                    startActivity(myIntent4);
                    break;
                default:
                    Toast.makeText(activity_notification.this,"Check your notifications!",Toast.LENGTH_SHORT).show();
            }


        });

        refreshList();




        buttonRefresh.setOnClickListener(v -> refreshList());

    }

    private void refreshList(){

        adapter.clear();
        notificatieList.clear();

        JsonArrayRequest requestAllNotoficaties = new JsonArrayRequest(Request.Method.GET, url,null, response -> {
            Gson json = new Gson();
            if(response.length()==0) Toast.makeText(activity_notification.this,"There are no Notifications to show",Toast.LENGTH_LONG).show();
            else {
                for(int i=0;i<response.length();i++) {
                    Notificatie r = new Notificatie();
                    try {
                        //r.setProfiel();
                        r=json.fromJson(response.getJSONObject(i).toString(),Notificatie.class);
                        adapter.add(r);
                        adapter.notifyDataSetChanged();
                        Log.d("Notificatie",r.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Notificatie","added");

                }
            }
        }, error ->  {
            Log.d("Notificatie",error.toString());
            Toast.makeText(activity_notification.this,"there was an error getting the notifications",Toast.LENGTH_SHORT).show();
        }) {
            //authorization header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", ACCESS_TOKEN);
                return params;
            }};

        requestQueue.add(requestAllNotoficaties);


    }





}
