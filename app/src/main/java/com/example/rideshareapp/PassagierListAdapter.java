package com.example.rideshareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PassagierListAdapter extends ArrayAdapter<Rit> {


    private Context mContext;
    private List<Rit> routeList;
    private int mResource;

    public PassagierListAdapter(Context c, int resource, List<Rit> list){
        super(c,resource,list);
        mContext=c;
        routeList=list;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String username = getItem(position).getPassagier().getLogin();
        String number= String.valueOf(getItem(position).getAantalPersonen());
        String pickupAdress = getItem(position).getBeginpunt();
        boolean geaccepteerd = getItem(position).isGoedgekeurd();


        LayoutInflater inflater = LayoutInflater.from(mContext);

        convertView = inflater.inflate(mResource,parent,false);

        TextView name = convertView.findViewById(R.id.tv_passagier_name);
        TextView pickup = convertView.findViewById(R.id.tv_passagier_pickup);
        TextView places = convertView.findViewById(R.id.tv_passagier_numberOfPlaces);
        TextView goedgekeurd=convertView.findViewById(R.id.tv_passagier_accepted);


        name.setText("Name: "+username);
        pickup.setText("Pickup At: "+pickupAdress);
        places.setText("Number of Places: "+number);

        if(geaccepteerd){
            goedgekeurd.setText("Accepted? Yes");
        }
        else{
            goedgekeurd.setText("Accepted? No");
        }

        return convertView;


    }
}
