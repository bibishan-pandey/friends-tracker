package com.project.natsu_dragneel.people_tracker_android_java.activities.maps_activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LiveLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mMap;
    LatLng friendLatLng;
    GeoFire geoFire;
    Button geofence_click;
    Double geofence_radius=500.0;
    Circle mapcircle;
    VerticalSeekBar mVerticalSeekBar;

    private GoogleApiClient mGoogleApiClient;

    String latitude, longitude, name, userid, prevdate, prevImage;
    TextView user_name_textview;
    Marker marker;
    DatabaseReference reference;
    String myImage;

    String myName, myLat, myLng, myDate;
    ArrayList<String> mKeys;
    MarkerOptions myOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_location);

        geofence_click=(Button)findViewById(R.id.geofence_click);

        mVerticalSeekBar= (VerticalSeekBar) findViewById(R.id.vertical_seekbar);
        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(i), 1500, null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        user_name_textview = (TextView) findViewById(R.id.user_name_textview);
        myOptions = new MarkerOptions();
        Intent intent = getIntent();
        mKeys = new ArrayList<>();
        if (intent != null) {
            latitude = intent.getStringExtra("latitude");
            longitude = intent.getStringExtra("longitude");
            name = intent.getStringExtra("Name");
            userid = intent.getStringExtra("UserId");
            prevdate = intent.getStringExtra("Date");
            prevImage = intent.getStringExtra("image");
        }
        user_name_textview.setText(name + "'s location");
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //  Toast.makeText(getApplicationContext(),"onAdded",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //   CreateUser user = dataSnapshot.getValue(CreateUser.class);
                //  Toast.makeText(getApplicationContext(),dataSnapshot.getKey(),Toast.LENGTH_LONG).show();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myName = dataSnapshot.child("name").getValue(String.class);
                        myLat = dataSnapshot.child("lat").getValue(String.class);
                        myLng = dataSnapshot.child("lng").getValue(String.class);
                        myDate = dataSnapshot.child("date").getValue(String.class);
                        myImage = dataSnapshot.child("profile_image").getValue(String.class);
                        friendLatLng = new LatLng(Double.parseDouble(myLat), Double.parseDouble(myLng));

                        myOptions.position(friendLatLng);
                        myOptions.snippet("Last seen: " + myDate);
                        myOptions.title(myName);

                        if (marker == null) {
                            marker = mMap.addMarker(myOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng, 15));
                        } else {
                            marker.setPosition(friendLatLng);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng kathmandu = new LatLng(27.7172, 85.3240);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kathmandu, 12));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View row = getLayoutInflater().inflate(R.layout.custom_snippet, null);
                TextView nameTxt = row.findViewById(R.id.snippetName);
                TextView dateTxt = row.findViewById(R.id.snippetDate);
                CircleImageView imageTxt = row.findViewById(R.id.snippetImage);
                if (myName == null && myDate == null) {
                    nameTxt.setText(name);
                    dateTxt.setText(dateTxt.getText().toString() + prevdate);
                    Picasso.get().load(prevImage).placeholder(R.drawable.defaultprofile).into(imageTxt);
                } else {
                    nameTxt.setText(myName);
                    dateTxt.setText(dateTxt.getText().toString() + myDate);
                    Picasso.get().load(myImage).placeholder(R.drawable.defaultprofile).into(imageTxt);
                }
                return row;
            }
        });

        friendLatLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        MarkerOptions optionsnew = new MarkerOptions();

        optionsnew.position(friendLatLng);
        optionsnew.title(name);
        optionsnew.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //  optionsnew.snippet("Last seen:"+prevdate);
        if (marker == null) {
            marker = mMap.addMarker(optionsnew);
        } else {
            marker.setPosition(friendLatLng);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng, 15));


        //geo query
        //0.5f==500m
        /*
        GeoQuery geoQuery= geoFire.queryAtLocation(new GeoLocation(geofence_circle.latitude,geofence_circle.longitude),0.5f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                sendNotification("People Tracker",String.format("%s entered the geofence area",key));
            }

            @Override
            public void onKeyExited(String key) {
                sendNotification("People Tracker",String.format("%s is no longer in the geofence area",key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("Move", "onKeyMoved: %s moved inside the geofence area");
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d("ERROR", "onGeoQueryError: Error"+error);
            }
        });*/
    }

    /*
    private void sendNotification(String title, String content) {
        Notification.Builder builder=new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content);
        NotificationManager manager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent=new Intent(this,LiveLocationActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        Notification notification=builder.build();
        notification.flags|=Notification.FLAG_AUTO_CANCEL;
        notification.defaults |=Notification.DEFAULT_SOUND;

        manager.notify(new Random().nextInt(),notification);

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void start_geofence(View v) {
        LatLng geofence_circle=new LatLng(Double.parseDouble(String.valueOf(27.7408542)),Double.parseDouble(String.valueOf(85.3166594)));
        if(geofence_click.getText().toString()=="Start Geofence"){
            geofence_click.setText("Stop Geofence");
            //geofence circle

            mapcircle=mMap.addCircle(new CircleOptions()
                    .center(geofence_circle)
                    .radius(geofence_radius)
                    .strokeColor(Color.BLACK)
                    .fillColor(Color.TRANSPARENT)
                    .strokeWidth(5f)
            );
        }else{
            geofence_click.setText("Start Geofence");
            if(mapcircle!=null){
                mapcircle.remove();
            }
        }

    }

    public void back_image_button(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location){
    }
}
