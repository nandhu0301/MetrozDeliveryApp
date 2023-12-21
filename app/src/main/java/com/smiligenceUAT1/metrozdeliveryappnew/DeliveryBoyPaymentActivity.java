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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.SplashActivity;
import com.smiligenceUAT1.metrozdeliveryappnew.adapter.PaymentAdapter;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.OrderDetails;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import static com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant.BOOLEAN_TRUE;

public class DeliveryBoyPaymentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    View mHeaderView;
    ListView listView;
    OrderDetails orderDetails;
    ArrayList<OrderDetails> orderDetailsArrayList=new ArrayList<> (  );
    DatabaseReference orderDetailsRef;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    boolean check = true;
    boolean notify=false;
    OrderDetails billDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_delivery_boy_payment );

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );


        androidx.appcompat.widget.Toolbar toolbar1 = findViewById ( R.id.toolbar );
        toolbar1.setTitle (Constant.PAYMENT_HISTORY);
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.setDrawerListener ( toggle );
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.White ) );
        toggle.syncState ();
        NavigationView navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( DeliveryBoyPaymentActivity.this );



        DeliveryBoyProfileActivity.menuNav = navigationView.getMenu ();
        mHeaderView = navigationView.getHeaderView ( 0 );
        navigationView.setCheckedItem ( R.id.payments );

        TextView textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name123 );
        ImageView image = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader123 );



        if (DeliveryBoyProfileActivity.username != null && !"".equals ( DeliveryBoyProfileActivity.username )) {
            textViewUsername.setText ( DeliveryBoyProfileActivity.username );
        }
        if (DeliveryBoyProfileActivity.image != null && !"".equals ( DeliveryBoyProfileActivity.image )) {
            Picasso.get ().load ( String.valueOf ( Uri.parse ( DeliveryBoyProfileActivity.image ) ) ).into ( image );
        }

        listView = findViewById ( R.id.paymentsList );
        orderDetailsRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ().child ( "OrderDetails" );

        loadFunction();
        Query paymentsDetailsWithDeliveredStatusQuery=orderDetailsRef.orderByChild ( "orderStatus" ).equalTo ( "Delivered" );

        paymentsDetailsWithDeliveredStatusQuery.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount ()>0)
                {
                    for ( DataSnapshot paymentSnap:dataSnapshot.getChildren () )
                    {
                        OrderDetails orderDetails=paymentSnap.getValue (OrderDetails.class);
                        if (orderDetails.getDeliverboyId ().equals ( DeliveryBoyProfileActivity.saved_id ))
                        {
                            orderDetailsArrayList.add ( orderDetails );
                        }
                    }
                    PaymentAdapter paymentAdapter = new PaymentAdapter ( DeliveryBoyPaymentActivity.this, orderDetailsArrayList );
                    listView.setAdapter ( paymentAdapter );
                    paymentAdapter.notifyDataSetChanged ();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( DeliveryBoyPaymentActivity.this );
            bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
            Button logout = bottomSheetDialog.findViewById ( R.id.logout );
            Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );

            bottomSheetDialog.show ();
            bottomSheetDialog.setCancelable ( false );

            logout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {


                    SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                    SharedPreferences.Editor editor = sharedPreferences.edit ();
                    editor.clear ();
                    editor.commit ();

                    SharedPreferences billSharedPref = getSharedPreferences ( "BILL", MODE_PRIVATE );
                    SharedPreferences.Editor billEditor = billSharedPref.edit ();
                    billEditor.clear ();
                    billEditor.commit ();

                    Intent intent = new Intent ( getApplicationContext (), RegisterOtp.class );
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                    bottomSheetDialog.dismiss ();
                }
            } );
            stayinapp.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss ();
                }
            } );
        } else if (id == R.id.attendanceHistory) {
            Intent intent = new Intent ( getApplicationContext (), AttendanceHistory.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.payments) {
            item.setChecked ( BOOLEAN_TRUE );
            item.setCheckable ( BOOLEAN_TRUE );
            item.setEnabled ( BOOLEAN_TRUE );
        }else if (id == R.id.weeklypayments) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyWeeklySettlements.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent ( DeliveryBoyPaymentActivity.this, DeliveryBoyProfileActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }
    public void createNotification(String res,String orderid) {
        int count=0;

        if (count==0) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DeliveryBoyPaymentActivity.this, default_notification_channel_id)
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
                                                                if (!((Activity) DeliveryBoyPaymentActivity.this).isFinishing()) {
                                                                    DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                            .getReference("OrderDetails").child(billDetails.getOrderId());
                                                                    orderDetailsRef.child("notificationStatus").setValue("true");
                                                                    notify = false;
                                                                }

                                                                if (!((Activity) DeliveryBoyPaymentActivity.this).isFinishing()) {
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