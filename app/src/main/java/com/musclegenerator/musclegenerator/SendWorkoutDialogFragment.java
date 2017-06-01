package com.musclegenerator.musclegenerator;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendWorkoutDialogFragment extends DialogFragment {
    FirebaseUser user;

    DatabaseReference database;
    ArrayList<String> workoutids;
    ArrayList<String> workoutnames;
    String willsendid = "";
    String workoutid = "";

    String token = "";
    public SendWorkoutDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_workout_dialog, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            willsendid = bundle.getString("uid");
            token = bundle.getString("token");
        }

        workoutids = new ArrayList<>();
        workoutnames = new ArrayList<>();
        user  = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        final MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner) view.findViewById(R.id.android_material_design_spinner);
         ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, workoutnames);
        materialDesignSpinner.setAdapter(arrayAdapter);


                        database.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {




                             for(DataSnapshot workouts :   dataSnapshot.child("workouts").child(user.getUid()).getChildren()){
                                 workoutids.add(workouts.getKey());
                                 workoutnames.add(workouts.child("name").getValue().toString());

                             }

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, workoutnames);
                                materialDesignSpinner.setAdapter(arrayAdapter);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

        Button b = (Button)view.findViewById(R.id.sendworkoutbutton);

      materialDesignSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              workoutid = workoutids.get(position);
          }
      });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle  = new Bundle();

                bundle.putString("workoutid",workoutid);
                bundle.putString("uid",willsendid);
                bundle.putString("token",token);

                SelectWorkoutToSendFragment selectWorkoutToSendFragment = new SelectWorkoutToSendFragment();

                selectWorkoutToSendFragment.setArguments(bundle);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentContainer,selectWorkoutToSendFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();

                getDialog().cancel();

            }
        });



                return view;
        }

}
