package com.musclegenerator.musclegenerator;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment implements MessageAdapter.OnCardClickListener {

    ArrayList<MessageItem> mItems;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView list;
    MessageAdapter adapter;
    FirebaseUser user;
    DatabaseReference dref;
    StorageReference sref;
    Bitmap bitmap;
    String denemeIsmi;
    String denemeMesaji;
    ProgressDialog mProgressDialog;
    int COUNT_FROM_DATABASE;
    MessageItem mItemTemp;
    int i,j;
    int totalDate;

    String cakal[] = new String [50];
    int index=0;
  //  String cardNameIndex;

    String[] InboxPeopleNames;
    int INBOX_PEOPLE_NUMBER;
   // int INBOX_MESSAGE_NUMBER = 3; // TODO: 27.05.2017   Bunu databaseden cekicez ilerde
    final long ONE_MEGABYTE = 1024 * 1024;

    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Getting Ready...");
        mProgressDialog.show();
        mItems = new ArrayList<>();
        dref = FirebaseDatabase.getInstance().getReference();
        sref = FirebaseStorage.getInstance().getReference();
        layoutManager = new LinearLayoutManager(getActivity());
        user = FirebaseAuth.getInstance().getCurrentUser();
        list = (RecyclerView) view.findViewById(R.id.inboxUserList);


        list.setHasFixedSize(true);
        list.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(mItems);
        list.setAdapter(adapter);
        adapter.setOnCardClickListener(this);


        dref.child("inbox").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                INBOX_PEOPLE_NUMBER = (int)dataSnapshot.getChildrenCount();
                InboxPeopleNames = new String[INBOX_PEOPLE_NUMBER];

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    InboxPeopleNames[j] = ds.getKey().toString();
                    j++;
                }

               if(INBOX_PEOPLE_NUMBER>0) {
                   Log.d("Isim1", InboxPeopleNames[0]);
                   j = 0;
                   for (j = 0; j < INBOX_PEOPLE_NUMBER; j++) {
                       denemeIsmi = InboxPeopleNames[j];
                       Log.d("Isim1", denemeIsmi);
                       dref.child("inbox").child(user.getUid()).child(InboxPeopleNames[j]).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {

                               for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                   denemeMesaji = ds.child("isim").getValue().toString();
                                   cakal[index] = ds.child("training").getValue().toString();
                                   index++;
                                   Log.d("mesaj", denemeMesaji);
                                   final MessageItem mm = new MessageItem();
                                   mm.setUserName(denemeIsmi);
                                   mm.setMessage(denemeMesaji);

                                   // Burada bana mesaj yollayanin UID'si lazim!!!! Bunu daha sonra inboxtan alcam!!

                                   sref.child("profilepictures").child(user.getUid()).child("PP").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                       @Override
                                       public void onSuccess(byte[] bytes) {
                                           bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


                                           mm.setBitmap(bitmap);
                                           mItems.add(mm);
                                           adapter = new MessageAdapter(mItems);
                                           list.setAdapter(adapter);
                                           adapter.setOnCardClickListener(InboxFragment.this);

                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception exception) {

                                           Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.calender_image);

                                           mm.setBitmap(bitmap);
                                           mItems.add(mm);
                                           adapter = new MessageAdapter(mItems);
                                           list.setAdapter(adapter);
                                           adapter.setOnCardClickListener(InboxFragment.this);

                                       }
                                   });

                               }

                               mProgressDialog.dismiss();
                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });


                   }
               }
               else{
                   mProgressDialog.dismiss();

               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); // Inboxta kac kisinin mesaj attigini goster


        return view;
    }


    @Override
    public void onCardClicked(View view,int position){

        Toast.makeText(getActivity(), "CARD CLICKED" + position,
                Toast.LENGTH_LONG).show();

        mItemTemp = new MessageItem();
        mItemTemp = mItems.get(position);
        String mesaj = cakal[position];
        Log.d("mesaj",mesaj);

        final String[][] database = createDatabaseStringToAdd(mesaj);


        dref.child("workouts").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                COUNT_FROM_DATABASE = Integer.parseInt(dataSnapshot.getValue().toString());
                COUNT_FROM_DATABASE += 1;
                dref.child("workouts").child("count").setValue(COUNT_FROM_DATABASE);
                dref.child("workouts").child(user.getUid()).child(COUNT_FROM_DATABASE+"").child("name").setValue(mItemTemp.getMessage());
                dref.child("workouts").child(user.getUid()).child(COUNT_FROM_DATABASE+"").child("total day").setValue(totalDate);

                for(int i=1;i<totalDate+1;i++) {
                    String tempS = "day " + i;
                    for (int j = 0; j < 30; j++) {
                        if(database[i][j]!=null) {
                            dref.child("workouts").child(user.getUid()).child(COUNT_FROM_DATABASE + "").child(tempS).child(database[i][j]).setValue(true);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



//-1#crunch#-2#Pull Up#inclineFly#legPress#



    public String[][] createDatabaseStringToAdd(String message) {

        String[][] workouts = new String[50][30];
        String[] totalDayData = message.split("-");
       // String[] workoutByDay = new String[totalDayData.length];
        totalDate = totalDayData.length-1;
        for(int i=1;i<totalDayData.length;i++) {

            String[] workoutByDay =  totalDayData[i].split("#");
            for(int j=0;j<workoutByDay.length;j++) {

               if(j!=0) {
                   workouts[i][j] = workoutByDay[j];
               }
            }
        }

        return workouts;
    }

}
