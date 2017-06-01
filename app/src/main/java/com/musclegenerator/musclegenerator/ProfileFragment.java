package com.musclegenerator.musclegenerator;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    ViewPager viewPager;
    ImageView pp;
    Button profilePictureButton;
    DatabaseReference dref;
    StorageReference mStorageRef;
    FirebaseUser user;
    Button editProfile;

    TextView name;
    TextView age;
    TextView signupdate;

    FrameLayout profileVisual; // pp için
    Uri uri; // pp için
    ProgressDialog mProgressDialog; // pp için
    Bitmap imageBitmap; // pp için



    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileVisual = (FrameLayout) view.findViewById(R.id.profileFrame);
        profileVisual.setVisibility(View.INVISIBLE);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Getting Ready...");
        mProgressDialog.show();
        dref = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        name = (TextView) view.findViewById(R.id.nameProfilePage);
        age = (TextView) view.findViewById(R.id.ageProfilePage);
        signupdate = (TextView) view.findViewById(R.id.signupdateProfilePage);
        editProfile = (Button) view.findViewById(R.id.goToEditProfileButton);
        Log.d("CURRENT USER:", user.getEmail());

        pp = (ImageView) view.findViewById(R.id.ivUserProfilePhoto);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tlUserProfileTabs);
        profilePictureButton = (Button) view.findViewById(R.id.profilePictureButton);
        viewPager = (ViewPager) view.findViewById(R.id.pager);


        dref.child("userBilgileri").child(user.getUid()).child("profilepicture").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue().toString().equals("false")){
                    profilePictureButton.setText("Add PP");
                    profileVisual.setVisibility(View.VISIBLE);
                    mProgressDialog.dismiss();
                }
                else{
                    profilePictureButton.setText("Change PP");
                    mStorageRef = FirebaseStorage.getInstance().getReference().child("profilepictures").child(user.getUid()).child("PP");
                    final long ONE_MEGABYTE = 5 * 1024 * 1024;
                    mStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Bitmap resized = Bitmap.createScaledBitmap(bitmap,300,250, true);
                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            Bitmap rotate = Bitmap.createBitmap(resized,0,0,resized.getWidth(),resized.getHeight(),matrix,true);
                            pp.setImageBitmap(rotate);
                            mProgressDialog.dismiss();
                            profileVisual.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        dref.child("userBilgileri").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("name").getValue()!=null && dataSnapshot.child("lastname").getValue()!=null) {
                    String tempUsername = dataSnapshot.child("name").getValue().toString() + " " + dataSnapshot.child("lastname").getValue().toString();
                    name.setText(tempUsername);
                }
                if(dataSnapshot.child("age").getValue()!=null) {
                     age.setText(dataSnapshot.child("age").getValue().toString());
                }
                if(dataSnapshot.child("signupdate").getValue()!=null) {
                    signupdate.setText("Member since " + dataSnapshot.child("signupdate").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm2 = getFragmentManager();
                FragmentTransaction ft2 = fm2.beginTransaction();

                EditProfileFragment fragmentEditProfile = new EditProfileFragment();
                ft2.replace(R.id.contentContainer,fragmentEditProfile);
                ft2.commit();

            }
        });

        profilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent library = new Intent(Intent.ACTION_PICK);
                library.setType("image/*");
                startActivityForResult(library, 2);

            }
        });




        final  FirebaseAuth auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        Button b = (Button)view.findViewById(R.id.btnFollow);



        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
            }
        });




        final PagerAdapter adapter = new PagerAdapter
                (getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK) {

             dref.child("userBilgileri").child(user.getUid()).child("profilepicture").setValue("true");
             mProgressDialog.setMessage("Uploading...");
             mProgressDialog.show();

            uri = data.getData();
            try {

                imageBitmap = SiliCompressor.with(getContext()).getCompressBitmap(uri.toString());
                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                Bitmap resized = Bitmap.createScaledBitmap(imageBitmap,300,250, true);
                Bitmap rotate = Bitmap.createBitmap(resized,0,0,resized.getWidth(),resized.getHeight(),matrix,true);
                uri = getImageUri(getContext(), rotate);
            } catch (IOException e) {
                e.printStackTrace();
            }

            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profilepictures").child(user.getUid()).child("PP");
            Bitmap bp;
            try {
                bp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                Bitmap resized = Bitmap.createScaledBitmap(bp,300,250, true);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotate = Bitmap.createBitmap(resized,0,0,resized.getWidth(),resized.getHeight(),matrix,true);
                pp.setImageBitmap(rotate);
                profilePictureButton.setText("Change PP");
                profileVisual.setVisibility(View.VISIBLE);
                mProgressDialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }


            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                }
            });


        }


    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);

    }

}