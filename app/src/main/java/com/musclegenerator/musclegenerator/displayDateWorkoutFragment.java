package com.musclegenerator.musclegenerator;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class displayDateWorkoutFragment extends DialogFragment {
    DatabaseReference database;
    FirebaseUser user;
    RecyclerView list;
    private RecyclerView.LayoutManager layoutManager;
    WorkoutCalendarAdapter adapter;
    ArrayList<WorkoutCalendarItem> items ;

    public displayDateWorkoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_date_workout, container, false);


        Bundle bundle = getArguments();
        String time = "";
        if(bundle.getString("date") != null){
             time = bundle.getString("date");
        }



        user  = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        list = (RecyclerView) view.findViewById(R.id.workoutlist);
        items = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        adapter = new WorkoutCalendarAdapter(items);
        list.setAdapter(adapter);
        list.setEnabled(false);


        final String lasttime = time;

        Log.d("LASTTIME ",lasttime);

                        database.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int all = (int)dataSnapshot.child("userRecords").child(user.getUid()).child(lasttime).getChildrenCount();
                                int init = 0;
                                for(DataSnapshot userrecord : dataSnapshot.child("userRecords").child(user.getUid()).child(lasttime).getChildren()){

                                    init++;
                                                    Log.d("xxyy","ANANX");
                                        WorkoutCalendarItem workoutCalendarItem = new WorkoutCalendarItem();
                                    String name = userrecord.getKey();
                                    String score = userrecord.getValue().toString();

                                    workoutCalendarItem.setScore(Integer.valueOf(score));
                                    workoutCalendarItem.setBodyname(name);

                                    items.add(workoutCalendarItem);

                                    if(init == all) {
                                        adapter = new WorkoutCalendarAdapter(items);
                                        list.setAdapter(adapter);
                                      adapter.notifyDataSetChanged();

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




        return view;
    }



    @Override
    public void onStart(){
        super.onStart();
        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation_fade);
    }


    public Date getdatefrom(String date) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Date mindate = new Date();
        try {

            String delims = "[,]";
            String[] tokens = date.split(delims);

            String[] dmtoken = tokens[0].split("\\s+");
            String[] yeartoken = tokens[1].split("\\s+");

            String finalyear = dmtoken[1] + " " + dmtoken[0] + " " + yeartoken[1];

            mindate = formatter.parse(finalyear);
        }
        catch (Exception e){e.printStackTrace();}

        return mindate;
    }


}



