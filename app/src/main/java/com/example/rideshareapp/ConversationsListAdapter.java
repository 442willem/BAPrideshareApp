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

public class ConversationsListAdapter extends ArrayAdapter<Profiel> {

    private Context mContext;
    private List<Profiel> routeList;
    private int mResource;

    public ConversationsListAdapter(Context c,int resource,List<Profiel> list){
        super(c,resource,list);
        mContext=c;
        routeList=list;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        String username=getItem(position).getLogin();

        LayoutInflater inflater = LayoutInflater.from(mContext);

        convertView = inflater.inflate(mResource,parent,false);

        TextView usernametv = (TextView) convertView.findViewById(R.id.tv_conversatoin_username);


        usernametv.setText(username);


        return convertView;


    }
}
