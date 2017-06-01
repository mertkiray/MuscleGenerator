package com.musclegenerator.musclegenerator;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
public class SelectWorkoutToSendFragment extends Fragment {

    DatabaseReference dref;
    FirebaseUser user;
    int workoutId = -1;  // BUNU KIRAYDAN AL!!!
    String messageBody ="";
    String message;
    String receiverUID = ""; // BUNU KIRAYDAN AL!!!
    String userName = "";
    int messageNumber = 1; // DOKUNMA BUNA SAKIN
    int workoutLenght;
    TextView messageShow;
    Button send ;
    ProgressDialog mProgressDialog;
    String token;


    public SelectWorkoutToSendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_select_workout_to_send, container, false);
        messageShow = (TextView) view.findViewById(R.id.mesajText);
        send = (Button) view.findViewById(R.id.sendFinalButton);
        dref = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle bundle = getArguments();
        if(bundle != null){
            workoutId = Integer.valueOf(bundle.getString("workoutid"));
            receiverUID = bundle.getString("uid");
            token = bundle.getString("token");
        }

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Preparing Workout...");
        mProgressDialog.show();




        dref.child("userBilgileri").child(user.getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue().toString();

                dref.child("inbox").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(receiverUID)!=null){
                            if(dataSnapshot.child(receiverUID).child(userName)!=null) {

                                int temp = (int)dataSnapshot.child(receiverUID).child(userName).getChildrenCount();
                                messageNumber = temp + 1;



                                dref.child("workouts").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String workoutLenghtString = dataSnapshot.child("" + workoutId).child("total day").getValue().toString();
                                        workoutLenght = Integer.parseInt(workoutLenghtString);

                                        for (int i = 1; i < (workoutLenght + 1); i++) {


                                            String tempDay = "day " + i;
                                            messageBody = messageBody + "-" +i +"#";
                                            for (DataSnapshot ds : dataSnapshot.child("" + workoutId).child(tempDay).getChildren()) {

                                                messageBody = messageBody + ds.getKey().toString()+ "#";



                                            }
                                        }

                                        message = createMessage(messageBody);
                                        messageShow.setText(message);
                                        mProgressDialog.dismiss();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
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


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dref.child("workouts").child(user.getUid()).child(workoutId+"").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String workoutName = dataSnapshot.child("name").getValue().toString();
                        dref.child("inbox").child(receiverUID).child(userName).child("" + messageNumber).child("training").setValue(messageBody);
                        dref.child("inbox").child(receiverUID).child(userName).child("" + messageNumber).child("isim").setValue(workoutName);



                                ProfileFragment profileFragment = new ProfileFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.contentContainer,profileFragment);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }



                });

                sendNotification sendNotification = new sendNotification();
                sendNotification.message = userName+ "has send you a workout.";
                Log.d("TOKEN:", token);
                sendNotification.sendnotificationto = token;
                sendNotification.execute();

            }

        });


        return view;
    }

    public String createMessage(String messageBody) {

        String m ="";
        String[] days = messageBody.split("-");

        for(int i=1;i<workoutLenght+1;i++) {

            m += "Day " + (i) + ": " +'\n';
            String[] moves = days[i].split("#");
            int movesInADay = moves.length;
            for(int j=1;j<movesInADay;j++) {
                m += "  Workout " + (j)+ " : " + moves[j] + '\n';
            }
        }


        return m;
    }

}
