package com.musclegenerator.musclegenerator;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mert on 28.05.2017.
 */

public class WorkoutCalendarAdapter  extends RecyclerView.Adapter<WorkoutCalendarAdapter.ViewHolder>{


    List<WorkoutCalendarItem> itemsa;

    public WorkoutCalendarAdapter(ArrayList<WorkoutCalendarItem> items ){
        super();
        itemsa = new ArrayList<WorkoutCalendarItem>();
        for(int i =0; i<items.size(); i++){
            WorkoutCalendarItem item = new WorkoutCalendarItem();
            item.setBodyname(items.get(i).getBodyname());
            item.setScore(items.get(i).getScore());
            itemsa.add(item);
        }
    }

    @Override
    public WorkoutCalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendarworkoutcard, parent, false);
        WorkoutCalendarAdapter.ViewHolder viewHolder = new WorkoutCalendarAdapter.ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(WorkoutCalendarAdapter.ViewHolder holder, final int position) {
        WorkoutCalendarItem list =  itemsa.get(position);
        holder.name.setText(list.getBodyname());
        holder.score.setText("REP: "+String.valueOf(list.getScore()));



    }

    @Override
    public int getItemCount() {
        return itemsa.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView score;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            score = (TextView) itemView.findViewById(R.id.score);



        }
    }
}
