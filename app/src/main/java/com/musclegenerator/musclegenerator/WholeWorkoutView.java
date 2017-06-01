package com.musclegenerator.musclegenerator;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class WholeWorkoutView extends Fragment {
    DatabaseReference database;
    FirebaseAuth auth;
    FirebaseUser Cuser;
    ExpandablePlaceHolderView mExpandableView;
    ArrayList<Feed> feedList;
    int inputId=4;
    boolean isFinished=false;
    private Context mContext;
    Info info;
    String hareketIsmi;
    Button selectButton;
    ProgressDialog mProgressDialog;

    public WholeWorkoutView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_whole_workout_view, container, false);
        auth = FirebaseAuth.getInstance();
        Cuser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        mExpandableView = (ExpandablePlaceHolderView) view.findViewById(R.id.expandableView);
        feedList = new ArrayList<>();
        selectButton = (Button) view.findViewById(R.id.selectButton);
        Bundle bundle = getArguments();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Getting Ready...");
        mProgressDialog.show();
        if(bundle!=null){
            inputId=bundle.getInt("workoutid");
        }
        //TODO:ID AS INPUT

        database.child("workouts").child(Cuser.getUid()).child(""+inputId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("USER ID", ""+Cuser.getUid());
                Log.d("bbodypart", "onDataChange: "+dataSnapshot);
                int totalDay = Integer.parseInt(dataSnapshot.child("total day").getValue().toString());
                for(int i=1; i<totalDay+1; i++){
                    Feed feed = new Feed();
                    String day = "day "+i;
                    feed.setHeading(day);
                    DataSnapshot bodypart = dataSnapshot.child(day);

                        for (DataSnapshot part : bodypart.getChildren()) {
                            Log.d("PART", "" + part);
                            info = new Info();
                            hareketIsmi = part.getKey().toString();
                            Log.d("HAREKET", "" + hareketIsmi);
                            info.setTitle(hareketIsmi);


                            feed.addToInfoList(info);


                        }

                        feedList.add(feed);

                }
                isFinished = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        runTimer(view);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child("userBilgileri").child(Cuser.getUid()).child("currentworkout").setValue(inputId);
                database.child("userBilgileri").child(Cuser.getUid()).child("workoutday").setValue(1);
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
                    mProgressDialog.dismiss();
                    Log.d("GİRDİM","GİRDİM");

                    mContext = getContext().getApplicationContext();

                    for (Feed feed : feedList) {

                        mExpandableView.addView(new HeadingView(mContext, feed.getHeading()));
                        for (Info info : feed.getInfoList()) {
                            mExpandableView.addView(new InfoViewThree(mContext, info));
                        }

                    }
                    isFinished = false;
                    h.removeCallbacks(this);
//                    loading.setVisibility(View.GONE);
//                    sView.setVisibility(View.VISIBLE);


                }

                //   Log.d("GİRİYOM","VALLA");



                h.postDelayed(this, 1000);

            }
        }, 1000);
    }
}
