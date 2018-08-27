package com.project.natsu_dragneel.people_tracker_android_java.activities.alert_activities;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.project.natsu_dragneel.people_tracker_android_java.classes.FollowClass;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

@SuppressWarnings("unused")
public class SendHelpAlertsActivity extends AppCompatActivity {

    private static final String TAG=SendHelpAlertsActivity.class.getSimpleName();

    private static final String no_followers="No follower members.";
    private static final String alert_sent="Alerts sent successfully.";
    private static final String alert_not_sent="Could not send alerts. Please try again later.";
    private static final String alert_cancel="Alert cancelled.";

    private TextView counterTextView;
    private int countValue = 5;
    private Thread myThread;
    private DatabaseReference circleReference;
    private DatabaseReference usersReference;
    private FirebaseUser user;
    private String memberUserId;
    private ArrayList<String> userIDsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_help_alerts);
        counterTextView = findViewById(R.id.count_down_textview);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        userIDsList = new ArrayList<>();
        user = auth.getCurrentUser();

        circleReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(user.getUid())
                .child("FollowerMembers");
        usersReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        myThread = new Thread(new ServerThread());
        myThread.start();
    }

    @SuppressWarnings("RedundantStringToString")
    private class ServerThread implements Runnable
    {
        @Override
        public void run() {
            try {
                while(countValue!=0) {
                    sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            counterTextView.setText(String.valueOf(countValue));
                            countValue = countValue - 1;
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        circleReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userIDsList.clear();
                                for (DataSnapshot dss: dataSnapshot.getChildren())
                                {
                                    memberUserId = dss.child("MemberId").getValue(String.class);
                                    userIDsList.add(memberUserId);
                                }
                                if(userIDsList.isEmpty())
                                {
                                    Toast.makeText(getApplicationContext(),no_followers,Toast.LENGTH_SHORT).show();
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
                                                            finish();
                                                            Toast.makeText(getApplicationContext(),alert_sent,Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(getApplicationContext(),alert_not_sent,Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            catch (Exception e) {
                Log.d(TAG, "run: catch");
            }
        }
    }

    public void setCancel(View v)
    {
        Toast.makeText(getApplicationContext(),alert_cancel,Toast.LENGTH_SHORT).show();
        myThread.interrupt();
        finish();
    }

    public void back_image_button(View v){
        myThread.interrupt();
        finish();
    }

    @Override
    public void onBackPressed() {
        myThread.interrupt();
        finish();
    }
}
