package com.project.natsu_dragneel.people_tracker_android_java.activities.signup_activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.project.natsu_dragneel.people_tracker_android_java.MainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.activities.signin_activities.SigninEmailActivity;

import java.util.Objects;

@SuppressWarnings({"deprecation", "unused"})
public class SignupEmailActivity extends AppCompatActivity {

    private static final String TAG = SignupEmailActivity.class.getSimpleName();

    private static final String account_exists = "You already have an account. Please signin.";
    private static final String wait = "Please wait";

    private EditText signup_email_editText;
    private Button signup_email_next_button;
    private ProgressDialog dialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_email);
        signup_email_editText = findViewById(R.id.signup_email_editText);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        signup_email_next_button = findViewById(R.id.signup_next_click);
        signup_email_next_button.setEnabled(false);
        signup_email_next_button.setBackgroundColor(Color.parseColor("#faebd7"));

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        signup_email_editText.addTextChangedListener(new TextWatcher() {
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
                if (signup_email_editText.getText().toString().matches(emailPattern) && s.length() > 0) {
                    signup_email_next_button.setEnabled(true);
                    signup_email_next_button.setBackgroundColor(Color.parseColor("#f05545"));
                } else {
                    signup_email_next_button.setEnabled(false);
                    signup_email_next_button.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void email_exist_check(View v) {
        dialog.setMessage(wait);
        dialog.show();

        auth.fetchProvidersForEmail(signup_email_editText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        dialog.dismiss();
                        boolean check = false;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            check = !Objects.requireNonNull(task.getResult().getProviders()).isEmpty();
                        }
                        if (!check) {
                            Intent myIntent = new Intent(SignupEmailActivity.this, SignupPasswordActivity.class);
                            myIntent.putExtra("Email", signup_email_editText.getText().toString());
                            startActivity(myIntent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), account_exists, Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(SignupEmailActivity.this, SigninEmailActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(SignupEmailActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void back_image_button(View v) {
        finish();
        Intent intent = new Intent(SignupEmailActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
