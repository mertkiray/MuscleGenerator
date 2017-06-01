package com.musclegenerator.musclegenerator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
public class EditProfileFragment extends Fragment {


    EditText name,lastname,age,weight,height;
    Button edit;
    DatabaseReference dref;
    FirebaseUser user;
    Boolean editable;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        name = (EditText)view.findViewById(R.id.nameEditProfile);
        lastname = (EditText)view.findViewById(R.id.lastnameEditProfile);
        age = (EditText)view.findViewById(R.id.ageEditProfile);
        weight = (EditText)view.findViewById(R.id.weightEditProfile);
        height = (EditText)view.findViewById(R.id.heightEditProfile);
        edit = (Button) view.findViewById(R.id.editButton);

        dref = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        dref.child("userBilgileri").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name.setText(dataSnapshot.child("name").getValue().toString());
                lastname.setText(dataSnapshot.child("lastname").getValue().toString());
                age.setText(dataSnapshot.child("age").getValue().toString());
                weight.setText(dataSnapshot.child("weight").getValue().toString());
                height.setText(dataSnapshot.child("height").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        name.setTag(name.getKeyListener());
        name.setKeyListener(null);
        lastname.setTag(lastname.getKeyListener());
        lastname.setKeyListener(null);
        age.setTag(age.getKeyListener());
        age.setKeyListener(null);
        weight.setTag(weight.getKeyListener());
        weight.setKeyListener(null);
        height.setTag(height.getKeyListener());
        height.setKeyListener(null);

        editable = false;

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editable==false) {

                    edit.setText("Save Changes");
                    editable = true;
                    name.setKeyListener((KeyListener) name.getTag());
                    lastname.setKeyListener((KeyListener) lastname.getTag());
                    age.setKeyListener((KeyListener) age.getTag());
                    weight.setKeyListener((KeyListener) weight.getTag());
                    height.setKeyListener((KeyListener) height.getTag());


                } else {
                    edit.setText("Edit");
                    editable = false;

                    name.setKeyListener(null);
                    lastname.setKeyListener(null);
                    age.setKeyListener(null);
                    weight.setKeyListener(null);
                    height.setKeyListener(null);

                    String Sname = name.getText().toString();
                    String Slastname = lastname.getText().toString();
                    String Sage = age.getText().toString();
                    String Sweight = weight.getText().toString();
                    String Sheight = height.getText().toString();


                    dref.child("userBilgileri").child(user.getUid()).child("name").setValue(Sname);
                    dref.child("userBilgileri").child(user.getUid()).child("lastname").setValue(Slastname);
                    dref.child("userBilgileri").child(user.getUid()).child("age").setValue(Sage);
                    dref.child("userBilgileri").child(user.getUid()).child("weight").setValue(Sweight);
                    dref.child("userBilgileri").child(user.getUid()).child("height").setValue(Sheight);

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ProfileFragment fragmentProfile = new ProfileFragment();
                    ft.replace(R.id.contentContainer,fragmentProfile);
                    ft.commit();
                }
            }
        });




        return view;
    }

}
