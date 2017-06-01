package com.musclegenerator.musclegenerator;


import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFriendFragment extends Fragment implements FriendCardAdapter.OnCardClickListener{
    DatabaseReference database;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView list;
    FirebaseUser user;
    PullRefreshLayout pullRefreshLayout;
    FriendCardAdapter adapter;
    SearchView searchView;
    StorageReference storageReference;
    ArrayList<AddFriendItem> items ;
    String sendername = "";
    boolean isFinished = false;

    public AddFriendFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        layoutManager = new LinearLayoutManager(getActivity());

        user  = FirebaseAuth.getInstance().getCurrentUser();
        list = (RecyclerView) view.findViewById(R.id.alluserlist);

        items = new ArrayList<>();
        adapter = new FriendCardAdapter(items);
        adapter.setOnCardClickListener(this);

        list.setAdapter(adapter);

           list.setEnabled(false);

         pullRefreshLayout = (PullRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);



        retrieveInfo();


        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Toast.makeText(getActivity(),"REFRESHED",Toast.LENGTH_SHORT).show();
                list.setEnabled(false);
                items.clear();
                items = new ArrayList<>();
                adapter = new FriendCardAdapter(items);
                list.setAdapter(adapter);
                retrieveInfo();

           //     searchView.setQuery("", false);
             //   searchView.setIconified(true);
                searchView.setIconified(true);

                searchView.clearFocus();


                runTimer(items);

                pullRefreshLayout.setRefreshing(false);
            }
        });

              runTimer(items);




        return view;


    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.searchbar,menu);
         searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                list.setEnabled(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<AddFriendItem> tempitem = new ArrayList<>() ;

                list.setEnabled(false);


                    for (int i = 0; i < items.size(); i++) {

                        if (items.get(i).getName().toLowerCase().contains(s.toLowerCase())) {
                            tempitem.add(items.get(i));
                        }
                    }

                list.setHasFixedSize(true);

                list.setEnabled(false);
                list.setLayoutManager(layoutManager);
                adapter = new FriendCardAdapter(tempitem);
                adapter.setOnCardClickListener(AddFriendFragment.this);

                list.setAdapter(adapter);


                return false;
            }
        });

    }



    public void retrieveInfo(){


        database.child("userBilgileri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                int count = 1;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Object o = ds.child("FriendRequest/"+user.getUid()).getValue();
                    Object a = ds.child("FriendRequest").getValue();

                    count++;
                    if(ds.getKey().equals(user.getUid())){
                        sendername = ds.child("name").getValue().toString()+ " "+ds.child("lastname").getValue().toString();
                    }

                    if (!ds.getKey().equals(user.getUid()) && ( a == null || o==null )) {
                        Log.d("USER KEY ",ds.getKey());
                        final  AddFriendItem addFriendItem = new AddFriendItem();

                        storageReference = FirebaseStorage.getInstance().getReference().child("profilepictures").child(ds.getKey()).child("PP");
                        try {
                           final File localFile = File.createTempFile("images", "jpeg");

                            final  DataSnapshot d = ds;
                            final int checkcount = count;
                            Log.d("CHILDREN COUN COUNT",dataSnapshot.getChildrenCount()+"   " + checkcount);


                            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){


                                   Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
                                    Bitmap resized = Bitmap.createScaledBitmap(bitmap,300,250, true);
                                    Matrix matrix = new Matrix();
                                    matrix.postRotate(90);
                                    Bitmap rotate = Bitmap.createBitmap(resized,0,0,resized.getWidth(),resized.getHeight(),matrix,true);
                                    addFriendItem.setBitmap(rotate);


                                    String surname = d.child("lastname").getValue().toString();
                                    String name = d.child("name").getValue().toString();

                                    String fullname = name + " " + surname;

                                    addFriendItem.setToken(d.child("token").getValue().toString());

                                    addFriendItem.setAge(Integer.valueOf(d.child("age").getValue().toString()));
                                    addFriendItem.setName(fullname);
                                    addFriendItem.setUid(d.getKey());

                                    items.add(addFriendItem);


                              //      if(checkcount == dataSnapshot.getChildrenCount()){
                              //          isFinished = true;
                             //       }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                    Log.d("CHILDREN COUN COUNT",dataSnapshot.getChildrenCount()+"   " + checkcount);

                                    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.calender_image);

                                    addFriendItem.setBitmap(bitmap);

                                    String surname = d.child("lastname").getValue().toString();
                                    String name = d.child("name").getValue().toString();

                                    String fullname = name + " " + surname;

                                    addFriendItem.setToken(d.child("token").getValue().toString());
                                    addFriendItem.setAge(Integer.valueOf(d.child("age").getValue().toString()));
                                    addFriendItem.setName(fullname);

                                    addFriendItem.setUid(d.getKey());

                                    items.add(addFriendItem);


                                  //  if(checkcount == dataSnapshot.getChildrenCount()){
                                   //     isFinished = true;
                                   // }


                                }
                            }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                    isFinished = true;
                                }
                            });
                        }
                        catch (Exception e){ e.printStackTrace();}

                    }


                }


            }





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
                    adapter = new FriendCardAdapter(item);

                    list.setAdapter(adapter);
                    adapter.setOnCardClickListener(AddFriendFragment.this);

                    list.setLayoutManager(layoutManager);

                    isFinished = false;
                    h.removeCallbacks(this);



                }
                h.postDelayed(this, 1000);
            }
        }, 1000);


    }


    public void addFriend(final String sendto,String token) {
        database.child("userBilgileri").child(sendto).child("FriendRequest").child(user.getUid()).setValue(true);

        sendNotification sendNotification = new sendNotification();
        sendNotification.message = sendername+" has sent you a friend request";
        sendNotification.sendnotificationto = token;

        sendNotification.execute();


    }

    @Override
    public void OnCardClicked(View view,int position){

        Toast.makeText(getActivity(), "CLICKED: "+items.get(position).getUid(),
                Toast.LENGTH_LONG).show();


        addFriend(items.get(position).getUid(),items.get(position).getToken());
        items.remove(position);
        adapter = new FriendCardAdapter(items);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();




    }

    @Override
    public void onDestroyOptionsMenu(){
        super.onDestroyOptionsMenu();
        searchView.setVisibility(View.GONE);
    }

}
