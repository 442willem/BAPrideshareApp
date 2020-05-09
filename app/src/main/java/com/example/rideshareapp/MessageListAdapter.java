package com.example.rideshareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.Timestamp;
import java.util.List;

public class MessageListAdapter extends ArrayAdapter<Bericht>{


    private Context mContext;
    private List<Bericht> messageList;
    private int mResource;

    public MessageListAdapter(Context c,int resource,List<Bericht> list){
        super(c,resource,list);
        mContext=c;
        messageList=list;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        Profiel username=getItem(position).getZender();
        String content = getItem(position).getContent();
        Timestamp timestamp = getItem(position).getTimestamp();
        String tijd = timestamp.toString();

        LayoutInflater inflater = LayoutInflater.from(mContext);

        convertView = inflater.inflate(mResource,parent,false);

        TextView usernametv = (TextView) convertView.findViewById(R.id.tv_message_username);
        TextView contenttv = (TextView) convertView.findViewById(R.id.tv_message_content);
        TextView timestamptv = (TextView) convertView.findViewById(R.id.tv_message_timestamp);


        usernametv.setText(username.getLogin());
        contenttv.setText(content);
        timestamptv.setText(tijd);


        return convertView;


    }
}
