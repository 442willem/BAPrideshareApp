package com.example.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class activity_conversation_menu extends AppCompatActivity {

    String ACCESS_TOKEN;
    RequestQueue requestQueue;
    String url;


    ConversationsListAdapter adapter;

    List<Profiel> profielList;

    Button buttonRefresh;


    ListView listViewAllConversations;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_menu);

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);


        username = sp.getString("login", null);

        buttonRefresh = findViewById(R.id.button_MyConversations_refresh);
        listViewAllConversations = findViewById(R.id.listViewMyConversations);

        profielList=new ArrayList<>();

        adapter = new ConversationsListAdapter(this,R.layout.conversationprofiel_layout,profielList);

        listViewAllConversations.setAdapter(adapter);

        listViewAllConversations.setOnItemClickListener((parent, view, position, id) -> {

            Log.d("positie", String.valueOf(position));
            Profiel selectedItem = profielList.get(position);

            Log.d("geselecteerde", String.valueOf(selectedItem));

            Intent myIntent = new Intent(view.getContext(), activity_conversation.class);
            myIntent.putExtra("otherPerson", selectedItem);
            startActivity(myIntent);
        });


        ACCESS_TOKEN = sp.getString("Token", null);
        requestQueue = Volley.newRequestQueue(this);

        //the different urls for every case : my routes , my rides, search rides
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .encodedAuthority("192.168.1.8:8080")
                .appendPath("G4REST")
                .appendPath("restApp")
                .appendPath("bericht_service")
                .appendPath("myConversations")
                .appendPath(username);

        url = uriBuilder.build().toString();

        refreshList();


        buttonRefresh.setOnClickListener(v -> refreshList());

    }

    private void goToMainActivity() {
        Intent main = new Intent(this,MainActivity.class);
        startActivity(main);
    }


    private void refreshList(){


        adapter.clear();
        profielList.clear();

        JsonArrayRequest requestAllRoutes = new JsonArrayRequest(Request.Method.GET, url,null, response -> {
            Gson json = new Gson();
            Log.d("TestConversation",response.toString());
            if(response.length()==0) Toast.makeText(activity_conversation_menu.this,"There are no conversations to show",Toast.LENGTH_LONG).show();
            else {
                for(int i=0;i<response.length();i++) {
                    try {
                        Profiel p=json.fromJson(response.getJSONObject(i).toString(),Profiel.class);
                        adapter.add(p);
                        adapter.notifyDataSetChanged();
                        Log.d("Profiel",p.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Route","added");

                }
            }
        }, error -> Toast.makeText(activity_conversation_menu.this,"erorr:"+error.toString(),Toast.LENGTH_SHORT).show()) {
            //authorization header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", ACCESS_TOKEN);
                return params;
            }};

        requestQueue.add(requestAllRoutes);
        Log.d("Route", "grootte: "+profielList.size());
    }
}
