package com.musclegenerator.musclegenerator;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutPreferencesFragment extends Fragment {
    Button fatBurnButton, cardioButton, muscleButton, createWorkout, selectWorkout, okButtonName, okButtonDay;
    EditText inputDay, inputName;
    ViewFlipper viewFlipper;
    String workoutStyle, workoutName;
    int totalDay;
    Bundle bundle;


    public WorkoutPreferencesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout_preferences, container, false);


        viewFlipper = (ViewFlipper) view.findViewById(R.id.simpleViewFlipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_flipper_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.view_flipper_out));
        fatBurnButton = (Button) view.findViewById(R.id.fatBurnButton);
        cardioButton = (Button) view.findViewById(R.id.cardioButton);
        muscleButton = (Button) view.findViewById(R.id.muscleButton);
        createWorkout = (Button) view.findViewById(R.id.createWorkout);
        selectWorkout = (Button) view.findViewById(R.id.selectWorkout);
        okButtonDay = (Button) view.findViewById(R.id.okButtonDay);
        okButtonName = (Button) view.findViewById(R.id.okButtonName);

        inputDay = (EditText) view.findViewById(R.id.inputDay);
        inputName = (EditText) view.findViewById(R.id.inputName);

        bundle = new Bundle();

        createWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });

        selectWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutListFragment workoutListFragment = new WorkoutListFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentContainer,workoutListFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        fatBurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                workoutStyle="fat burn";
            }
        });

        cardioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                workoutStyle="cardio";
            }
        });

        muscleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                workoutStyle="muscle";
                Log.d("girdi", "onClick: girdi");
            }
        });

        okButtonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                workoutName = inputName.getText().toString();
            }
        });

        okButtonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalDay = Integer.parseInt(inputDay.getText().toString());
                bundle.putString("workoutStyle", workoutStyle);
                bundle.putString("workoutName", workoutName);
                bundle.putInt("totalDay", totalDay);
                bundle.putInt("remainingDay", totalDay);

                WorkoutFragment workoutFragment = new WorkoutFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                workoutFragment.setArguments(bundle);
                ft.replace(R.id.contentContainer,workoutFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        return view;
    }

}
