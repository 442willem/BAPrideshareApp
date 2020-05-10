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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Register_Activity extends AppCompatActivity {

    //visual elements
    private EditText textVoornaam;
    private EditText textAchternaam;
    private EditText textPaypal;
    private EditText textUsernameRegister;
    private EditText textPasswordRegister;
    private TextView textViewErrorRegister;
    private Button buttonRegister;





    private  SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private JSONObject profiel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);

        textVoornaam= findViewById(R.id.editText_voornaam);
        textAchternaam= findViewById(R.id.editText_achternaam);
        textPaypal= findViewById(R.id.editText_paypalemail);
        textUsernameRegister= findViewById(R.id.editText_login_register);
        textPasswordRegister= findViewById(R.id.editText_password_register);
        textViewErrorRegister= findViewById(R.id.textView_errorRegister);

        buttonRegister= findViewById(R.id.button_register);

        profiel = new JSONObject();

        sp= getSharedPreferences("registratie",MODE_PRIVATE);
        spEditor=sp.edit();


        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .encodedAuthority("192.168.1.8:8080")
                .appendPath("G4REST")
                .appendPath("restApp")
                .appendPath("authentication")
                .appendPath("registratie");

        final String url = uriBuilder.build().toString();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                try{
                    profiel.put("login", textUsernameRegister.getText().toString());
                    profiel.put("password", textPasswordRegister.getText().toString());
                    profiel.put("achternaam", textAchternaam.getText().toString());
                    profiel.put("voornaam", textVoornaam.getText().toString());
                    profiel.put("paypalemail", textPaypal.getText().toString());
                }catch (JSONException e) {
                    e.printStackTrace();
                }


                JsonObjectRequest createProfiel = new JsonObjectRequest(Request.Method.POST, url,profiel,
                        new Response.Listener<org.json.JSONObject>() {
                             @Override
                             public void onResponse(org.json.JSONObject response) {
                                 spEditor.putString("login",textVoornaam.getText().toString()).apply();
                                 spEditor.putString("password",textPasswordRegister.getText().toString()).apply();
                                 spEditor.putBoolean("registered",true).apply();
                                  goToLoginActivity();
                              }
                         }, new Response.ErrorListener() {
                               @Override
                                 public void onErrorResponse(VolleyError error) {
                                   Log.d("registerError",error.toString());
                                   textViewErrorRegister.setText(error.toString());
                               }
                });

                requestQueue.add(createProfiel);
            }
        });

    }

    private void goToLoginActivity(){
        Intent login = new Intent(Register_Activity.this, Login_Activity.class);
        startActivity(login);
    }


}
