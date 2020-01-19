package com.example.safetyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Location_Adapter extends BaseAdapter {
    Context context;
    List<String> locNames;
    List<String> from;
    List<String> to;
    LayoutInflater layoutInflater;

    public Location_Adapter(Context context,List<String> locNames, List<String>  from, List<String>  to) {
        this.context = context;
        this.locNames = locNames;
        this.from = from;
        this.to = to;

        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return locNames.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=layoutInflater.inflate(R.layout.safezone_list,null);
        TextView locName = view.findViewById(R.id.locName);
        TextView fromtxt = view.findViewById(R.id.from);
        TextView totxt = view.findViewById(R.id.to);
        locName.setText(""+locNames.get(position).toString());
        fromtxt.setText(""+from.get(position).toString());
        totxt.setText(""+to.get(position).toString());
        return view;
    }
}
