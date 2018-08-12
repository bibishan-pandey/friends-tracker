package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupNameActivity extends Activity {

    String email;
    String password;
    final String choose_profile_pic="Choose profile picture";
    Uri resultURI;
    EditText editText_name_signup;
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_name);
        interface_builder();
    }

    public void interface_builder(){
        editText_name_signup=(EditText)findViewById(R.id.editText_name_signup);

        circleImageView=(CircleImageView)findViewById(R.id.circleImageView);
        Intent intent=getIntent();
        if(intent!=null){
            email=intent.getStringExtra("Email");
            password=intent.getStringExtra("Password");
        }
    }

    public void generate_code(View v){
        Date myDate=new Date();
        SimpleDateFormat format1=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss a", Locale.getDefault());
        String date=format1.format(myDate);
        Random r=new Random();
        int n=100000+r.nextInt(900000);
        String code=String.valueOf(n);
        if(resultURI!=null){
            Intent myIntent=new Intent(SignupNameActivity.this,SignupInviteCodeActivity.class);
            myIntent.putExtra("Name",editText_name_signup.getText().toString());
            myIntent.putExtra("Email",email);
            myIntent.putExtra("Password",password);
            myIntent.putExtra("Date",date);
            myIntent.putExtra("isSharing","false");
            myIntent.putExtra("Code",code);
            myIntent.putExtra("ImageURI",resultURI);
            startActivity(myIntent);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(),choose_profile_pic,Toast.LENGTH_LONG).show();
        }
    }

    public void select_image(View v){
        Intent i=new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i,12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==12 && resultCode==RESULT_OK && data!=null){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            crop_image(requestCode,resultCode,data);
        }
    }

    private void crop_image(int requestCode, int resultCode, Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            resultURI = result.getUri();
            circleImageView.setImageURI(resultURI);
        }
        else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
        }
    }
}
