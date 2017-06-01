package com.musclegenerator.musclegenerator;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutListFragment extends Fragment {
    WorkoutListAdapter adapter;
    ArrayList<WorkoutListElement> list;
    ListView listView;
    FirebaseUser Cuser;
    DatabaseReference dref;
    boolean isFinished=false;
    Context cnt;
    ProgressDialog mProgressDialog;
    public WorkoutListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout_list, container, false);
        dref = FirebaseDatabase.getInstance().getReference();
        Cuser = FirebaseAuth.getInstance().getCurrentUser();
        listView = (ListView) view.findViewById(R.id.workoutList) ;
        list = new ArrayList<>();
        cnt =this.getContext();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Getting Ready...");
        mProgressDialog.show();

        dref.child("workouts").child(Cuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot bodypart : dataSnapshot.getChildren()){
                    String name = bodypart.child("name").getValue().toString();
                    int id = Integer.parseInt(bodypart.getKey().toString());
                    WorkoutListElement element = new WorkoutListElement(name,id);
                    list.add(element);
                    Log.d("NAME", "onDataChange: "+name);
                    Log.d("ID", "onDataChange: "+id);

                }
                isFinished=true;
                adapter = new WorkoutListAdapter(cnt, list);

                listView.setAdapter(adapter);
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("workoutid", list.get(position).getId());
                WholeWorkoutView prof = new WholeWorkoutView();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                prof.setArguments(bundle);
                ft.replace(R.id.contentContainer,prof);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
        return view;
    }

}
