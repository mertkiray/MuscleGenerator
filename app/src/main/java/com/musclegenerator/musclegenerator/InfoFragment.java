package com.musclegenerator.musclegenerator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {
    ViewFlipper viewFlipper;
    Button okButtonheight, okButtonweight, secondBut;
    EditText heightText, weightText, dayText;
    ImageView femaleButton,maleButton;
    double height, weight;
    String gender;
    Bundle bundle;
    FirebaseUser Cuser;
    DatabaseReference dref;
    static boolean isFinishedforMain = false;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_info, container, false);
        okButtonheight = (Button) view.findViewById(R.id.okButtonheight);
        okButtonweight = (Button) view.findViewById(R.id.okButtonweight);
        maleButton = (ImageView) view.findViewById(R.id.maleButton);
        femaleButton = (ImageView) view.findViewById(R.id.femaleButton);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.simpleViewFlipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_flipper_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_flipper_out));
        heightText = (EditText) view.findViewById(R.id.inputheight);
        weightText = (EditText) view.findViewById(R.id.inputweight);


        Cuser = FirebaseAuth.getInstance().getCurrentUser();
        MainActivity.bottomBar.setVisibility(View.GONE);
        dref = FirebaseDatabase.getInstance().getReference();

        bundle = new Bundle();

        okButtonheight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                height=Integer.parseInt(heightText.getText().toString());
                Log.d("height", "onClick: "+height);

                bundle.putDouble("height", height);
            }
        });

        okButtonweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                weight=Double.parseDouble(weightText.getText().toString());
                Log.d("weight", "onClick: "+weight);

                bundle.putDouble("weight", weight);
            }
        });

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFinishedforMain = true;
                viewFlipper.showNext();
                gender="male";
                Log.d("gender", "onClick: "+gender);

                bundle.putString("gender", gender);
                MainActivity.bottomBar.setVisibility(View.VISIBLE);
                dref.child("userBilgileri").child(Cuser.getUid()).child("gender").setValue(gender);
                dref.child("userBilgileri").child(Cuser.getUid()).child("weight").setValue(weight);
                dref.child("userBilgileri").child(Cuser.getUid()).child("height").setValue(height);

                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentContainer,profileFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFinishedforMain = true;

                viewFlipper.showNext();
                gender="female";
                Log.d("gender", "onClick: "+gender);

                bundle.putString("gender", gender);
                MainActivity.bottomBar.setVisibility(View.VISIBLE);
                dref.child("userBilgileri").child(Cuser.getUid()).child("gender").setValue(gender);
                dref.child("userBilgileri").child(Cuser.getUid()).child("weight").setValue(weight);
                dref.child("userBilgileri").child(Cuser.getUid()).child("height").setValue(height);

                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentContainer,profileFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        return view;
    }

}
