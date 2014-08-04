package com.universityofleipzig.bottlemail.adapter;

import java.util.ArrayList;

import com.universityofleipzig.bottlemail.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

public class BottleDetailAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> mMessagesItems;
    
    public BottleDetailAdapter(Context context, ArrayList<String> messageItems) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mMessagesItems = messageItems;
    }
    
    @Override
    public int getCount() {
        return mMessagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int currentPosition = position;
        final ViewHolder holder;
        
        // viewholder pattern
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_bottledetails_item, null);
            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.activity_bottledetails_item_title);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        //set title
        holder.title.setText(mMessagesItems.get(currentPosition));
        
        return convertView;
    }
    
    class ViewHolder {
        TextView title;
    }
}
