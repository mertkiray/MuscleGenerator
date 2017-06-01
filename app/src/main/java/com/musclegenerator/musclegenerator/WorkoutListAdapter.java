package com.musclegenerator.musclegenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MONSTER on 28.05.2017.
 */

public class WorkoutListAdapter extends ArrayAdapter<WorkoutListElement> {


    public ArrayList<WorkoutListElement> items;


    public WorkoutListAdapter(Context context, ArrayList<WorkoutListElement> items) {
        super(context,0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        WorkoutListElement element =getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workout_list_element, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.title);
        name.setText(element.getName());
        return convertView;
    }




}


