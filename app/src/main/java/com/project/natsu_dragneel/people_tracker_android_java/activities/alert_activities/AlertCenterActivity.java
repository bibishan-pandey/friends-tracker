package com.project.natsu_dragneel.people_tracker_android_java.activities.alert_activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.adapters.alert_adapter.HelpAlertsAdapter;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unused")
public class AlertCenterActivity extends AppCompatActivity {

    private static final String show_alerts="Showing alerts";
    private static final String no_alerts="No alerts";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private ArrayList<CreateUser> myList;
    private String memberUserId;

    private CreateUser createUser;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_center);
        recyclerView = findViewById(R.id.alertRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(user.getUid())
                .child("HelpAlerts");
        userReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        myList = new ArrayList<>();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myList.clear();
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dss: dataSnapshot.getChildren())
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            memberUserId = Objects.requireNonNull(dss.child("MemberId").getValue()).toString();
                        }

                        userReference.child(memberUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                createUser = dataSnapshot.getValue(CreateUser.class);
                                myList.add(createUser);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(),databaseError.getCode(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Toast.makeText(getApplicationContext(),show_alerts,Toast.LENGTH_SHORT).show();
                    recyclerAdapter = new HelpAlertsAdapter(myList,getApplicationContext());

                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),no_alerts,Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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

    public void back_image_button(View v){
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
