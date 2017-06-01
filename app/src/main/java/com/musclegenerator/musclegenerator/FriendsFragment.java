package com.musclegenerator.musclegenerator;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements FriendWorkoutSendAdapter.OnCardClickListener{
    DatabaseReference database;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView list;
    FirebaseUser user;
    FriendWorkoutSendAdapter adapter;
    StorageReference storageReference;
    ArrayList<AddFriendItem> items ;
    boolean isFinished = false;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_fragment, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        layoutManager = new LinearLayoutManager(getActivity());

        user  = FirebaseAuth.getInstance().getCurrentUser();
        list = (RecyclerView) view.findViewById(R.id.friendslist);

        items = new ArrayList<>();
        adapter = new FriendWorkoutSendAdapter(items);
        adapter.setOnCardClickListener(FriendsFragment.this);

        list.setAdapter(adapter);

        list.setEnabled(false);
        list.setLayoutManager(layoutManager);

        retrieveInfo();

        Log.d("FRIENDS","FRIENDS");


        runTimer(items);

        return view;
    }


    public void retrieveInfo(){


        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                int count = 0;
               final int alltotal = (int)dataSnapshot.child("userBilgileri").child(user.getUid()).child("Friends").getChildrenCount();
                for (DataSnapshot ds : dataSnapshot.child("userBilgileri").child(user.getUid()).child("Friends").getChildren()) {



                            Log.d("USER KEY ", ds.getKey());
                            final AddFriendItem addFriendItem = new AddFriendItem();


                    for(DataSnapshot alluser : dataSnapshot.child("userBilgileri").getChildren()){

                            if(alluser.getKey().equals(ds.getKey())) {
                                count++;

                                Log.d("AAAAXXX","AXXXXX");
                                storageReference = FirebaseStorage.getInstance().getReference().child("profilepictures").child(alluser.getKey()).child("PP");
                                try {
                                    final File localFile = File.createTempFile("images", "jpeg");

                                    final DataSnapshot d = alluser;
                                    final int checkcount = count;


                                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
                                            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 300, 250, true);
                                            Matrix matrix = new Matrix();
                                            matrix.postRotate(90);
                                            Bitmap rotate = Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, true);
                                            addFriendItem.setBitmap(rotate);


                                            String surname = d.child("lastname").getValue().toString();
                                            String name = d.child("name").getValue().toString();

                                            String fullname = name + " " + surname;


                                            addFriendItem.setAge(Integer.valueOf(d.child("age").getValue().toString()));
                                            addFriendItem.setName(fullname);
                                            addFriendItem.setUid(d.getKey());
                                            addFriendItem.setToken(d.child("token").getValue().toString());

                                            items.add(addFriendItem);


                                            //      if(checkcount == dataSnapshot.getChildrenCount()){
                                            //          isFinished = true;
                                            //       }


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {

                                            Log.d("CHILDREN COUN COUNT", dataSnapshot.getChildrenCount() + "   " + checkcount);

                                            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.calender_image);

                                            addFriendItem.setBitmap(bitmap);

                                            String surname = d.child("lastname").getValue().toString();
                                            String name = d.child("name").getValue().toString();

                                            String fullname = name + " " + surname;


                                            addFriendItem.setAge(Integer.valueOf(d.child("age").getValue().toString()));
                                            addFriendItem.setName(fullname);
                                            addFriendItem.setToken(d.child("token").getValue().toString());

                                            addFriendItem.setUid(d.getKey());

                                            items.add(addFriendItem);


                                            //  if(checkcount == dataSnapshot.getChildrenCount()){
                                            //     isFinished = true;
                                            // }


                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                            if(checkcount == alltotal) {
                                                isFinished = true;
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }

            }}





            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



    }




    private void runTimer(final ArrayList<AddFriendItem> item) {
        final Handler h = new Handler();


        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(isFinished) {

                    list.setHasFixedSize(true);
                    list.setEnabled(false);
                    adapter = new FriendWorkoutSendAdapter(item);

                    list.setAdapter(adapter);
                    adapter.setOnCardClickListener(FriendsFragment.this);

                    list.setLayoutManager(layoutManager);

                    isFinished = false;
                    h.removeCallbacks(this);

                }
                h.postDelayed(this, 1000);
            }
        }, 1000);


    }



    @Override
    public void OnCardClicked(View view,int position){

        Toast.makeText(getActivity(), "CLICKED: "+items.get(position).getUid(),
                Toast.LENGTH_LONG).show();


        SendWorkoutDialogFragment sendWorkoutDialogFragment = new SendWorkoutDialogFragment();
        FragmentManager fm = getFragmentManager();
        Bundle bundle = new Bundle();
        String uid = items.get(position).getUid();
        bundle.putString("uid",uid);
        bundle.putString("token",items.get(position).getToken());

        sendWorkoutDialogFragment.setArguments(bundle);

        sendWorkoutDialogFragment.show(fm,"choose");



        //    addFriend(items.get(position).getUid());
    }



}



