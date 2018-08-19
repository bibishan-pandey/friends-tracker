package com.project.natsu_dragneel.people_tracker_android_java.activities;

import android.Manifest;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.natsu_dragneel.people_tracker_android_java.AccountMainActivity;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.services.LocationShareService;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserLocationMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
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
    Double latitude,longitude;
    CircleImageView circleImageView;
    String myName, myEmail, myDate, mySharing,myProfileImage;

    Toolbar toolbar;

    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "UserLocationMainActivity";
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("People Tracker");
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        textName = (TextView) header.findViewById(R.id.nameTxt);
        textEmail = (TextView) header.findViewById(R.id.emailTxt);
        circleImageView = (CircleImageView)header.findViewById(R.id.user_image);
        //   aSwitch.setOnCheckedChangeListener(getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UserLocationMainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1000);
            }
        }
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    myDate = dataSnapshot.child(user.getUid()).child("Date").getValue().toString();
                    mySharing = dataSnapshot.child(user.getUid()).child("isSharing").getValue().toString();
                    myEmail = dataSnapshot.child(user.getUid()).child("Email").getValue().toString();
                    myName = dataSnapshot.child(user.getUid()).child("Name").getValue().toString();
                    myProfileImage = dataSnapshot.child(user.getUid()).child("ProfileImage").getValue().toString();

                    textName.setText(myName);
                    textEmail.setText(myEmail);
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.icon_profile).into(circleImageView);
                }catch(NullPointerException e)
                {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Could not connect to the network. Please try again later",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_location_main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_follow) {
            Intent intent = new Intent(UserLocationMainActivity.this, FollowActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_followers) {
            Intent intent=new Intent(UserLocationMainActivity.this,FollowersActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_followed) {
            Intent myIntent = new Intent(UserLocationMainActivity.this, FollowedActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.nav_alertCenter) {
            //Intent myIntent = new Intent(UserLocationMainActivity.this, SendHelpAlertsActivity.class);
            //startActivity(myIntent);
        } else if (id == R.id.nav_inviteMembers) {
            //Intent intent = new Intent(UserLocationMainActivity.this, SignupInviteCodeActivity.class);
            //startActivity(intent);
        } else if (id == R.id.nav_shareLocation) {
            String uri = "http://maps.google.com/maps?saddr="+latitude+","+longitude;
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String ShareSub = "Here is my location";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.nav_signOut) {
            if (user != null) {
                auth.signOut();
                Intent intent = new Intent(UserLocationMainActivity.this, AccountMainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        final Task location=mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Location loc=(Location)task.getResult();
                latitude=loc.getLatitude();
                longitude=loc.getLongitude();
                moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM);
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(latLngCurrent).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, 15));
        } else {
            marker.setPosition(latLngCurrent);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, 15));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                if(isServiceRunning(getApplicationContext(),LocationShareService.class))
                {
                    Toast.makeText(getApplicationContext(),"You are already sharing your location.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent myIntent = new Intent(UserLocationMainActivity.this,LocationShareService.class);
                    startService(myIntent);
                }
                break;
            case R.id.action_stop:
                Intent myIntent2 = new Intent(UserLocationMainActivity.this,LocationShareService.class);
                stopService(myIntent2);
                reference.child(user.getUid()).child("isSharing").setValue("false")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"Location sharing is now stopped",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Location sharing could not be stopped",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==1000)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getApplicationContext(),"Location permission granted.",Toast.LENGTH_SHORT).show();
                onConnected(null);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean isServiceRunning(Context c, Class<?> serviceClass)
    {
        ActivityManager activityManager = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for(ActivityManager.RunningServiceInfo runningServiceInfo : services)
        {
            if(runningServiceInfo.service.getClassName().equals(serviceClass.getName()))
            {
                return true;
            }
        }
        return false;
    }

    public void geofencing(View v) {
        //Intent myIntent = new Intent(UserLocationMainActivity.this, SignupInviteCodeActivity.class);
        //startActivity(myIntent);
    }
}
