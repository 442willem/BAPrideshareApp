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

public class RitListAdapter extends ArrayAdapter<Rit> {


    private Context mContext;
    private List<Rit> routeList;
    private int mResource;

    public RitListAdapter(Context c, int resource, List<Rit> list){
        super(c,resource,list);
        mContext=c;
        routeList=list;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        boolean accepted = getItem(position).isGoedgekeurd();
        int prijs = getItem(position).getPrijs();
        boolean betaald= getItem(position).isBetaald();
        String begintijdRoute = getItem(position).getVertrektijd().toString();

        LayoutInflater inflater = LayoutInflater.from(mContext);

        convertView = inflater.inflate(mResource,parent,false);

        TextView begin = (TextView) convertView.findViewById(R.id.routeTextView_begin);
        TextView eind = (TextView) convertView.findViewById(R.id.routeTextView_eind);
        TextView begintijd = (TextView) convertView.findViewById(R.id.routeTextView_begintijd);
        TextView eindtijd = (TextView) convertView.findViewById(R.id.routeTextView_eindtijd);

        begin.setText("departure time: "+begintijdRoute);
        eind.setText("prices: "+prijs);
        if(betaald){
            eindtijd.setText("payed: yes");
        }
        else{
            eindtijd.setText("payed: no");
        }

        if(accepted){
            begintijd.setText("accepted: yes");
        }
        else{
            begintijd.setText("accepted: no");
        }


        return convertView;


    }
}
