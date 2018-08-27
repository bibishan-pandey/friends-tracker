package com.project.natsu_dragneel.people_tracker_android_java.activities.signin_activities;

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

import java.util.Objects;

@SuppressWarnings({"deprecation", "unused"})
public class SigninEmailActivity extends AppCompatActivity {

    private static final String TAG = SigninEmailActivity.class.getSimpleName();

    private static final String wait = "Please wait!";
    private static final String email_not_exists = "This email does not exist. Please create an account first";

    private EditText signin_email_editText;
    private Button signin_email_next_button;
    private ProgressDialog dialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_email);
        signin_email_editText = findViewById(R.id.signin_email_editText);
        dialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        signin_email_next_button = findViewById(R.id.signin_next_click);
        signin_email_next_button.setEnabled(false);
        signin_email_next_button.setBackgroundColor(Color.parseColor("#faebd7"));
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        signin_email_editText.addTextChangedListener(new TextWatcher() {
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
                if (signin_email_editText.getText().toString().matches(emailPattern) && s.length() > 0) {
                    signin_email_next_button.setEnabled(true);
                    signin_email_next_button.setBackgroundColor(Color.parseColor("#f05545"));
                } else {
                    signin_email_next_button.setEnabled(false);
                    signin_email_next_button.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void email_exists_or_not(View v) {
        dialog.setMessage(wait);
        dialog.show();
        auth.fetchProvidersForEmail(signin_email_editText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        boolean check = false;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            check = !Objects.requireNonNull(task.getResult().getProviders()).isEmpty();
                        }
                        if (!check) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), email_not_exists, Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            Intent myIntent = new Intent(SigninEmailActivity.this, SigninPasswordActivity.class);
                            myIntent.putExtra("email_login", signin_email_editText.getText().toString());
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(SigninEmailActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void back_image_button(View v) {
        finish();
        Intent intent = new Intent(SigninEmailActivity.this, MainActivity.class);
        startActivity(intent);
    }
}