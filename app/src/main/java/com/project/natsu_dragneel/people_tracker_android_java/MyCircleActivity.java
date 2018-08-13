package com.project.natsu_dragneel.people_tracker_android_java;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.classes.CreateUser;
import com.project.natsu_dragneel.people_tracker_android_java.classes.MembersAdapter;

import java.util.ArrayList;

public class MyCircleActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    String circle_member_id;

    FirebaseAuth auth;
    FirebaseUser user;

    CreateUser createUser;

    ArrayList<CreateUser> name_list;

    DatabaseReference reference,user_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_circle);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        name_list=new ArrayList<>();

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        user_reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference=FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("CircleMembers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name_list.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot dss:dataSnapshot.getChildren()){
                        circle_member_id=dss.child("circle_member_id").getValue(String.class);
                        user_reference.child(circle_member_id)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        createUser=dataSnapshot.getValue(CreateUser.class);
                                        name_list.add(createUser);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        adapter=new MembersAdapter(name_list,getApplicationContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
