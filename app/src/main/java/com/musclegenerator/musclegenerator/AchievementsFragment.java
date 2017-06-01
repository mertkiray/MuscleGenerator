package com.musclegenerator.musclegenerator;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class AchievementsFragment extends Fragment {


    TextView crunchTextBronze,crunchTextSilver,crunchTextGold;
    TextView hammerPressTextBronze,hammerPressTextSilver,hammerPressTextGold;
    TextView pullUpTextBronze,pullUpTextSilver,pullUpTextGold;
    TextView chestPressTextBronze,chestPressTextSilver,chestPressTextGold;
    TextView inclineFlyTextBronze,inclineFlyTextSilver,inclineFlyTextGold;
    TextView legPressTextBronze,legPressTextSilver,legPressTextGold;
    TextView shoulderPressTextBronze,shoulderPressTextSilver,shoulderPressTextGold;
    TextView facePullTextBronze,facePullTextSilver,facePullTextGold;
    TextView jogTextBronze,jogTextSilver,jogTextGold;

    FirebaseUser user;
    DatabaseReference dref;
    int crunchCount,hammerPressCount,pullUpCount,chestPressCount,inclineFlyCount,legPressCount,shoulderPressCount,facePullCount,jogCount;
    ProgressDialog mProgressDialog;


    // TODO: 29.05.2017  Ilk basta bir array olusturucam sonra o arraydeki toplam sayilarina bakicam sonra onlari kiyasliyip ona gore yazicam 
    //
    public AchievementsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dref = FirebaseDatabase.getInstance().getReference();

        crunchCount=0;
        hammerPressCount =0;
        pullUpCount =0;
        chestPressCount=0;
        inclineFlyCount =0;
        legPressCount=0;
        shoulderPressCount=0;
        facePullCount=0;
        jogCount=0;

        // ACHIEVEMENT 1
        crunchTextBronze = (TextView) view.findViewById(R.id.ach1bronzestatus);
        crunchTextSilver = (TextView) view.findViewById(R.id.ach1silverstatus);
        crunchTextGold = (TextView) view.findViewById(R.id.ach1goldstatus);

        // ACHIEVEMENT 2
        hammerPressTextBronze = (TextView) view.findViewById(R.id.ach2bronzestatus);
        hammerPressTextSilver = (TextView) view.findViewById(R.id.ach2silverstatus);
        hammerPressTextGold = (TextView) view.findViewById(R.id.ach2goldstatus);

        // ACHIEVEMENT 3
        pullUpTextBronze = (TextView) view.findViewById(R.id.ach3bronzestatus);
        pullUpTextSilver = (TextView) view.findViewById(R.id.ach3silverstatus);
        pullUpTextGold = (TextView) view.findViewById(R.id.ach3goldstatus);

        // ACHIEVEMENT 4
        chestPressTextBronze = (TextView) view.findViewById(R.id.ach4bronzestatus);
        chestPressTextSilver = (TextView) view.findViewById(R.id.ach4silverstatus);
        chestPressTextGold = (TextView) view.findViewById(R.id.ach4goldstatus);

        // ACHIEVEMENT 5
        inclineFlyTextBronze = (TextView) view.findViewById(R.id.ach5bronzestatus);
        inclineFlyTextSilver = (TextView) view.findViewById(R.id.ach5silverstatus);
        inclineFlyTextGold = (TextView) view.findViewById(R.id.ach5goldstatus);

        // ACHIEVEMENT 6
        legPressTextBronze = (TextView) view.findViewById(R.id.ach6bronzestatus);
        legPressTextSilver = (TextView) view.findViewById(R.id.ach6silverstatus);
        legPressTextGold = (TextView) view.findViewById(R.id.ach6goldstatus);

        // ACHIEVEMENT 7
        shoulderPressTextBronze = (TextView) view.findViewById(R.id.ach7bronzestatus);
        shoulderPressTextSilver = (TextView) view.findViewById(R.id.ach7silverstatus);
        shoulderPressTextGold = (TextView) view.findViewById(R.id.ach7goldstatus);

        // ACHIEVEMENT 8
        facePullTextBronze = (TextView) view.findViewById(R.id.ach8bronzestatus);
        facePullTextSilver = (TextView) view.findViewById(R.id.ach8silverstatus);
        facePullTextGold = (TextView) view.findViewById(R.id.ach8goldstatus);

        // ACHIEVEMENT 9
        jogTextBronze = (TextView) view.findViewById(R.id.ach9bronzestatus);
        jogTextSilver = (TextView) view.findViewById(R.id.ach9silverstatus);
        jogTextGold = (TextView) view.findViewById(R.id.ach9goldstatus);

        dref.child("userRecords").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(user.getUid())!=null) {
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setMessage("Checking Achievements...");
                    mProgressDialog.show();
                    //dayCount = (int)dataSnapshot.child(user.getUid()).getChildrenCount();

                    for(DataSnapshot ds : dataSnapshot.child(user.getUid()).getChildren()){

                        if(ds.child("crunch").getValue()!=null) {
                            crunchCount += Integer.parseInt(ds.child("crunch").getValue().toString());
                            if(crunchCount>=10){
                                crunchTextBronze.setText("Completed");
                            }
                            if(crunchCount>=50){
                                crunchTextSilver.setText("Completed");
                            }
                            if(crunchCount>=100){
                                crunchTextGold.setText("Completed");
                            }
                        }

                        if(ds.child("hammerPress").getValue()!=null) {
                            hammerPressCount += Integer.parseInt(ds.child("hammerPress").getValue().toString());
                            if(hammerPressCount>=10){
                                hammerPressTextBronze.setText("Completed");
                            }
                            if(hammerPressCount>=50){
                                hammerPressTextSilver.setText("Completed");
                            }
                            if(hammerPressCount>=100){
                                hammerPressTextGold.setText("Completed");
                            }
                        }

                        if(ds.child("Pull Up").getValue()!=null) {
                            pullUpCount += Integer.parseInt(ds.child("Pull Up").getValue().toString());
                            if(pullUpCount>=10){
                                pullUpTextBronze.setText("Completed");
                            }
                            if(pullUpCount>=50){
                                pullUpTextSilver.setText("Completed");
                            }
                            if(pullUpCount>=100){
                                pullUpTextGold.setText("Completed");
                            }
                        }

                        if(ds.child("chestPress").getValue()!=null) {
                            chestPressCount += Integer.parseInt(ds.child("chestPress").getValue().toString());
                            if(chestPressCount>=10){
                                chestPressTextBronze.setText("Completed");
                            }
                            if(chestPressCount>=50){
                                chestPressTextSilver.setText("Completed");
                            }
                            if(chestPressCount>=100){
                                chestPressTextGold.setText("Completed");
                            }
                        }

                        if(ds.child("inclineFly").getValue()!=null) {
                            inclineFlyCount += Integer.parseInt(ds.child("inclineFly").getValue().toString());
                            if(inclineFlyCount>=10){
                                inclineFlyTextBronze.setText("Completed");
                            }
                            if(inclineFlyCount>=50){
                                inclineFlyTextSilver.setText("Completed");
                            }
                            if(inclineFlyCount>=100){
                                inclineFlyTextGold.setText("Completed");
                            }
                        }


                        if(ds.child("legPress").getValue()!=null) {
                            legPressCount += Integer.parseInt(ds.child("legPress").getValue().toString());
                            if(legPressCount>=10){
                                legPressTextBronze.setText("Completed");
                            }
                            if(legPressCount>=50){
                                legPressTextSilver.setText("Completed");
                            }
                            if(legPressCount>=100){
                                legPressTextGold.setText("Completed");
                            }
                        }

                        if(ds.child("shoulderPress").getValue()!=null) {
                            shoulderPressCount += Integer.parseInt(ds.child("shoulderPress").getValue().toString());
                            if(shoulderPressCount>=10){
                                shoulderPressTextBronze.setText("Completed");
                            }
                            if(shoulderPressCount>=50){
                                shoulderPressTextSilver.setText("Completed");
                            }
                            if(shoulderPressCount>=100){
                                shoulderPressTextGold.setText("Completed");
                            }
                        }

                        if(ds.child("Face Pull").getValue()!=null) {
                            facePullCount += Integer.parseInt(ds.child("Face Pull").getValue().toString());
                            if(facePullCount>=10){
                                facePullTextBronze.setText("Completed");
                            }
                            if(facePullCount>=50){
                                facePullTextSilver.setText("Completed");
                            }
                            if(facePullCount>=100){
                                facePullTextGold.setText("Completed");
                            }
                        }

                        if(ds.child("jog").getValue()!=null) {
                            jogCount += Integer.parseInt(ds.child("jog").getValue().toString());
                            if(jogCount>=10){
                                jogTextBronze.setText("Completed");
                            }
                            if(jogCount>=50){
                                jogTextSilver.setText("Completed");
                            }
                            if(jogCount>=100){
                                jogTextGold.setText("Completed");
                            }
                        }

                    } // end of for loop
                    mProgressDialog.dismiss();





                } else {

                    Toast.makeText(getActivity(),"You dont have any history!", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
