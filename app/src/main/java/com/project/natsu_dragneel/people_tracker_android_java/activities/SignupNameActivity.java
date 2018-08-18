package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupNameActivity extends Activity {

    String email;
    String password;
    final String choose_profile_pic="Choose profile picture";
    final String please_wait="Please wait...";

    Uri resultUri;
    EditText editText_name_signup;
    CircleImageView circleImageView;
    Button button_next_name_signup;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_name);
        interface_builder();
    }

    public void interface_builder(){
        editText_name_signup=(EditText)findViewById(R.id.editText_name_signup);
        button_next_name_signup=(Button)findViewById(R.id.button_next_name_signup);
        //button_next_name_signup.setEnabled(false);
        //button_next_name_signup.setBackgroundColor(Color.parseColor("#faebd7"));

        circleImageView=(CircleImageView)findViewById(R.id.circleImageView);
        dialog = new ProgressDialog(this);
        Intent intent=getIntent();
        if(intent!=null){
            email=intent.getStringExtra("email");
            password=intent.getStringExtra("password");
        }

        /*
        editText_name_signup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0){
                    button_next_name_signup.setEnabled(true);
                    button_next_name_signup.setBackgroundColor(Color.parseColor("FF00FF"));
                }
                else{
                    button_next_name_signup.setEnabled(false);
                    button_next_name_signup.setBackgroundColor(Color.parseColor("#faebd7"));
                }
            }
        });*/
    }

    public void generate_code(View v){
        dialog.setMessage(please_wait);
        dialog.show();
        if(editText_name_signup.getText().toString().length()>0){
            Date curDate = new Date();
            Random rnd = new Random();
            int n = 100000 + rnd.nextInt(900000);
            final String code = String.valueOf(n);
            if(resultUri!=null){
                Intent intent=new Intent(SignupNameActivity.this,SignupInviteCodeActivity.class);
                intent.putExtra("name",editText_name_signup.getText().toString());
                intent.putExtra("email",email);
                intent.putExtra("password",password);
                intent.putExtra("date","n/a");
                intent.putExtra("isSharing","false");
                intent.putExtra("code",code);
                intent.putExtra("imageUri",resultUri);
                dialog.dismiss();
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),choose_profile_pic,Toast.LENGTH_LONG).show();
            }
        }
    }

    public void openGallery(View v){
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, 12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==12 && resultCode==RESULT_OK && data!=null){
            CropImage
                    .activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            crop_image(resultCode,data);
        }
    }

    private void crop_image(int resultCode, Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            resultUri = result.getUri();
            circleImageView.setImageURI(resultUri);
        }
        else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
        }
    }
}
