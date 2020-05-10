package com.example.rideshareapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;

import java.util.List;

public class NotificatieListAdapter extends ArrayAdapter<Notificatie> {

    private Context mContext;
    private List<Notificatie> notificatieList;
    private int mResource;

    public NotificatieListAdapter(Context c, int resource, List<Notificatie> list){
        super(c,resource,list);
        mContext =c;
        notificatieList = list;
        mResource =resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        String tijdStamp = getItem(position).getTijdstip().toString();
        String message = getItem(position).getMessage();
        boolean gelezen = getItem(position).getGelezen();

        String routeBegin="";
        String routeEind="";
        if(getItem(position).getRit() != null && getItem(position).getRit().getRoute() != null ){

             routeBegin = getItem(position).getRit().getRoute().getBeginpunt();
             routeEind = getItem(position).getRit().getRoute().getEindpunt();

        }


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tijd = convertView.findViewById(R.id.NotificatieTextView_tijd);
        TextView bericht = convertView.findViewById(R.id.NotificatieTextView_message);
        TextView begin = convertView.findViewById(R.id.NotificatieTextView_begin);
        TextView eind = convertView.findViewById(R.id.NotificatieTextView_eind);

        if(!gelezen){
            bericht.setTextColor(-65536);
        }

        tijd.setText("Notificatie recieved on: " +tijdStamp);
        bericht.setText(message);
        if(getItem(position).getRit() != null) {
            begin.setText("On Route from: " + routeBegin);
            eind.setText("To: " + routeEind);
        }
        return convertView;



    }

}
