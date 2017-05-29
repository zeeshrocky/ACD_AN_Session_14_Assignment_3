package com.example.zeeshan.updatecontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Adapter extends BaseAdapter {
    LayoutInflater inflater;
    private ListView listView;
    private ArrayList<String> al_contactName,al_contactNumber;
    Context context;

    Adapter(Context context, ArrayList<String> al_contactName, ArrayList<String> al_contactNumber) {
        this.context = context;
        this.al_contactName = al_contactName;
        this.al_contactNumber = al_contactNumber;
    }
    @Override
    public int getCount() {
        return al_contactName.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        inflater = LayoutInflater.from(context);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.name_contacts,parent,false);
            holder = new ViewHolder();
            holder.bindView(convertView);
            convertView.setTag(holder);
            //Log.e("Main_ACTIVITY","convertView is NULL");
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.contactName.setText(al_contactName.get(position));
        holder.contactNumber.setText(al_contactNumber.get(position));
        return convertView;
    }

    public class ViewHolder {
        TextView contactName, contactNumber;

        void bindView(View convertView) {
            //Log.e("ViewHolder => bindView", "Wow! its called...");
            contactName = (TextView) convertView.findViewById(R.id.contact_name);
            contactNumber = (TextView) convertView.findViewById(R.id.contact_number);
        }
    }
}

