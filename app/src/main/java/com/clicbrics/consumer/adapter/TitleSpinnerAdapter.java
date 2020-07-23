package com.clicbrics.consumer.adapter;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clicbrics.consumer.R;

import java.util.ArrayList;

/**
 * Created by Paras on 20-10-2016.
 */


// Custom Adapter for Spinner
public class TitleSpinnerAdapter extends ArrayAdapter<String> {

    private Context context1;
    private ArrayList<String> data;
    public Resources res;
    LayoutInflater inflater;

    public TitleSpinnerAdapter(Context context, ArrayList<String> objects) {
        super(context, R.layout.e_toolbar_spinner_row, objects);

        context1 = context;
        data = objects;

        inflater = (LayoutInflater) context1
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.e_toolbar_spinner_row, parent, false);

        TextView tvCategory = (TextView) row.findViewById(R.id.tvCategory);

        tvCategory.setText(data.get(position).toString());

        return row;
    }
}