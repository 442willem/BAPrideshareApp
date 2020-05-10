package com.example.rideshareapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduledService extends Service {

    private static final String CHANNEL_ID = "TEST";
    private Timer timer = new Timer();
    String url;
    String ACCESS_TOKEN;
    RequestQueue requestQueue;

    List<Notificatie> notificatieList;
    Gson json;

    String username;
    SharedPreferences sp;
    SharedPreferences.Editor spEditor;
    private int teller = 0;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        createNotificationChannel();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sp= getSharedPreferences("settings", Context.MODE_PRIVATE);
                spEditor=sp.edit();

                ACCESS_TOKEN = sp.getString("Token",null);
                requestQueue = Volley.newRequestQueue(getApplicationContext().getApplicationContext());
                username = sp.getString("login",null);


                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("http")
                        .encodedAuthority("192.168.1.39:8080")
                        .appendPath("G4REST")
                        .appendPath("restApp")
                        .appendPath("notification_service")
                        .appendPath("alleNotificaties")
                        .appendPath(username);

                url = uriBuilder.build().toString();
                json = new Gson();
                JsonArrayRequest requestAllNotoficaties = new JsonArrayRequest(Request.Method.GET, url,null, response -> {
                    Gson json = new Gson();

                     {
                        for(int i=0;i<response.length();i++) {
                            Notificatie r = new Notificatie();
                            try {
                                //r.setProfiel();
                                r=json.fromJson(response.getJSONObject(i).toString(),Notificatie.class);
                                if(!r.getGelezen()) {
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext().getApplicationContext(), CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_drive_eta_black_24dp)
                                            .setContentTitle(r.getType())
                                            .setContentText(r.getMessage())
                                            .setAutoCancel(true)
                                            .setContentIntent(setNotIntent(r))
                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                    .bigText(r.getMessage()))
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext().getApplicationContext());

                                    // notificationId is a unique int for each notification that you must define

                                    notificationManager.notify(teller, builder.build());
                                    teller++;
                                }
                                Log.d("Notificatie",r.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("Notificatie","added");

                        }
                    }
                }, error ->  {
                    Log.d("Notificatie",error.toString());

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
        }, 0, 5*60*1000);//5 Minutes
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public PendingIntent setNotIntent(Notificatie type){
        Intent mijnIntent= null;
        switch(type.getType()) {
            case "betaling":
                Toast.makeText(getApplicationContext().getApplicationContext(),"Payment happens on the website!",Toast.LENGTH_SHORT).show();
                break;
            case "review":
                Toast.makeText(getApplicationContext().getApplicationContext(),"Reviews happen on the website!",Toast.LENGTH_SHORT).show();
                break;
            case "routeHerinnering":
                mijnIntent= new Intent(getApplicationContext().getApplicationContext(), activity_searching_routes.class);
                mijnIntent.putExtra("soort",1);

                break;
            case "ritHerinnering":
                 mijnIntent = new Intent(getApplicationContext().getApplicationContext(), activity_rit_list.class);


                break;
            case "ritAccepted":
            case "ritChange":
                mijnIntent = new Intent(getApplicationContext().getApplicationContext(), activity_viewrit_p.class);
                mijnIntent.putExtra("rit", type.getRit());

                break;
            case "ritRequest":
                mijnIntent = new Intent(getApplicationContext().getApplicationContext(), activity_viewrit.class);
                mijnIntent.putExtra("rit", type.getRit());

                break;
            case "bericht":
                mijnIntent = new Intent(getApplicationContext().getApplicationContext(), activity_conversation_menu.class);


                break;
            default:
                Toast.makeText(getApplicationContext().getApplicationContext(),"Check your notifications!",Toast.LENGTH_SHORT).show();
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(mijnIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }

}
