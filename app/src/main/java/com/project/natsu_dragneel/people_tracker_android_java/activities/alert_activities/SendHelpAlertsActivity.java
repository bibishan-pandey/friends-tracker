package com.project.natsu_dragneel.people_tracker_android_java.activities.alert_activities;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.activities.maps_activities.MyNavigationTutorial;
import com.project.natsu_dragneel.people_tracker_android_java.classes.FollowClass;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class SendHelpAlertsActivity extends AppCompatActivity {

    TextView t1_CounterTxt;
    int countValue = 5;
    Thread myThread;
    DatabaseReference circlereference,usersReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String memberUserId;
    ArrayList<String> userIDsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_help_alerts);
        t1_CounterTxt = findViewById(R.id.textView9);
        auth = FirebaseAuth.getInstance();

        userIDsList = new ArrayList<>();
        user = auth.getCurrentUser();

        circlereference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("CircleMembers");
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        myThread = new Thread(new ServerThread());
        myThread.start();



    }





    private class ServerThread implements Runnable
    {
        @Override
        public void run() {
            try {
                //do some heavy task here on main separate thread like: Saving files in directory, any server operation or any heavy task

                ///Once this task done and if you want to update UI the you can update UI operation on runOnUiThread method like this:

                while(countValue!=0) {

                    sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t1_CounterTxt.setText(String.valueOf(countValue));
                            countValue = countValue - 1;

                        }
                    });




                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        circlereference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userIDsList.clear();
                                for (DataSnapshot dss: dataSnapshot.getChildren())
                                {
                                    memberUserId = dss.child("circlememberid").getValue(String.class);
                                    userIDsList.add(memberUserId);
                                }


                                if(userIDsList.isEmpty())
                                {
                                    Toast.makeText(getApplicationContext(),"No circle members. Please add some one to your circle.",Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    FollowClass circleJoin = new FollowClass(user.getUid());
                                    for(int i =0;i<userIDsList.size();i++)
                                    {

                                        usersReference.child(userIDsList.get(i).toString()).child("HelpAlerts").child(user.getUid()).setValue(circleJoin)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(getApplicationContext(),"Alerts sent successfully.",Toast.LENGTH_SHORT).show();

                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(getApplicationContext(),"Could not send alerts. Please try again later.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }









                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });


            }
            catch (Exception e) {
                //print the error here

            }
        }
    }


    public void setCancel(View v)
    {

        Toast.makeText(getApplicationContext(),"Alert cancelled.",Toast.LENGTH_SHORT).show();
        myThread.interrupt();
        Intent myIntent = new Intent(SendHelpAlertsActivity.this,MyNavigationTutorial.class);
        startActivity(myIntent);
        finish();
    }



}
