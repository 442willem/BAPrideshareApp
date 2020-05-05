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

            spEditor.putBoolean("tijdenVerandert",false).apply();
        }

        //saving all the route info and going to vertrektijd activity to choose a date
        vertrektijd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spEditor.putBoolean("searching",true).apply();

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

                spEditor.putBoolean("searching",true).apply();

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
                spEditor.putBoolean("searched",true).apply();
                Intent cancel = new Intent(activity_search_route.this,MainActivity.class);
                startActivity(cancel);
            }
        });


        //saving all the values for the search route query
        searchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String beginpunt = vertrek.getText().toString();
                String eindpunt = aankomst.getText().toString();
                String begintijd = vertrektijdString.getText().toString();
                String eindtijd = aankomsttijdString.getText().toString();


                if(beginpunt == null || eindpunt == null || begintijd == null || eindtijd == null){
                    Toast.makeText(activity_search_route.this,"Not everything is chosen",Toast.LENGTH_SHORT).show();
                }
                else {

                    spEditor.putString("vertrek",vertrek.getText().toString()).apply();
                    spEditor.putString("aankomst",aankomst.getText().toString()).apply();
                    spEditor.putString("eindtijd",aankomsttijdString.getText().toString()).apply();
                    spEditor.putString("vertrektijd",vertrektijdString.getText().toString()).apply();


                    sp= getSharedPreferences("settings",MODE_PRIVATE);

                    final String ACCESS_TOKEN = sp.getString("Token",null);
                    final RequestQueue requestQueue = Volley.newRequestQueue(activity_search_route.this);


                    spEditor.putBoolean("searched",true).apply();

                    Intent created = new Intent(activity_search_route.this,activity_searching_routes.class);
                    startActivity(created);

                }
            }
        });



    }

}
