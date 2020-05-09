package com.example.rideshareapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.GeolocationApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.android.libraries.places.api.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class activity_viewrit extends FragmentActivity implements OnMapReadyCallback {

    private static String TAG = activity_viewrit_p.class.getSimpleName();

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Button buttonAccept;
    private Button buttonDeny;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private Geocoder geocoder;
    private Address address;

    Gson json;
    List<String> tussenstops;
    String way[];



    private Route route;
    private Rit rit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        sp = getSharedPreferences("settings",MODE_PRIVATE);
        String apiKey = getString(R.string.google_maps_key);
        rit = (Rit) getIntent().getSerializableExtra("rit");
        route = rit.getRoute();


        tussenstops = getTussenstops();



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrit);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(rit.isGoedgekeurd()){
            View accept = findViewById(R.id.button_accept);
            accept.setVisibility(View.GONE);
        } else{
            View deny = findViewById(R.id.button_deny);
            deny.setVisibility(View.GONE);
        }

        buttonAccept = (Button) findViewById(R.id.button_accept);
        buttonDeny = (Button) findViewById(R.id.button_deny);

        LatLng begin = getLocationFromAddress(route.getBeginpunt());
        LatLng eind = getLocationFromAddress(route.getEindpunt());

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getSharedPreferences("settings", MODE_PRIVATE);

                final String ACCESS_TOKEN = sp.getString("Token", null);
                final RequestQueue requestQueue = Volley.newRequestQueue(activity_viewrit.this);
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("http")
                        .encodedAuthority("192.168.1.39:8080")
                        .appendPath("G4REST")
                        .appendPath("restApp")
                        .appendPath("rit_service")
                        .appendPath("keurRitGoed")
                        .appendPath(String.valueOf(rit.getId()));

                final String url = uriBuilder.build().toString();

                    JsonObjectRequest requestAllRoutes = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(org.json.JSONObject response) {

                            Log.d("CreateRoute", response.toString());

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError errore) {
                            Toast.makeText(activity_viewrit.this, "The passenger has been accepted", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        //authorization header
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json; charset=UTF-8");
                            params.put("Authorization", ACCESS_TOKEN);
                            return params;
                        }
                    };

                    requestQueue.add(requestAllRoutes);
                    Intent cancel = new Intent(activity_viewrit.this, MainActivity.class);
                    startActivity(cancel);



            }
        });

        buttonDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getSharedPreferences("settings", MODE_PRIVATE);

                final String ACCESS_TOKEN = sp.getString("Token", null);
                final RequestQueue requestQueue = Volley.newRequestQueue(activity_viewrit.this);
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("http")
                        .encodedAuthority("192.168.1.39:8080")
                        .appendPath("G4REST")
                        .appendPath("restApp")
                        .appendPath("rit_service")
                        .appendPath("keurRitAf")
                        .appendPath(String.valueOf(rit.getId()));

                final String url = uriBuilder.build().toString();

                JsonObjectRequest requestAllRoutes = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {

                        Log.d("CreateRoute", response.toString());

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError errore) {
                        Toast.makeText(activity_viewrit.this, "The passenger has been denied", Toast.LENGTH_LONG).show();
                    }
                }) {
                    //authorization header
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json; charset=UTF-8");
                        params.put("Authorization", ACCESS_TOKEN);
                        return params;
                    }
                };

                requestQueue.add(requestAllRoutes);
                Intent cancel = new Intent(activity_viewrit.this, MainActivity.class);
                startActivity(cancel);



            }
        });


        Log.d("Maybe", String.valueOf(tussenstops));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        LatLng begin = getLocationFromAddress(route.getBeginpunt());
        LatLng eind = getLocationFromAddress(route.getEindpunt());
        Log.d("begin", String.valueOf(begin));
        Log.d("eind", (eind.latitude+","+eind.longitude));
        ArrayList<LatLng> wayp = new ArrayList<>();

        for (String t : tussenstops){
            LatLng temp = getLocationFromAddress(t);
            Log.d("temp", String.valueOf(temp));
            mMap.addMarker(new MarkerOptions().position(temp).title("TussenStop!"));
            wayp.add(temp);
        }
        StringBuilder stbldr = new StringBuilder();
        String next = "";
        for(int n=0;n<tussenstops.size();n++){
            stbldr.append(next);
            stbldr.append(tussenstops.get(n));
            next="|";
        }
        Log.d("tussenstops", stbldr.toString());
        stbldr.append(rit.getBeginpunt()+"|"+rit.getEindpunt());
        Log.d("JUICY_tussenstops", stbldr.toString());

        mMap.addMarker(new MarkerOptions()
                .position(getLocationFromAddress(rit.getBeginpunt()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.addMarker(new MarkerOptions()
                .position(getLocationFromAddress(rit.getEindpunt()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));




        mMap.addMarker(new MarkerOptions().position(begin).title("Start"));


        mMap.addMarker(new MarkerOptions().position(eind).title("Stop"));

        LatLng zaragoza = new LatLng(41.648823,-0.889085);
        List<LatLng> path = new ArrayList();

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAUI3IbCN38MjQJgCJptMXN4NluM7EdHns")
                .build();


        DirectionsApiRequest result = DirectionsApi.newRequest(context).origin((begin.latitude+","+begin.longitude))
                .destination((eind.latitude+","+eind.longitude))
                .waypoints(stbldr.toString())
                .optimizeWaypoints(true);







        DirectionsApiRequest req = DirectionsApi.getDirections(context, (begin.latitude+","+begin.longitude), (eind.latitude+","+eind.longitude));


        try {
            DirectionsResult res = req.await();
            DirectionsResult ser = result.await();
            Log.d("Mss", String.valueOf(ser.routes[0]));


            //Loop through legs and steps to get encoded polylines of each step
            if (ser.routes != null && ser.routes.length > 0) {
                Log.d("HIER", "HIER opnieuw");
                DirectionsRoute route = ser.routes[0];
                Log.d("R", String.valueOf(res.routes[0]));

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();

                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                                Log.d("Hier", coord1.lat + " " + coord1.lng );
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e("Test", ex.getLocalizedMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(begin, 12));



    }
    public LatLng  getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng  p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);

            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();
            Log.d("LAT", String.valueOf(location.getLatitude()));
            Log.d("LNG", String.valueOf(location.getLongitude()));

            p1 = new LatLng ((double) (location.getLatitude() ),
                    (double) (location.getLongitude() ));

            return p1;
        }catch (Exception e){}
        return null;
    }

    public List<String> getTussenstops(){
        final String ACCESS_TOKEN = sp.getString("Token",null);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);


        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .encodedAuthority("192.168.1.39:8080")
                .appendPath("G4REST")
                .appendPath("restApp")
                .appendPath("route_service")
                .appendPath("tussenstops")
                //hier de id van de route ingeve
                .appendPath(String.valueOf(route.getId()));

        final String url = uriBuilder.build().toString();

        json = new Gson();

        tussenstops=new ArrayList<>();

        JsonArrayRequest requestAllRoutes = new JsonArrayRequest(Request.Method.GET, url,null, response -> {
            Gson json = new Gson();

            for(int i=0;i<response.length();i++) {
                String tussenstop=null;
                try {
                    tussenstop=response.getString(i);
                    Log.d("Tussenstop",tussenstop);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                tussenstops.add(tussenstop);
                Log.d("Tussenstop",tussenstops.toString());


            }
        }, error -> Toast.makeText(activity_viewrit.this,"there was an error getting the waypoints",Toast.LENGTH_SHORT).show()){
            //authorization header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", ACCESS_TOKEN);
                return params;
            }};
        requestQueue.add(requestAllRoutes);
        return tussenstops;
    }
}
