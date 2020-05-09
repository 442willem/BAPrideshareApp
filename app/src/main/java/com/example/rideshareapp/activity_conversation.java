package com.example.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activity_conversation extends AppCompatActivity {
    String ACCESS_TOKEN;
    RequestQueue requestQueue;
    String url;

    Profiel otherPerson;
    String username;
    String idOther;

    JSONObject messageObject;


    MessageListAdapter adapter;

    List<Bericht> berichtList;

    Button buttonSendMessage;

    EditText editTextMessage;

    ListView listViewConversation;

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        otherPerson = (Profiel) getIntent().getSerializableExtra("otherPerson");
        idOther = String.valueOf(otherPerson.getId());

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);

        buttonSendMessage=      findViewById(R.id.btn_conversation_send);
        editTextMessage=        findViewById(R.id.et_conversation_message);
        listViewConversation =  findViewById(R.id.listViewConversation);

        berichtList=new ArrayList<>();

        adapter = new MessageListAdapter(this,R.layout.adapter_view_message,berichtList);

        listViewConversation.setAdapter(adapter);

        username=sp.getString("login",null);
        ACCESS_TOKEN = sp.getString("Token", null);
        requestQueue = Volley.newRequestQueue(this);


        refreshList();


        listViewConversation.setSelection(adapter.getCount() - 1);

        buttonSendMessage.setOnClickListener(v -> {


            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http")
                    .encodedAuthority("192.168.0.184:8080")
                    .appendPath("G4REST")
                    .appendPath("restApp")
                    .appendPath("bericht_service")
                    .appendPath("createBericht")
                    .appendPath(username)
                    .appendPath(otherPerson.getLogin());

            url = uriBuilder.build().toString();

            messageObject=new JSONObject();
            try{
                messageObject.put("content",editTextMessage.getText().toString());
                editTextMessage.setText("");

            }catch (JSONException e){
                Log.e("MessageJSONException",e.toString());
            }


            JsonObjectRequest createBericht = new JsonObjectRequest(Request.Method.POST, url,messageObject,
                    response -> Log.d("message","gelukt"), error -> Log.d("messageError",error.toString())){
                //authorization header
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", ACCESS_TOKEN);
                    return params;
                }};

            requestQueue.add(createBericht);
            requestQueue.start();
            refreshList();
        });

    }


    private void refreshList(){

        adapter.clear();
        berichtList.clear();

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .encodedAuthority("192.168.0.184:8080")
                .appendPath("G4REST")
                .appendPath("restApp")
                .appendPath("bericht_service")
                .appendPath("myMessages")
                .appendPath(username)
                .appendPath(idOther);

        url = uriBuilder.build().toString();

        JsonArrayRequest requestAllRoutes = new JsonArrayRequest(Request.Method.GET, url,null, response -> {
            Gson json = new Gson();
            Log.d("TestConversation",response.toString());
            if(response.length()==0) Toast.makeText(activity_conversation.this,"There are no messages to show",Toast.LENGTH_LONG).show();
            else {
                for(int i=0;i<response.length();i++) {
                    try {
                        Bericht p=json.fromJson(response.getJSONObject(i).toString(),Bericht.class);
                        adapter.add(p);
                        adapter.notifyDataSetChanged();
                        Log.d("Bericht",p.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Bericht","added");

                }
            }
        }, error -> Toast.makeText(activity_conversation.this,"erorr:"+error.toString(),Toast.LENGTH_SHORT).show()) {
            //authorization header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", ACCESS_TOKEN);
                return params;
            }};

        requestQueue.add(requestAllRoutes);

    }
}
