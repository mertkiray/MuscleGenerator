package com.musclegenerator.musclegenerator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputName,inputLastname,inputAge;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ImageView picture;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference dref;
    SharedPreferences settings;
    FirebaseUser user;
    HashMap<String, Object> userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);

       // picture = (ImageView) findViewById(R.id.picturePreview);
        inputName = (EditText) findViewById(R.id.name);
        inputLastname = (EditText) findViewById(R.id.lastname);
        inputAge = (EditText) findViewById(R.id.score);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);



        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));


            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String name = inputName.getText().toString().trim();
                String lastname = inputLastname.getText().toString().trim();

                String ageString = inputAge.getText().toString().trim();
                //int age = Integer.parseInt(ageString);


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter your name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lastname)) {
                    Toast.makeText(getApplicationContext(), "Enter your last name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(ageString)) {
                    Toast.makeText(getApplicationContext(), "Enter your age and don't lie!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }




                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                //  dref.child("userBilgileri").child(mUID).child("name").setValue(inputName.toString());

                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();


                                } else {
                                    loginAfterSignUp();
                                    //  startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    //  finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
    public void loginAfterSignUp() {
        String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(SignupActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Bilgilerini bir sonraki aktiviteye yolluyorum. Orada da database e yaziyorum.
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            intent.putExtra("USER","1");
                            intent.putExtra("NAME",inputName.getText().toString());
                            intent.putExtra("LASTNAME",inputLastname.getText().toString());
                            intent.putExtra("AGE",inputAge.getText().toString());
                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                            //Log.d("DATE", "onComplete: "+currentDateTimeString);
                            intent.putExtra("DATE", currentDateTimeString);


                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}