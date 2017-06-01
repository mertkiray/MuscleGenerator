package com.musclegenerator.musclegenerator;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutFragment extends Fragment{

    private ExpandablePlaceHolderView mExpandableView;
    private Context mContext;
    public static ArrayList<Info> selectedInfoList ;
    DatabaseReference database;
    ArrayList<Feed> feedList;
    ScrollView sView;
    AVLoadingIndicatorView loading;
    Bundle bundle;
    FirebaseAuth auth;
    FirebaseUser Cuser;
    String workoutStyle,workoutName;
    int totalDay, remainingDay;
    Button doneButton;
    int count;
    ProgressDialog mProgressDialog;


    boolean isFinished = false;



    public WorkoutFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        auth = FirebaseAuth.getInstance();
        Cuser = FirebaseAuth.getInstance().getCurrentUser();
        feedList = new ArrayList<>();
        selectedInfoList= new ArrayList<>();
        mExpandableView = (ExpandablePlaceHolderView) view.findViewById(R.id.expandableView);
        loading=(AVLoadingIndicatorView)view.findViewById(R.id.AVLoadingIndicatorView);
        sView=(ScrollView)view.findViewById(R.id.container);
        doneButton = (Button) view.findViewById(R.id.doneButton);
        bundle = getArguments();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Getting Ready...");
        mProgressDialog.show();

        MainActivity.bottomBar.setVisibility(View.GONE);

        if(bundle!=null) {
            workoutStyle = bundle.getString("workoutStyle");
            workoutName = bundle.getString("workoutName");
            totalDay = bundle.getInt("totalDay");
            remainingDay = bundle.getInt("remainingDay");
            remainingDay--;
            int currentDay = totalDay-remainingDay;

//            getActivity().setTitle("Day "+currentDay);
        }



        database = FirebaseDatabase.getInstance().getReference();

        final ArrayList<Info> infolist= new ArrayList<>();

        database.child("workouts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count = Integer.parseInt(dataSnapshot.child("count").getValue().toString())+1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.child("hareketler").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot bodypart : dataSnapshot.getChildren()){

                    Feed feed = new Feed();
                    feed.setHeading(bodypart.getKey().toString());

                    for(DataSnapshot inside : bodypart.getChildren()){

                            Log.d("INSIDE",inside.toString());



                            Info info = new Info();
                        info.setTitle(inside.getKey().toString());
                        info.setDifficulty(inside.child("difficulty").getValue().toString());
                        info.setImageUrl(inside.child("image").getValue().toString());
                        Log.d("INFO TITLE",inside.getKey().toString());
                        Log.d("INFO DIFFICULTY",inside.child("difficulty").getValue().toString());
                        Log.d("INFO IMAGE URL",inside.child("image").getValue().toString());

                        feed.addToInfoList(info);
                    }
                    feedList.add(feed);
                }
                isFinished = true;
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        runTimer(view);


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentDay=totalDay-remainingDay;
                if(currentDay<2) {
                    database.child("workouts").child(Cuser.getUid()).child(""+count).child("total day").setValue(totalDay);
                    database.child("workouts").child(Cuser.getUid()).child(""+count).child("workout style").setValue(workoutStyle);
                    database.child("workouts").child(Cuser.getUid()).child(""+count).child("name").setValue(workoutName);
                }
                for(Info i :selectedInfoList) {
                    database.child("workouts").child(Cuser.getUid()).child(""+count).child("day " + currentDay).child(i.getTitle()).setValue(true);
                }
                if(remainingDay==0){
                    database.child("workouts").child("count").setValue(count);
                    MainActivity.bottomBar.setVisibility(View.VISIBLE);
                }

                selectedInfoList.clear();

                if(remainingDay>0){


                    bundle = new Bundle();
                    bundle.putString("workoutStyle", workoutStyle);
                    bundle.putString("workoutName", workoutName);
                    bundle.putInt("totalDay", totalDay);
                    bundle.putInt("remainingDay", remainingDay);
                    WorkoutFragment workoutFragment = new WorkoutFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    workoutFragment.setArguments(bundle);
                    ft.replace(R.id.contentContainer,workoutFragment);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();

                }else{
                    ProfileFragment prof = new ProfileFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.contentContainer,prof);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            }
        });
        return view;
    }
    private void runTimer(final View view) {
       final Handler h = new Handler();
        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(isFinished) {
                    Log.d("GİRDİM","GİRDİM");

                    mContext = getContext().getApplicationContext();

                    for (Feed feed : feedList) {

                        mExpandableView.addView(new HeadingView(mContext, feed.getHeading()));
                        for (Info info : feed.getInfoList()) {
                            mExpandableView.addView(new InfoView(mContext, info));
                        }

                    }
                    isFinished = false;
                    h.removeCallbacks(this);
                    loading.setVisibility(View.GONE);
                    sView.setVisibility(View.VISIBLE);


                }

             //   Log.d("GİRİYOM","VALLA");



                h.postDelayed(this, 1000);

            }
        }, 1000);
    }

}
