package com.example.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class activity_vertrektijd extends AppCompatActivity {

    private DatePicker date;
    private TimePicker time;
    private Button goBack;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertrektijd);

        sp = getSharedPreferences("searchRoute",MODE_PRIVATE);
        if(!sp.getBoolean("searching",false))sp = getSharedPreferences("createRoute",MODE_PRIVATE);
        spEditor=sp.edit();

        date = (DatePicker) findViewById(R.id.datePicker_createRoute_vertrektijd);
        time= (TimePicker) findViewById(R.id.timePicker_createRoute_vertrektijd);
        goBack=(Button)findViewById(R.id.btn_createRoute_ok_vertrektijd);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();
                int dag = date.getDayOfMonth();
                int maand = date.getMonth()+1;
                int jaar = date.getYear();

                sb.append(dag).append("/").append(maand).append("/").append(jaar).append(" ");

                int uur = time.getHour();
                int minuut = time.getMinute();
                if (uur <10){
                    sb.append("0").append(uur).append(":").append(minuut);
                }
                else sb.append(uur).append(":").append(minuut);

                spEditor.putString("vertrektijd",sb.toString()).apply();
                spEditor.putBoolean("tijdenVerandert",true).apply();

                Intent createRoute = new Intent(activity_vertrektijd.this,activity_create_route.class);
                startActivity(createRoute);
            }
        });

    }
}
