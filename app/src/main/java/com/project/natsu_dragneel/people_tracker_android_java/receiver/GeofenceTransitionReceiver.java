package com.project.natsu_dragneel.people_tracker_android_java.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.project.natsu_dragneel.people_tracker_android_java.R;
import com.project.natsu_dragneel.people_tracker_android_java.activities.maps_activities.LiveLocationActivity;

public class GeofenceTransitionReceiver extends WakefulBroadcastReceiver {

    public static final String TAG=GeofenceTransitionReceiver.class.getSimpleName();

    private Context context;

    public GeofenceTransitionReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: (context, intent)");
        this.context=context;
        GeofencingEvent event=GeofencingEvent.fromIntent(intent);
        if(event!=null){
            if(event.hasError()){
                onError(event.getErrorCode());
            }
            else{
                int transition=event.getGeofenceTransition();
                if(transition== Geofence.GEOFENCE_TRANSITION_ENTER||transition==Geofence.GEOFENCE_TRANSITION_DWELL|| transition==Geofence.GEOFENCE_TRANSITION_DWELL){
                    String[] geofenceIds=new String[event.getTriggeringGeofences().size()];
                    for(int index=0;index<event.getTriggeringGeofences().size();index++){
                        geofenceIds[index]=event.getTriggeringGeofences().get(index).getRequestId();
                    }
                    if(transition==Geofence.GEOFENCE_TRANSITION_DWELL||transition==Geofence.GEOFENCE_TRANSITION_ENTER){
                        onEnteredGeofences(geofenceIds);
                    }
                    else{
                        onExitedGeofences(geofenceIds);
                    }
                }
            }
        }
    }

    protected void onError(int errorCode){
        Toast.makeText(context, String.format("onError:(%1$d)", errorCode), Toast.LENGTH_SHORT).show();
        Log.d(TAG, String.format("onError: (%1$d)", errorCode));
    }

    protected void onEnteredGeofences(String[] geofenceIds){
        for(String fenceId:geofenceIds){
            Toast.makeText(context, String.format("Entered the geofence: %1$s",fenceId), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onEnteredGeofences: Entered");
            createNotification(fenceId,"Entered");
        }
    }

    protected void onExitedGeofences(String[] geofenceIds){
        for (String fenceId:geofenceIds){
            Toast.makeText(context, String.format("Exited the geofence: %1$s", fenceId), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onExitedGeofences: Exited");
            createNotification(fenceId,"Exited");
        }
    }

    //notification
    private void createNotification(String fenceId,String fenceState){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
        builder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL);
        builder
                .setContentText(fenceId)
                .setContentTitle(String.format("Fence:%1$s",fenceState))
                .setSmallIcon(R.drawable.icon_geofence)
                .setColor(Color.argb(0x55,0x00,0x00,0xff))
                .setTicker(String.format("%1$s Fence: %2$s",fenceState,fenceId));

        Intent notificationIntent=new Intent(context, LiveLocationActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
        notificationIntent.setAction(Intent.ACTION_DEFAULT);

        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,notificationIntent,0);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(R.id.notification,builder.build());
    }
}
