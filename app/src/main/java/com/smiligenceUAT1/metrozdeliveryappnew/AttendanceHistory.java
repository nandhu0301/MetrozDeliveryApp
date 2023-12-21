package com.smiligenceUAT1.metrozdeliveryappnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.Query;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.SplashActivity;
import com.smiligenceUAT1.metrozdeliveryappnew.adapter.AttendanceAdapter;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.Check_In_Check_Out;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.OrderDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import static com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant.*;
public class AttendanceHistory  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    View mHeaderView;
    DatabaseReference databaseReference;
    boolean check = true;
    ListView deliveryboyListview;
    ArrayList<Check_In_Check_Out> check_in_check_outsArraylist = new ArrayList<Check_In_Check_Out> ();
    Check_In_Check_Out check_in_check_out;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    boolean notify=false;
    OrderDetails billDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_attendance_history );


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        androidx.appcompat.widget.Toolbar toolbar1 = findViewById ( R.id.toolbar );
        toolbar1.setTitle (ATTENDANCE_HISTORY);
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.setDrawerListener ( toggle );
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.White ) );
        toggle.syncState ();
        NavigationView navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( AttendanceHistory.this );

        DeliveryBoyProfileActivity.menuNav = navigationView.getMenu ();
        mHeaderView = navigationView.getHeaderView ( 0 );
        navigationView.setCheckedItem ( R.id.checkin_checkout_id );
        deliveryboyListview = findViewById ( R.id.deliveryboyListview );
        databaseReference = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "DeliveryBoyAttendanceTable" ).child ( DeliveryBoyProfileActivity.saved_id );

        navigationView.setCheckedItem ( R.id.attendanceHistory );

        TextView textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name123 );
        ImageView image = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader123 );


        if (DeliveryBoyProfileActivity.username != null && !"".equals ( DeliveryBoyProfileActivity.username )) {
            textViewUsername.setText ( DeliveryBoyProfileActivity.username );
        }
        if (DeliveryBoyProfileActivity.image != null && !"".equals ( DeliveryBoyProfileActivity.image )) {
            Picasso.get ().load ( String.valueOf ( Uri.parse ( DeliveryBoyProfileActivity.image ) ) ).into ( image );
        }



        loadFunction();

        databaseReference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (check = true) {
                    for ( DataSnapshot dataSnapshot1 : dataSnapshot.getChildren () ) {
                        Check_In_Check_Out check_In_Check_Out = dataSnapshot1.getValue ( Check_In_Check_Out.class );
                        check_in_check_outsArraylist.add ( check_In_Check_Out );
                    }
                    AttendanceAdapter deliveryBoyAdapter = new AttendanceAdapter ( AttendanceHistory.this, check_in_check_outsArraylist );
                    deliveryboyListview.setAdapter ( deliveryBoyAdapter );
                    deliveryBoyAdapter.notifyDataSetChanged ();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        deliveryboyListview.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, final long id) {
                check_in_check_out = check_in_check_outsArraylist.get ( position );
                Intent intent = new Intent ( AttendanceHistory.this, AttendanceHistoryDetails.class );
                intent.putExtra ( "AttendanceId", check_in_check_out.getId () );
                startActivity ( intent );
            }
        } );


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId ();
        if (id == R.id.View_Orders_id) {
            Intent intent = new Intent ( getApplicationContext (), ViewOrdersActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );

        } else if (id == R.id.checkin_checkout_id) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyAttendanceActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.View_Profile_id) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyProfileActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AttendanceHistory.this);
            bottomSheetDialog.setContentView(R.layout.logout_confirmation);
            Button logout = bottomSheetDialog.findViewById(R.id.logout);
            Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();

                    SharedPreferences billSharedPref = getSharedPreferences("BILL", MODE_PRIVATE);
                    SharedPreferences.Editor billEditor = billSharedPref.edit();
                    billEditor.clear();
                    billEditor.commit();

                    Intent intent = new Intent(AttendanceHistory.this, RegisterOtp.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    bottomSheetDialog.dismiss();
                }
            });
            stayinapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });
        } else if (id == R.id.attendanceHistory) {
            item.setChecked ( BOOLEAN_TRUE );
            item.setCheckable ( BOOLEAN_TRUE );
            item.setEnabled ( BOOLEAN_TRUE );

        }
        else if (id==R.id.payments)
        {
            Intent intent=new Intent ( AttendanceHistory.this,DeliveryBoyPaymentActivity.class );
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity ( intent );
        }else if (id == R.id.weeklypayments) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyWeeklySettlements.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(AttendanceHistory.this, DeliveryBoyProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void createNotification(String res,String orderid) {
        int count=0;

        if (count==0) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AttendanceHistory.this, default_notification_channel_id)
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
                                                                if (!((Activity) AttendanceHistory.this).isFinishing()) {
                                                                    DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                            .getReference("OrderDetails").child(billDetails.getOrderId());
                                                                    orderDetailsRef.child("notificationStatus").setValue("true");
                                                                    notify = false;

                                                                    if (!((Activity) AttendanceHistory.this).isFinishing()) {
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}