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

public class RouteListAdapter extends ArrayAdapter<Route> {

    private Context mContext;
    private List<Route> routeList;
    private int mResource;

    public RouteListAdapter(Context c,int resource,List<Route> list){
        super(c,resource,list);
        mContext=c;
        routeList=list;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String beginRoute=getItem(position).getBeginpunt();
        String eindRoute = getItem(position).getEindpunt();
        String eindtijdRoute= getItem(position).getEindtijd().toString();
        String begintijdRoute = getItem(position).getVertrektijd().toString();

        LayoutInflater inflater = LayoutInflater.from(mContext);

        convertView = inflater.inflate(mResource,parent,false);

        TextView begin = (TextView) convertView.findViewById(R.id.routeTextView_begin);
        TextView eind = (TextView) convertView.findViewById(R.id.routeTextView_eind);
        TextView begintijd = (TextView) convertView.findViewById(R.id.routeTextView_begintijd);
        TextView eindtijd = (TextView) convertView.findViewById(R.id.routeTextView_eindtijd);

        begin.setText("beginpunt: "+beginRoute);
        eind.setText("eindpunt: "+eindRoute);
        eindtijd.setText("eindtijd: "+eindtijdRoute);
        begintijd.setText("vertrektijd: "+begintijdRoute);

        return convertView;


    }
}
