package com.musclegenerator.musclegenerator;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutViewFragment extends Fragment {

    private ExpandablePlaceHolderView mExpandableView;
    private Context mContext;
    FirebaseAuth auth;
    FirebaseUser Cuser;
    DatabaseReference database;
    public static ArrayList<Info> selectedInfoList ;
    ScrollView sView;
    AVLoadingIndicatorView loading;
    Button doneButton;
    ArrayList<Feed> feedList;
    boolean isFinished1 = false;
    boolean isFinished2 = false;
    boolean isFinished3 = false;
    ArrayList<String> hareketList;
    View view;
    int currentWorkout=8;
    int workoutday;
    TextView textt;
    String currentDateTimeString;
    String putdate;
    ProgressDialog mProgressDialog;


    public WorkoutViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view= inflater.inflate(R.layout.fragment_workout_view, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        Cuser = FirebaseAuth.getInstance().getCurrentUser();
        mExpandableView = (ExpandablePlaceHolderView) view.findViewById(R.id.expandableView);
        loading=(AVLoadingIndicatorView)view.findViewById(R.id.AVLoadingIndicatorView);
        sView=(ScrollView)view.findViewById(R.id.container);
        doneButton = (Button) view.findViewById(R.id.doneButton);
        feedList = new ArrayList<>();
        hareketList = new ArrayList<>();
        selectedInfoList = new ArrayList<>();
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        putdate = getdatefrom(currentDateTimeString);
        textt= (TextView) view.findViewById(R.id.text);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Getting Ready...");
        mProgressDialog.show();


        if(Cuser!=null) {


            database.child("userBilgileri").child(Cuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    workoutday = Integer.parseInt(dataSnapshot.child("workoutday").getValue().toString());
                    currentWorkout = Integer.parseInt(dataSnapshot.child("currentworkout").getValue().toString());
                    Log.d("currentworkout", "" + currentWorkout);
                    Log.d("workoutday", "" + workoutday);

                    isFinished3 = true;

                    database.child("workouts").child(Cuser.getUid()).child("" + currentWorkout).child("day " + workoutday).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot bodypart : dataSnapshot.getChildren()) {

                                hareketList.add(bodypart.getKey());

                            }
                            if (hareketList.size() < 1) {
                                Log.d("BURAYA GİRDİ AQ", "BURAYA GİRDİ AQ");
                                WorkoutPreferencesFragment workoutFragment = new WorkoutPreferencesFragment();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.contentContainer, workoutFragment);
                                ft.addToBackStack(null);
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                ft.commit();

                            }
                            isFinished2 = true;

                            if (isFinished2) {
                                database.child("hareketler").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot bodypart : dataSnapshot.getChildren()) {

                                            for (DataSnapshot part : bodypart.getChildren()) {
                                                String wkName = part.getKey().toString();
                                                if (hareketList.contains(wkName)) {
                                                    Feed feed = new Feed();
                                                    feed.setHeading(wkName);
                                                    Info info = new Info();
                                                    info.setDifficulty(part.child("difficulty").getValue().toString());
                                                    info.setTitle(wkName);
                                                    info.setImageUrl(part.child("image").getValue().toString());
                                                    Log.d("INSIDE", part.getKey().toString());
                                                    feed.addToInfoList(info);
                                                    feedList.add(feed);
                                                }

                                            }


                                        }

                                        isFinished1 = true;
                                        mProgressDialog.dismiss();
                                        runTimer(view);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedInfoList.size() > 0) {
                        for (Info info : selectedInfoList) {
                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                            String putdate = getdatefrom(currentDateTimeString);


                            database.child("userRecords").child(Cuser.getUid()).child(putdate).child(info.getTitle()).setValue(info.getCount());
                        }
                        //TODO: GET OUT OF HERE!!
                        database.child("userBilgileri").child(Cuser.getUid()).child("workoutday").setValue(workoutday + 1);
                        ProfileFragment workoutFragment = new ProfileFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.contentContainer, workoutFragment);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                }
            });
        }


        return view;
    }

    private void runTimer(final View view) {
        final Handler h = new Handler();
        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(isFinished1) {
                    Log.d("4444444444444", "onDataChange: ");
                    Log.d("GİRDİM","GİRDİM");
                    database.child("userRecords").child(Cuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("3333333333333333", "onDataChange: ");
                            if(dataSnapshot.hasChildren()){
                            for(DataSnapshot a : dataSnapshot.getChildren()) {
                                Log.d("1111111111", "onDataChange: ");
                                if (a.getKey().toString().equals(putdate)) {
                                    Log.d("2222222", "onDataChange: ");
                                    textt.setVisibility(View.VISIBLE);
                                    sView.setVisibility(View.GONE);
                                    loading.setVisibility(View.GONE);

                                } else {
                                    loading.setVisibility(View.GONE);
                                    sView.setVisibility(View.VISIBLE);
                                    textt.setVisibility(View.GONE);
                                }
                            }
                            }else{
                                loading.setVisibility(View.GONE);
                                sView.setVisibility(View.VISIBLE);
                                textt.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mContext = getContext().getApplicationContext();

                    for (Feed feed : feedList) {


                        mExpandableView.addView(new HeadingView(mContext, feed.getHeading()));
                        for (Info info : feed.getInfoList()) {
                            mExpandableView.addView(new infoViewTwo(mContext, info));
                        }

                    }
                    isFinished1 = false;
                    h.removeCallbacks(this);



                }

                //   Log.d("GİRİYOM","VALLA");



                h.postDelayed(this, 1000);

            }
        }, 1000);
    }


    public String getdatefrom(String date) {


            String delims = "[,]";
            String[] tokens = date.split(delims);

            String[] dmtoken = tokens[0].split("\\s+");
            String[] yeartoken = tokens[1].split("\\s+");

            String finalyear = dmtoken[1] + " " + dmtoken[0] + " " + yeartoken[1];


        return finalyear;
    }

}
// child("pastWorkouts").child(user.getUId()).child(DATE)
