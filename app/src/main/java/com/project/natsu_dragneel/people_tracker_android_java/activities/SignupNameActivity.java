package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupNameActivity extends AppCompatActivity {
    EditText signup_name_edittext;
    CircleImageView circleImageView;
    Button signup_name_button;
    String email, password;
    Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_name);
        signup_name_edittext = (EditText) findViewById(R.id.signup_name_edittext);
        signup_name_button = (Button) findViewById(R.id.signup_name_button);

        signup_name_button.setEnabled(false);
        signup_name_button.setBackgroundColor(Color.parseColor("#faebd7"));

        circleImageView = (CircleImageView) findViewById(R.id.profile_image);

        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("Email");
            password = intent.getStringExtra("Password");
        }
        signup_name_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    signup_name_button.setEnabled(true);
                    signup_name_button.setBackground(getResources().getDrawable(R.drawable.button_shape_normal));
                } else {
                    signup_name_button.setEnabled(false);
                    signup_name_button.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void generateCode(View v) {
        if (signup_name_edittext.getText().toString().length() > 0) {
            Date curDate = new Date();
            Random rnd = new Random();
            int n = 100000 + rnd.nextInt(900000);

            final String code = String.valueOf(n);
            if (resultUri != null) {
                Intent myIntent = new Intent(SignupNameActivity.this, SignupInviteCodeActivity.class);
                myIntent.putExtra("Name", signup_name_edittext.getText().toString());
                myIntent.putExtra("Email", email);
                myIntent.putExtra("Password", password);
                myIntent.putExtra("Date", "na");
                myIntent.putExtra("isSharing", "false");
                myIntent.putExtra("Code", code);
                myIntent.putExtra("ImageURL", resultUri);
                startActivity(myIntent);
                Toast.makeText(getApplicationContext(), resultUri.toString(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Please choose your profile picture.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openGallery(View v) {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, 12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            CropImage.activity(uri)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                circleImageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
