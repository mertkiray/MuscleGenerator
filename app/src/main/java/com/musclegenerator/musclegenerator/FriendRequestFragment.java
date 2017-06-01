package com.musclegenerator.musclegenerator;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class FriendRequestFragment extends Fragment  implements FriendRequestAdapter.OnCardClickListener {
    DatabaseReference database;
    FirebaseUser user;
    RecyclerView list;
    private RecyclerView.LayoutManager layoutManager;
    PullRefreshLayout pullRefreshLayout;
    FriendRequestAdapter adapter;
    StorageReference storageReference;
    ArrayList<AddFriendItem> items ;
    boolean isFinished = false;


    public FriendRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);
        user  = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();
        layoutManager = new LinearLayoutManager(getActivity());
        list = (RecyclerView) view.findViewById(R.id.alluserlist);
        items = new ArrayList<>();
        adapter = new FriendRequestAdapter(items);
        adapter.setOnCardClickListener(FriendRequestFragment.this);
        list.setAdapter(adapter);
        list.setEnabled(false);

        Log.d("GİRDİM","GİRDİM");

        retrieveInfo();

        runTimer(items);

        pullRefreshLayout = (PullRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);


        return view;
    }


    public void retrieveInfo(){

        database.child("userBilgileri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    if(ds.getKey().equals(user.getUid())){


                        for(DataSnapshot a : ds.child("FriendRequest").getChildren()){

                            for(DataSnapshot x: dataSnapshot.getChildren()){
                                if(x.getKey().equals(a.getKey())){

                                    final    AddFriendItem item = new AddFriendItem();

                                    item.setUid(x.getKey());
                                    item.setAge(Integer.valueOf(x.child("age").getValue().toString()));
                                    item.setName(x.child("name").getValue().toString() + " "+x.child("lastname").getValue().toString());

                                    storageReference = FirebaseStorage.getInstance().getReference().child("profilepictures").child(x.getKey()).child("PP");
                                    try {
                                        final File localFile = File.createTempFile("images", "jpeg");

                                        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
                                                Bitmap resized = Bitmap.createScaledBitmap(bitmap,300,250, true);
                                                Matrix matrix = new Matrix();
                                                matrix.postRotate(90);
                                                Bitmap rotate = Bitmap.createBitmap(resized,0,0,resized.getWidth(),resized.getHeight(),matrix,true);
                                                item.setBitmap(rotate);
                                                items.add(item);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.calender_image);

                                                item.setBitmap(bitmap);
                                                items.add(item);
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                                isFinished = true;
                                            }
                                        });




                                    }
                                    catch (Exception e){e.printStackTrace(); }

                                }
                            }

                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    @Override
    public void OnCardClicked(View v,int position){

        database.child("userBilgileri").child(user.getUid()).child("Friends").child(items.get(position).getUid()).setValue(true);
        database.child("userBilgileri").child(items.get(position).getUid()).child("Friends").child(user.getUid()).setValue(true);

        database.child("userBilgileri").child(user.getUid()).child("FriendRequest").child(items.get(position).getUid()).removeValue();

        items.remove(position);
        adapter = new FriendRequestAdapter(items);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void OnRejectClicked(View v,int position){

        database.child("userBilgileri").child(user.getUid()).child("FriendRequest").child(items.get(position).getUid()).removeValue();

        items.remove(position);
        adapter = new FriendRequestAdapter(items);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void runTimer(final ArrayList<AddFriendItem> item) {
        final Handler h = new Handler();


        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(isFinished) {

                    list.setHasFixedSize(true);
                    list.setEnabled(false);
                    adapter = new FriendRequestAdapter(item);
                    list.setAdapter(adapter);
                    adapter.setOnCardClickListener(FriendRequestFragment.this);
                    list.setLayoutManager(layoutManager);
                    isFinished = false;
                    h.removeCallbacks(this);

                }
                h.postDelayed(this, 1000);
            }
        }, 1000);


    }

}
