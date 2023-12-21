package com.smiligenceUAT1.metrozdeliveryappnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.CommonMethods;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.SplashActivity;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.Check_In_Check_Out;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.OrderDetails;

import java.util.Random;

import static com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant.*;

public class AttendanceHistoryDetails extends AppCompatActivity {
    TextView checkInDateTextView, checkInTimeTextvView, checkInDayTextView;
    TextView checkOutDateTextView, checkOutTimeTextvView, checkOutDayTextView;

    TextView breakFromWorkTxt, totalCheckedInTime, actualWorkingHours;
    TextView breakOneTxt, breakTwoTxt, lunchTxt, permissionTxt;

    DatabaseReference attendanceDetailsRef;
    ImageView back;
    TextView attendanceHistoryDate;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    boolean check = true;
    boolean notify=false;
    OrderDetails billDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_attendance_history_details );

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        back = findViewById ( R.id.back );
        attendanceHistoryDate = findViewById ( R.id.attendanceHistoryDate );


        checkInDateTextView = findViewById ( R.id.checkinDateHistory );
        checkInTimeTextvView = findViewById ( R.id.checkInTimehistory );
        checkInDayTextView = findViewById ( R.id.checkinDayhistory );

        checkOutDateTextView = findViewById ( R.id.checkOutDateHistory );
        checkOutTimeTextvView = findViewById ( R.id.checkOutTimehistory );
        checkOutDayTextView = findViewById ( R.id.checkOutDayhistory );

        breakFromWorkTxt = findViewById ( R.id.breakFromWorkhistory );
        totalCheckedInTime = findViewById ( R.id.totalCheckInhourshistory );
        actualWorkingHours = findViewById ( R.id.totalWorkingHourshistory );

        breakOneTxt = findViewById ( R.id.teaBreakOnehistory );
        breakTwoTxt = findViewById ( R.id.teaBreakTwohistory );
        lunchTxt = findViewById ( R.id.lunchhistory );
        permissionTxt = findViewById ( R.id.Permissionhistory );
        attendanceDetailsRef = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id );



        final String getId = getIntent ().getStringExtra ( "AttendanceId" );

        back.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent ( AttendanceHistoryDetails.this, AttendanceHistory.class );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
            }
        } );

        loadFunction();
        final Query detailsQuery = attendanceDetailsRef.orderByChild ( "id" ).equalTo ( getId );
        detailsQuery.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () > 0) {
                    for ( DataSnapshot detailsDataSnap : dataSnapshot.getChildren () ) {
                        Check_In_Check_Out check_in_check_out = detailsDataSnap.getValue ( Check_In_Check_Out.class );
                        attendanceHistoryDate.setText ( "     Attendance(" + check_in_check_out.getCreationDate () + ")" );
                        checkInDateTextView.setText ( check_in_check_out.getCheckInDate () );
                        checkInTimeTextvView.setText ( check_in_check_out.getCheckInTime () );
                        checkInDayTextView.setText ( check_in_check_out.getCheckInDay () );
                        checkOutDateTextView.setText ( check_in_check_out.getCheckOutDate () );
                        checkOutTimeTextvView.setText ( check_in_check_out.getCheckOutTime () );
                        checkOutDayTextView.setText ( check_in_check_out.getCheckOutDay () );
                        breakFromWorkTxt.setText ( check_in_check_out.getTotalBreakTime () );
                        totalCheckedInTime.setText ( check_in_check_out.getTotalCheckInCheckOutTime () );
                        actualWorkingHours.setText ( check_in_check_out.getActutalWorkingTime () );
                        breakOneTxt.setText ( check_in_check_out.getTotalTeaBreakMorning () );
                        breakTwoTxt.setText ( check_in_check_out.getTotalTeaBreakEvening () );
                        lunchTxt.setText ( check_in_check_out.getTotalLunch_Dinner_Break () );
                        permissionTxt.setText ( check_in_check_out.getOtherPermissionBreak () );
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );




    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(AttendanceHistoryDetails.this, DeliveryBoyProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void createNotification(String res,String orderid) {
        int count=0;

        if (count==0) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AttendanceHistoryDetails.this, default_notification_channel_id)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("New Delivery Order")
                    .setContentText(res)
                    .setAutoCancel(true);

            Intent secondActivityIntent = new Intent(this, SplashActivity.class);
            PendingIntent secondActivityPendingIntent = PendingIntent.getActivity(this, 0, secondActivityIntent, PendingIntent.FLAG_ONE_SHOT);
            mBuilder.addAction(R.mipmap.ic_launcher, "View", secondActivityPendingIntent);


            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                        notificationChannel.enableLights(true);
                        notificationChannel.setLightColor(Color.RED);
                        notificationChannel.enableVibration(true);
                        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        try {
                            Uri path = Uri.parse("android.resource://com.smiligenceUAT1.metrozdeliveryappnew/" + R.raw.old_telephone_tone);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), path);
                            r.play();
                        } catch (Exception e) {
                        }
                        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                        assert mNotificationManager != null;
                        mNotificationManager.createNotificationChannel(notificationChannel);

                    }
                    assert mNotificationManager != null;


                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    Random random = new Random();
                    int m = random.nextInt(9999 - 1000) + 1000;
                    notificationManager.notify(Integer.parseInt(orderid), notification);
                }
            },3000);
            count = count + 1;
        }
    }
    public void loadFunction()
    {
        final Query deliveryBoyNotificationQuery = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("OrderDetails").orderByChild("deliverboyId").equalTo(DeliveryBoyProfileActivity.saved_id);
        deliveryBoyNotificationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notify=true;
                if (check)
                {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    billDetails = dataSnapshot1.getValue(OrderDetails.class);
                                    if (!billDetails.getAssignedTo().equals("")) {
                                        if (billDetails.getOrderStatus().equals("Order Placed") && billDetails.getNotificationStatus().equals("false") || billDetails.getOrderStatus().equals("Ready For PickUp") && billDetails.getNotificationStatus().equals("false")) {
                                            if (!billDetails.getOrderStatus().equals("Ready For PickUp")) {
                                                if (!billDetails.getOrderStatus().equals("Delivery is on the way")) {
                                                    if (!billDetails.getOrderStatus().equals("Delivered")) {
                                                        if ("false".equals(billDetails.getNotificationStatus())) {
                                                            if (notify) {
                                                                if (!((Activity) AttendanceHistoryDetails.this).isFinishing()) {
                                                                    DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                            .getReference("OrderDetails").child(billDetails.getOrderId());
                                                                    orderDetailsRef.child("notificationStatus").setValue("true");
                                                                    notify = false;
                                                                }

                                                                if (!((Activity) AttendanceHistoryDetails.this).isFinishing()) {
                                                                    createNotification("Order #" + billDetails.getOrderId() + " Assigned for delivery ", billDetails.getOrderId());
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}