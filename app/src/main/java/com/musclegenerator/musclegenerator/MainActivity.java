package com.musclegenerator.musclegenerator;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
     public static BottomBar bottomBar;
     private FirebaseAuth auth;
     private FirebaseAuth.AuthStateListener authListener;
    FirebaseUser Cuser;
    DatabaseReference dref;
    static boolean gir = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dref = FirebaseDatabase.getInstance().getReference();
        Cuser = FirebaseAuth.getInstance().getCurrentUser();
        final String token = FirebaseInstanceId.getInstance().getToken();

        if(Cuser != null)
        dref.child("userBilgileri").child(Cuser.getUid()).child("token").setValue(token);

        String a = getIntent().getStringExtra("USER");
        if(a != null){

            if (a.equals("1")) {

                dref.child("userBilgileri").child(Cuser.getUid()).child("name").setValue(getIntent().getStringExtra("NAME"));
                dref.child("userBilgileri").child(Cuser.getUid()).child("lastname").setValue(getIntent().getStringExtra("LASTNAME"));
                dref.child("userBilgileri").child(Cuser.getUid()).child("age").setValue(getIntent().getStringExtra("AGE"));
                dref.child("userBilgileri").child(Cuser.getUid()).child("profilepicture").setValue("false");
                dref.child("userBilgileri").child(Cuser.getUid()).child("signupdate").setValue(getIntent().getStringExtra("DATE"));
                dref.child("userBilgileri").child(Cuser.getUid()).child("workoutday").setValue(-1);
                dref.child("userBilgileri").child(Cuser.getUid()).child("currentworkout").setValue(-1);

                InfoFragment workoutFragment = new InfoFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.contentContainer,workoutFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                gir = false;

            }else{
                gir = true;
            }
        }else{
            gir = true;
        }


        auth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //get current user
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                         startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setActiveTabColor(getResources().getColor(R.color.white));
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.home) {

                    if(InfoFragment.isFinishedforMain || gir) {


                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(getResources(), R.color.homeColor, null)));
                        WorkoutViewFragment workoutViewFragment = new WorkoutViewFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.contentContainer, workoutViewFragment);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }


                }else if(tabId == R.id.profile){
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(getResources(), R.color.profileColor, null)));
                         ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                             ft.replace(R.id.contentContainer,profileFragment);
                             ft.addToBackStack(null);
                             ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                             ft.commit();
                }else if(tabId == R.id.calendar){
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(getResources(), R.color.calenderColor, null)));

                    calendarFunc();


                }else if(tabId == R.id.workout){
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(getResources(), R.color.workoutColor, null)));
                    WorkoutPreferencesFragment workoutPreferencesFragment = new WorkoutPreferencesFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.contentContainer,workoutPreferencesFragment);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();



                }else if(tabId == R.id.inbox){
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ResourcesCompat.getColor(getResources(), R.color.inboxColor, null)));
                    GeneralInboxFragment generalInboxFragment = new GeneralInboxFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.contentContainer,generalInboxFragment);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public void calendarFunc(){

          Resources res = getResources();
          final Drawable drawable = res.getDrawable(R.drawable.check);
        final ArrayList<Date> workoutdates = new ArrayList<>();


        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CaldroidFragment calenderFragment = new CaldroidFragment();
                String signdate = dataSnapshot.child("userBilgileri").child(Cuser.getUid()).child("signupdate").getValue().toString();

                Date mindate = getdatefrom(signdate);
                    calenderFragment.setMinDate(mindate);



                Bundle args = new Bundle();

                Calendar cal = Calendar.getInstance();
                args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH)+1);
                args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));

                calenderFragment.setArguments(args);


                for(DataSnapshot userrec : dataSnapshot.child("userRecords").child(Cuser.getUid()).getChildren()){

                    String time = userrec.getKey();

                    Date date = new Date();
                    final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
                    try {
                         date = formatter.parse(time);
                    }catch (Exception e){e.printStackTrace();}

                    calenderFragment.setBackgroundDrawableForDate(drawable,date);

                    workoutdates.add(date);

                }


                final CaldroidListener listener = new CaldroidListener() {
                    @Override
                    public void onSelectDate(Date date, View view) {

                        if(workoutdates.contains(date)) {

                            displayDateWorkoutFragment dispfragment = new displayDateWorkoutFragment();

                            Bundle bundle = new Bundle();

                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(date);
                            String putdate = getStringofDate(currentDateTimeString);

                            Toast.makeText(MainActivity.this, "Hello" + putdate,
                                    Toast.LENGTH_LONG).show();

                            bundle.putString("date",putdate);

                            dispfragment.setArguments(bundle);

                            FragmentManager fm = getSupportFragmentManager();

                            dispfragment.show(fm, "dateworkout");
                        }


                    }
                };
                calenderFragment.setCaldroidListener(listener);




                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.contentContainer,calenderFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

/*



*/

    }

    public Date getdatefrom(String date) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Date mindate = new Date();
        try {

            String delims = "[,]";
            String[] tokens = date.split(delims);

            String[] dmtoken = tokens[0].split("\\s+");
            String[] yeartoken = tokens[1].split("\\s+");

            String finalyear = dmtoken[1] + " " + dmtoken[0] + " " + yeartoken[1];

             mindate = formatter.parse(finalyear);
        }
        catch (Exception e){e.printStackTrace();}

            return mindate;
    }

    public String getStringofDate(String date) {


        String delims = "[,]";
        String[] tokens = date.split(delims);

        String[] dmtoken = tokens[0].split("\\s+");
        String[] yeartoken = tokens[1].split("\\s+");

        String finalyear = dmtoken[1] + " " + dmtoken[0] + " " + yeartoken[1];


        return finalyear;
    }


}
