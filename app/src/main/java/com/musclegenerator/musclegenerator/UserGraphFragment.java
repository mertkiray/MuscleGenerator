package com.musclegenerator.musclegenerator;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserGraphFragment extends Fragment {
    FirebaseUser user;
      ArrayList< ArrayList<String> > workoutmap;
    DatabaseReference database;
    ArrayList<String> workoutnames;
    boolean isFinished = false;
    public UserGraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);

        final MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner) view.findViewById(R.id.android_material_design_spinner);

        workoutnames = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        workoutmap = new ArrayList<>();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot categories : dataSnapshot.child("hareketler").getChildren()){

                    for(DataSnapshot workouts : categories.getChildren()){
                        workoutnames.add(workouts.getKey());
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, workoutnames);
                        materialDesignSpinner.setAdapter(arrayAdapter);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Button b = (Button)view.findViewById(R.id.showgraph);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runTimer();
                workoutmap = new ArrayList<ArrayList<String>>();

                final String workout =   materialDesignSpinner.getText().toString();
                Log.d("WORKOUT",workout);


                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int total = (int)dataSnapshot.child("userRecords").child(user.getUid()).getChildrenCount();
                        int x = 0;
                        for (DataSnapshot value : dataSnapshot.child("userRecords").child(user.getUid()).getChildren()) {
                            x++;
                            for (DataSnapshot values : value.getChildren()) {
                                if (values.getKey().equals(workout)) {
                                    if(values.getValue() == null){
                                        continue;
                                    }

                                    Log.d("VALUES :",values+"");
                                    ArrayList<String> map = new ArrayList<>();
                                    map.add(value.getKey());
                                    map.add(values.getValue().toString());
                                    workoutmap.add(map);
                                }

                                Log.d("X :",x+"");
                                Log.d("TOTAL: ",total+"");

                            }


                            if(x == total){
                            isFinished = true;

                            }

                        }



                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }
        });

        return view;
    }


    private void runTimer() {
        final Handler h = new Handler();


        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(isFinished) {

                    final Bundle bundle = new Bundle();
                    bundle.putSerializable("map",workoutmap);
                    bundle.putString("deneme","DENEME");

                   final GraphFragment graphFragment = new GraphFragment();

                    FragmentTransaction fm = getFragmentManager().beginTransaction();


                    graphFragment.setArguments(bundle);

                    graphFragment.show(fm,"graph");


                    isFinished = false;
                    h.removeCallbacks(this);

                }
                h.postDelayed(this, 1000);
            }
        }, 1000);


    }

}
