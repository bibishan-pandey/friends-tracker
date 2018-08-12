package com.project.natsu_dragneel.people_tracker_android_java;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class InviteCodeActivity extends AppCompatActivity {

    String name,email,password,date,isSharing,code;
    Uri imageURI;

    TextView t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        t1=(TextView)findViewById(R.id.textView_circle_code);
        Intent myIntent=getIntent();

        if(myIntent!=null){
            name=myIntent.getStringExtra("Name");
            email=myIntent.getStringExtra("Email");
            password=myIntent.getStringExtra("Password");
            date=myIntent.getStringExtra("Date");
            isSharing=myIntent.getStringExtra("isSharing");
            code=myIntent.getStringExtra("Code");
            imageURI=myIntent.getParcelableExtra("ImageURI");
        }
        t1.setText(code);
    }
}
