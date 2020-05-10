package com.example.rideshareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

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


        String departure = getItem(position).getBeginpunt();
        String arrival = getItem(position).getEindpunt();
        boolean accepted = getItem(position).isGoedgekeurd();
        int prijs = getItem(position).getPrijs();
        boolean betaald= getItem(position).isBetaald();
        String begintijdRoute = getItem(position).getVertrektijd().toString();

        LayoutInflater inflater = LayoutInflater.from(mContext);

        convertView = inflater.inflate(mResource,parent,false);
        TextView dep =(TextView) convertView.findViewById(R.id.rideTextView_begin);
        TextView arr = (TextView) convertView.findViewById(R.id.rideTextView_end);
        TextView begin = (TextView) convertView.findViewById(R.id.routeTextView_begin);
        TextView eind = (TextView) convertView.findViewById(R.id.routeTextView_eind);
        TextView begintijd = (TextView) convertView.findViewById(R.id.routeTextView_begintijd);
        TextView eindtijd = (TextView) convertView.findViewById(R.id.routeTextView_eindtijd);
        dep.setText("Departure position: " + departure);
        arr.setText("Arrival position: " + arrival);
        begin.setText("Departure time: "+begintijdRoute);
        eind.setText("Price: €"+prijs);
        if(betaald){
            eindtijd.setText("Payed? Yes");
        }
        else{
            eindtijd.setText("Payed? No");
        }

        if(accepted){
            begintijd.setText("Accepted? Yes");
        }
        else{
            begintijd.setText("Accepted? No");
        }


        return convertView;


    }
}
