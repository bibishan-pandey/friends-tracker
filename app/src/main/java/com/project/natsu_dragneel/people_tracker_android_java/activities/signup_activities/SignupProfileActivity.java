package com.project.natsu_dragneel.people_tracker_android_java.activities.signup_activities;
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
import com.project.natsu_dragneel.people_tracker_android_java.activities.invitation_activity.InvitationActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupProfileActivity extends AppCompatActivity {

    EditText signup_profile_edittext;
    CircleImageView circleImageView;
    Button signup_profile_next_button;
    String email,password;

    Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_profile);
        signup_profile_edittext = (EditText)findViewById(R.id.signup_profile_edittext);
        signup_profile_next_button = (Button)findViewById(R.id.signup_profile_next_button);

        signup_profile_next_button.setEnabled(false);
        signup_profile_next_button.setBackgroundColor(Color.parseColor("#faebd7"));
        circleImageView = (CircleImageView)findViewById(R.id.profile_image);

        Intent intent = getIntent();
        if (intent!=null) {
            email = intent.getStringExtra("email");
            password = intent.getStringExtra("password");
        }
        signup_profile_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0)
                {
                    signup_profile_next_button.setEnabled(true);
                    signup_profile_next_button.setBackgroundColor(Color.parseColor("#9C27B0"));
                }
                else
                {
                    signup_profile_next_button.setEnabled(false);
                    signup_profile_next_button.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });
    }

    public void generate_code_profile(View v) {
        if (signup_profile_edittext.getText().toString().length() > 0) {
            Date curDate = new Date();
            Random rnd = new Random();
            int n = 100000 + rnd.nextInt(900000);
            final String code = String.valueOf(n);
            if(resultUri !=null)
            {
                Intent myIntent = new Intent(SignupProfileActivity.this, InvitationActivity.class);
                myIntent.putExtra("name", signup_profile_edittext.getText().toString());
                myIntent.putExtra("email", email);
                myIntent.putExtra("password", password);
                myIntent.putExtra("date", "na");
                myIntent.putExtra("issharing", "false");
                myIntent.putExtra("code", code);

                myIntent.putExtra("imageUri",resultUri);
                startActivity(myIntent);
                Toast.makeText(getApplicationContext(),resultUri.toString(),Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"You must choose your profile picture.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void open_gallery(View v)
    {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, 12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12 && resultCode==RESULT_OK && data!=null)
        {
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
