package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.adapters.HelpAlertsAdapter;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

import java.util.ArrayList;

public class AlertCenterActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerView.Adapter recycleradapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<CreateUser> myList;
    FirebaseAuth auth;
    FirebaseUser user;
    String memberUserId;

    CreateUser createUser;
    DatabaseReference reference,userReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_center);
        recyclerView = (RecyclerView)findViewById(R.id.alertRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Help Center");
        setSupportActionBar(toolbar);



        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("HelpAlerts");
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        myList = new ArrayList<>();


        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myList.clear();

                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dss: dataSnapshot.getChildren())
                    {
                        memberUserId = dss.child("circlememberid").getValue().toString();

                        userReference.child(memberUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                createUser = dataSnapshot.getValue(CreateUser.class);
                                myList.add(createUser);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(),databaseError.getCode(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Toast.makeText(getApplicationContext(),"Showing alerts",Toast.LENGTH_SHORT).show();
                    recycleradapter = new HelpAlertsAdapter(myList,getApplicationContext());

                    recyclerView.setAdapter(recycleradapter);
                    recycleradapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Alert list is empty",Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(null);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
