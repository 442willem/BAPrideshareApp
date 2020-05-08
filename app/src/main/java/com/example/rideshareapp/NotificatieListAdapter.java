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
        String routeBegin = getItem(position).getRit().getRoute().getBeginpunt();
        String routeEind = getItem(position).getRit().getRoute().getEindpunt();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tijd = (TextView) convertView.findViewById(R.id.NotificatieTextView_tijd);
        TextView bericht = (TextView) convertView.findViewById(R.id.NotificatieTextView_message);
        TextView begin = (TextView) convertView.findViewById(R.id.NotificatieTextView_begin);
        TextView eind = (TextView) convertView.findViewById(R.id.NotificatieTextView_eind);

        tijd.setText("Notificatie op: " +tijdStamp);
        bericht.setText(message);
        begin.setText("Op route van: " + routeBegin);
        eind.setText("Naar: "+ routeEind);
        return convertView;



    }

}
