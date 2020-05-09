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
    Button buttonMyRites;
    Button buttonMyNotifications;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAllRoutes = findViewById(R.id.button_AlleRouten);
        buttonMyRoutes = findViewById(R.id.button_mijnRouten);
        buttonSearchRoutes = findViewById(R.id.button_zoekRoutes);
        buttonMyRites = findViewById(R.id.button_mijnRitten);
        buttonLogout = findViewById(R.id.button_logout);
        buttonMyNotifications = findViewById(R.id.button_alleNotificaties);
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

        buttonMyConversations.setOnClickListener(v -> {
            Intent searchRoutes = new Intent(MainActivity.this,activity_conversation_menu.class);
            startActivity(searchRoutes);
        });
        buttonMyRoutes.setOnClickListener(v -> {
            sp = getSharedPreferences("list",MODE_PRIVATE);
            spEditor = sp.edit();
            spEditor.putInt("soort",1).apply();
            goToSearchingRoutesActivity();
        });

        buttonSearchRoutes.setOnClickListener(v -> {
            sp = getSharedPreferences("list",MODE_PRIVATE);
            spEditor = sp.edit();
            spEditor.putInt("soort",0).apply();
            goToSearchRoutesActivity();
        });

        buttonMyRites.setOnClickListener(v ->{
            goToSearchingRidesActivity();
        });

        buttonMyNotifications.setOnClickListener(v ->{
            goToAllNotifications();
        });

        buttonAllRoutes.setOnClickListener(v -> goToAllRoutesActivity());
        buttonCreateRoute.setOnClickListener(v -> goToCreateRouteActivity());

        buttonLogout.setOnClickListener(v -> goToLoginActivity());
    }

    private void goToAllRoutesActivity(){
        Intent allRoutes = new Intent(this,activity_all_routes.class);
        startActivity(allRoutes);
    }
    private void goToAllNotifications(){
        Intent allNotifications = new Intent(this, activity_notification.class);
        startActivity(allNotifications);
    }

    private void goToSearchRoutesActivity(){
        Intent searchRoutes = new Intent(this,activity_search_route.class);
        startActivity(searchRoutes);
    }

    private void goToSearchingRoutesActivity(){
        Intent searchRoutes = new Intent(this,activity_searching_routes.class);
        startActivity(searchRoutes);
    }

    private void goToSearchingRidesActivity(){
        Intent searchRoutes = new Intent(this,activity_rit_list.class);
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
