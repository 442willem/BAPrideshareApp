package com.example.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView textViewWelcome;

    Button buttonAllRoutes;
    Button buttonMyRoutes;
    Button buttonSearchRoutes;
    Button buttonLogout;
    Button buttonMyConversations;
    Button buttonCreateRoute;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAllRoutes = findViewById(R.id.button_AlleRouten);
        buttonMyRoutes = findViewById(R.id.button_mijnRouten);
        buttonSearchRoutes = findViewById(R.id.button_zoekRoutes);
        buttonLogout = findViewById(R.id.button_logout);
        buttonMyConversations = findViewById(R.id.button_mijnBerichten);
        buttonCreateRoute = findViewById(R.id.button_createRoute);
        textViewWelcome = findViewById(R.id.textView_welcome);

        sp= getSharedPreferences("createRoute",MODE_PRIVATE);
        spEditor=sp.edit();

        if(sp.getBoolean("succesCreate",false)){
            Toast.makeText(MainActivity.this,"Route succesfully created",Toast.LENGTH_SHORT);
            spEditor.putBoolean("succesCreate",false);
        }

        sp=getSharedPreferences("settings", Context.MODE_PRIVATE);
        spEditor=sp.edit();

        String user = sp.getString("login","error");
        textViewWelcome.setText("Welcome "+user);


        buttonSearchRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchRoutesActivity();
            }
        });

        buttonAllRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAllRoutesActivity();
            }
        });
        buttonCreateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreateRouteActivity();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });
    }

    private void goToAllRoutesActivity(){
        Intent allRoutes = new Intent(this,activity_all_routes.class);
        startActivity(allRoutes);
    }

    private void goToSearchRoutesActivity(){
        Intent searchRoutes = new Intent(this,activity_search_route.class);
        startActivity(searchRoutes);
    }

    private void goToLoginActivity(){
        Intent login = new Intent(this,Login_Activity.class);
        spEditor.clear().apply();
        startActivity(login);
    }

    private void goToCreateRouteActivity(){
        Intent createRoute = new Intent(this,activity_create_route.class);
        startActivity(createRoute);
    }
}
