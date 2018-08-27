package com.project.natsu_dragneel.people_tracker_android_java.activities.signin_activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.MainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.activities.maps_activities.CurrentLocationActivity;
import com.project.natsu_dragneel.people_tracker_android_java.security.SHA_Conversion;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@SuppressWarnings("unused")
public class SigninPasswordActivity extends AppCompatActivity {

    private static final String TAG = SigninPasswordActivity.class.getSimpleName();

    private static final String wait = "Please wait. Signing in.";
    private static final String not_verified = "This email is not verified yet. Please check your email";
    private static final String wrong_credentials = "Wrong username/password";

    private EditText signin_password_editText;
    private Button signin_password_next_button;
    private String signin_password_secure;
    private TextView hashed_textView;
    private String server_password;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private FirebaseUser user;
    private String email;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_password);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        signin_password_editText = findViewById(R.id.signin_password_editText);
        hashed_textView=findViewById(R.id.hashed_text);
        signin_password_next_button = findViewById(R.id.signin_next_click);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email_login");
        }

        signin_password_next_button.setEnabled(false);
        signin_password_next_button.setBackgroundColor(Color.parseColor("#faebd7"));

        signin_password_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 6) {
                    signin_password_next_button.setEnabled(true);
                    signin_password_next_button.setBackgroundColor(Color.parseColor("#f05545"));
                } else {
                    signin_password_next_button.setEnabled(false);
                    signin_password_next_button.setBackgroundColor(Color.parseColor("#faebd7"));
                }
                try {
                    signin_password_secure = SHA_Conversion.hashPassword(signin_password_editText.getText().toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                hashed_textView.setText(signin_password_secure);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    server_password = dataSnapshot.child(user.getUid()).child("Password").getValue().toString();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Could not connect to the network. Please try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Login(View v) {
        dialog.setMessage(wait);
        dialog.show();
        if (signin_password_editText.getText().toString().length() >= 6) {
            signin_process();
        }
    }

    private void signin_process() {
        auth.signInWithEmailAndPassword(email, signin_password_secure)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = auth.getCurrentUser();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                if (Objects.requireNonNull(user).isEmailVerified()) {
                                    dialog.dismiss();
                                    finish();
                                    Intent myIntent = new Intent(SigninPasswordActivity.this, CurrentLocationActivity.class);
                                    startActivity(myIntent);
                                } else {
                                    dialog.dismiss();
                                    finish();
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(getApplicationContext(), not_verified, Toast.LENGTH_SHORT).show();
                                    Intent myIntent = new Intent(SigninPasswordActivity.this, MainActivity.class);
                                    startActivity(myIntent);
                                }
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), wrong_credentials, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(SigninPasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void back_image_button(View v) {
        finish();
        Intent intent = new Intent(SigninPasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }
}