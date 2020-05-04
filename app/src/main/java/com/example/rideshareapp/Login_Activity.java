package com.example.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
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


//connectie met server via volley
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;


public class Login_Activity extends AppCompatActivity {
    //visual elements
    EditText textUsername;
    EditText textPassword;
    Button buttonLogin;
    TextView textViewRegister;
    TextView textViewError;


    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login_);



                textUsername = findViewById(R.id.editText_username);
                textPassword = findViewById(R.id.editText_password);
                buttonLogin = findViewById(R.id.button_login);
                textViewRegister = findViewById(R.id.textView_register);
                textViewError = findViewById(R.id.textView_error);

                final RequestQueue requestQueue = Volley.newRequestQueue(this);

                sp = getSharedPreferences("registratie", MODE_PRIVATE);
                spEditor = sp.edit();

                if (sp.getBoolean("registered", false)) {
                    textUsername.setText(sp.getString("login", "registration username"));
                    textPassword.setText(sp.getString("password", "registration password"));
                    spEditor.putBoolean("registered", false).apply();
                    spEditor.putString("login", null).apply();
                    spEditor.putString("password", null).apply();

                }

                sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
                spEditor = sp.edit();


                //if user was previously loggin in, go directly to main screen
                if (sp.getBoolean("loggedIn", false)) {
                    goToMainActivity();
                }

                textViewRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent registerIntent = new Intent(Login_Activity.this, Register_Activity.class);
                        startActivity(registerIntent);
                    }
                });

                buttonLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String login = textUsername.getText().toString().trim();
                        final String password = textPassword.getText().toString().trim();

                        final String url = "http://192.168.0.184:8080/G4REST/restApp/authentication/login?login=" + login + "&password=" + password;

                        TokenRequest tokenRequest = new TokenRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                spEditor.putString("Token", response);
                                spEditor.putString("login", login);
                                spEditor.putString("password", password);
                                spEditor.putBoolean("loggedIn", true);
                                spEditor.apply();

                                goToMainActivity();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                textViewError.setText(error.toString());
                            }
                        });
                        requestQueue.add(tokenRequest);

                    }
            });
    }


    private void goToMainActivity(){
        Intent main = new Intent(Login_Activity.this,MainActivity.class);
        startActivity(main);
    }



}
