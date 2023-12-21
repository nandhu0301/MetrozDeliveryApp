package com.smiligenceUAT1.metrozdeliveryappnew;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

public class MyBackgroundService extends Service
{

    private static final String CHANNEL_ID="my_channel";
    private final IBinder mBinder=new LocalBinder();
    public static final long UPDATE_INTERVAL_IN_MIL=10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MUL=UPDATE_INTERVAL_IN_MIL/2;
    private static final int NOTI_ID=1223;
    private boolean mChangingConfiguration=false;
    private NotificationManager mnotificationManager;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler mserviceHandler;
    private Location mLocation;
    private static final String EXTRA_STARTED_FROM_NOTIIFCATION="com.smiligenceUAT1.metrozdeliveryappnew"+".started_from_notification";
    private Context mcontext= MyBackgroundService.this;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    public MyBackgroundService()
    {

    }

    @SuppressLint("ServiceCast")
    @Override
    public void onCreate() {
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationCallback=new LocationCallback(){
           @Override
           public void onLocationResult(LocationResult locationResult) {
               super.onLocationResult(locationResult);
               onNewLocation(locationResult.getLastLocation());
           }
       };
       createLocationRequest();
       getLastLocation();
        HandlerThread handlerThread=new HandlerThread("EDMTDev");
        handlerThread.start();
        mserviceHandler=new Handler(handlerThread.getLooper());
        mnotificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            NotificationChannel mChannel=new NotificationChannel(CHANNEL_ID,getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mnotificationManager.createNotificationChannel(mChannel);

        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean startedFromNotification=intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIIFCATION,false);
        if (startedFromNotification)
        {
            removeLocationUpdates();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration=true;
    }

    public void removeLocationUpdates() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            CommonFiles.setRequestingLocationUpdates(this,false);
            stopSelf();
        }catch (SecurityException s)
        {
            CommonFiles.setRequestingLocationUpdates(this,true);
        }
    }

    private void getLastLocation() {
        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() !=null)
                    {
                        mLocation=task.getResult();

                    }
                    else
                    {

                    }
                }
            });
        }catch (SecurityException secex)
        {

        }
    }

    private void createLocationRequest() {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MIL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MUL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void onNewLocation(Location lastLocation)
    {
        mLocation=lastLocation;
        EventBus.getDefault().postSticky(new SendLocationToActivity(mLocation));
        if (serviceIsRunningInForeground(this))
        {
            mnotificationManager.notify(NOTI_ID,getNotification());
        }
    }


    private Notification getNotification() {


        Intent intent=new Intent(this, MyBackgroundService.class);
        String text= CommonFiles.getLocationText(mLocation);
        double mlat= CommonFiles.latitudeStringFromLocation(mLocation);
        double mlong= CommonFiles.longtitudeStringFromLocation(mLocation);

            final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("DeliveryBoyLoginDetails").child(DeliveryBoyProfileActivity.saved_id);
            deliveryBoyDetailsDataRef.child("deliveryBoyLatitude").setValue(mlat);
            deliveryBoyDetailsDataRef.child("deliveryBoyLongtitude").setValue(mlong);

        intent.putExtra(EXTRA_STARTED_FROM_NOTIIFCATION,true);
        PendingIntent servicePendingIntent= PendingIntent.getService(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent activityPendingIntent= PendingIntent.getActivity(this,0,new Intent(this, MainActivity.class),0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this).addAction(R.drawable.ic_cancelicon,"Launch",activityPendingIntent)
                .addAction(R.drawable.ic_cancelicon,"Remove",servicePendingIntent).setContentText("Current LatLong"+text)
                .setContentTitle(CommonFiles.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_baseline_launch_24)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            builder.setChannelId(CHANNEL_ID);

        }

        return builder.build();

    }

    private boolean serviceIsRunningInForeground(Context context)
    {
        ActivityManager manager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE))
            if (getClass().getName().equals(service.service.getClassName()))
            {
                if (service.foreground)
                    return true;
            }
        return false;
    }

    public void requestLocationUpdates() {
        CommonFiles.setRequestingLocationUpdates(this,true);
        startService(new Intent(getApplicationContext(), MyBackgroundService.class));
        try
        {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

        }catch (SecurityException s)
        {

        }
    }

    public class LocalBinder extends Binder
    {
        MyBackgroundService getservice()
        {
            return MyBackgroundService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration=false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration=false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
         if (!mChangingConfiguration && CommonFiles.requestingLocationUpdated(this))
         {
             startForeground(NOTI_ID,getNotification());
         }
        return true;
    }

    @Override
    public void onDestroy() {
        mserviceHandler.removeCallbacks(null);
        super.onDestroy();
    }
}
