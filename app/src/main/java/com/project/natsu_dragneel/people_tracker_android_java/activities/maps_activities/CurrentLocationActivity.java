package com.project.natsu_dragneel.people_tracker_android_java.activities.maps_activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.MainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.activities.alert_activities.AlertCenterActivity;
import com.project.natsu_dragneel.people_tracker_android_java.activities.alert_activities.SendHelpAlertsActivity;
import com.project.natsu_dragneel.people_tracker_android_java.activities.follow_activity.FollowActivity;
import com.project.natsu_dragneel.people_tracker_android_java.activities.followers_activity.FollowersActivity;
import com.project.natsu_dragneel.people_tracker_android_java.activities.following_activity.FollowingActivity;
import com.project.natsu_dragneel.people_tracker_android_java.activities.invitation_activity.InvitationActivity;
import com.project.natsu_dragneel.people_tracker_android_java.services.LocationShareService;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentLocationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    FirebaseAuth auth;
    FirebaseUser user;
    LatLng latLngCurrent;
    DatabaseReference reference;
    TextView textName, textEmail;
    Marker marker;
    CircleImageView circleImageView;
    String myName, myEmail, myDate, mySharing, myProfileImage;
    Switch location_share_switch;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        location_share_switch=(Switch)findViewById(R.id.location_share_switch);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        textName = (TextView) header.findViewById(R.id.nameTxt);
        textEmail = (TextView) header.findViewById(R.id.emailTxt);
        circleImageView = (CircleImageView) header.findViewById(R.id.imageView2);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CurrentLocationActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1000);
            }

        }


        //my location data
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    myDate = dataSnapshot.child(user.getUid()).child("date").getValue().toString();
                    mySharing = dataSnapshot.child(user.getUid()).child("issharing").getValue().toString();
                    myEmail = dataSnapshot.child(user.getUid()).child("email").getValue().toString();
                    myName = dataSnapshot.child(user.getUid()).child("name").getValue().toString();
                    myProfileImage = dataSnapshot.child(user.getUid()).child("profile_image").getValue().toString();

                    if(mySharing.equals("false")){
                        location_share_switch.setChecked(false);
                    }
                    else if(mySharing.equals("true")){
                        location_share_switch.setChecked(true);
                    }
                    textName.setText(myName);
                    textEmail.setText(myEmail);
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.defaultprofile).into(circleImageView);
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not connect to the network. Please try again later", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();

        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.signout) {
            if (user != null) {
                auth.signOut();
                finish();

                Intent myIntent2 = new Intent(CurrentLocationActivity.this, LocationShareService.class);
                stopService(myIntent2);

                Intent i = new Intent(CurrentLocationActivity.this, MainActivity.class);
                startActivity(i);
            }
        } else if (id == R.id.joinCircle) {

            Intent myIntent = new Intent(CurrentLocationActivity.this, FollowActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.myCircle) {
            Intent intent = new Intent(CurrentLocationActivity.this, FollowersActivity.class);
            startActivity(intent);

        } else if (id == R.id.inviteFriends) {
            Intent myIntent = new Intent(CurrentLocationActivity.this, InvitationActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.joinedCircle) {
            Intent myIntent = new Intent(CurrentLocationActivity.this, FollowingActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.sendHelpAlert) {
            Intent myIntent = new Intent(CurrentLocationActivity.this, SendHelpAlertsActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.alertCenter) {
            Intent myIntent = new Intent(CurrentLocationActivity.this, AlertCenterActivity.class);
            startActivity(myIntent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng kathmandu = new LatLng(27.7172, 85.3240);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kathmandu, 12));

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(7000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);

        latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
        if (marker == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            //marker = mMap.addMarker(new MarkerOptions().position(latLngCurrent).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, 15));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


        } else {
            marker.setPosition(latLngCurrent);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, 15));
        }


    }


    public void share_location_switch(View v){
        Boolean switch_state=location_share_switch.isChecked();
        if(switch_state==true){
            if (isServiceRunning(getApplicationContext(), LocationShareService.class)) {
                Toast.makeText(getApplicationContext(), "You are already sharing your location.", Toast.LENGTH_SHORT).show();
            } else {
                Intent myIntent = new Intent(CurrentLocationActivity.this, LocationShareService.class);
                Toast.makeText(getApplicationContext(), "Location sharing is now started", Toast.LENGTH_SHORT).show();
                startService(myIntent);
            }
        }
        else if(switch_state==false){
            Intent myIntent2 = new Intent(CurrentLocationActivity.this, LocationShareService.class);
            stopService(myIntent2);
            reference.child(user.getUid()).child("issharing").setValue("false")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Location sharing is now stopped", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(getApplicationContext(), "Location sharing could not be stopped", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:

                if (isServiceRunning(getApplicationContext(), LocationShareService.class)) {
                    Toast.makeText(getApplicationContext(), "You are already sharing your location.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent myIntent = new Intent(CurrentLocationActivity.this, LocationShareService.class);
                    startService(myIntent);
                }


                break;
            case R.id.action_stop:
                Intent myIntent2 = new Intent(CurrentLocationActivity.this, LocationShareService.class);
                stopService(myIntent2);
                reference.child(user.getUid()).child("issharing").setValue("false")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Location sharing is now stopped", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(getApplicationContext(), "Location sharing could not be stopped", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                break;


        }
        return super.onOptionsItemSelected(item);
    }*/

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_location, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
                onConnected(null);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean isServiceRunning(Context c, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }

    public void fetch_location(View v) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, 15));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }
}
