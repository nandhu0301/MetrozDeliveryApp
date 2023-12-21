package com.smiligenceUAT1.metrozdeliveryappnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smiligenceUAT1.metrozdeliveryappnew.Common.SplashActivity;
import com.smiligenceUAT1.metrozdeliveryappnew.adapter.DeliveryBoyAdapter;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.OrderDetails;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.UserDetails;
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
import java.util.Collections;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant.*;

public class ViewOrdersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference orderDetailsRef, userDetailsdataRef;

    ArrayList<OrderDetails> deliveryBoyArrayList = new ArrayList<> ();
    ListView listView;
    OrderDetails billDetails;
    public TextView textViewUsername;
    public ImageView imageView;
    NavigationView navigationView;

    View mHeaderView;
    public String fieldOfficerName, roleName, imageStr;
    AlertDialog dialog;
    boolean isCheck=true;
    SweetAlertDialog errorDialog;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    boolean check = true;
    boolean notify=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_view_orders );

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        orderDetailsRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ().child ( "OrderDetails" );
        userDetailsdataRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "DeliveryBoyLoginDetails" );
        listView = findViewById ( R.id.deliveryboyListview );

        androidx.appcompat.widget.Toolbar toolbar1 = findViewById ( R.id.toolbar );
        toolbar1.setTitle (MY_ASSIGNED_ORDERS);
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.setDrawerListener ( toggle );
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.White ) );
        toggle.syncState ();
        navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( ViewOrdersActivity.this );
        DeliveryBoyProfileActivity.menuNav = navigationView.getMenu ();
        mHeaderView = navigationView.getHeaderView ( 0 );
        navigationView.setCheckedItem ( R.id.View_Orders_id );


        textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name123 );
        imageView = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader123 );

        loadFunction();
        final Query roleNameQuery = userDetailsdataRef.orderByChild ( "mobileNumber" ).equalTo ( DeliveryBoyProfileActivity.saved_customerPhonenumber );


        roleNameQuery.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryBoyArrayList.clear();

                for ( DataSnapshot snap : dataSnapshot.getChildren () ) {
                    UserDetails loginUserDetailslist = snap.getValue ( UserDetails.class );
                    fieldOfficerName = loginUserDetailslist.getFirstName ();
                    textViewUsername.setText ( fieldOfficerName );
                    roleName = "Delivery Boy";
                    imageStr = loginUserDetailslist.getDeliveryBoyProfile ();
                    if (imageStr != null) {
                        String profileImageUri = String.valueOf ( Uri.parse ( imageStr ) );
                        Picasso.get ().load ( profileImageUri ).into ( imageView );
                    } else {

                    }
                }
                orderDetailsRef.addListenerForSingleValueEvent ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Query fetchDetailsQuery = orderDetailsRef.
                                orderByChild ( ASSIGNED_TO_STATUS ).equalTo ( fieldOfficerName );
                        fetchDetailsQuery.addValueEventListener ( new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                deliveryBoyArrayList.clear();
                                if (check = true) {
                                    for ( DataSnapshot dataSnapshot1 : dataSnapshot.getChildren () ) {
                                        OrderDetails billDetails = dataSnapshot1.getValue ( OrderDetails.class );
                                        deliveryBoyArrayList.add ( billDetails );
                                    }
                                    Collections.reverse ( deliveryBoyArrayList );
                                    DeliveryBoyAdapter deliveryBoyAdapter = new DeliveryBoyAdapter ( ViewOrdersActivity.this, deliveryBoyArrayList );
                                    listView.setAdapter ( deliveryBoyAdapter );
                                    deliveryBoyAdapter.notifyDataSetChanged ();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        listView.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                billDetails = deliveryBoyArrayList.get(position);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewOrdersActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    final View alertdialogView = inflater.inflate(R.layout.view_orders_layout, null);
                    alertDialogBuilder.setView(alertdialogView);
                    EditText orderId = alertdialogView.findViewById(R.id.orderid);
                    EditText customerName = alertdialogView.findViewById(R.id.customerName);
                    EditText storeAddress = alertdialogView.findViewById(R.id.storeaddress);
                    EditText deliverAddress = alertdialogView.findViewById(R.id.deliveryAddress);
                    EditText distance = alertdialogView.findViewById(R.id.totalDistance);
                    EditText deliveryFee = alertdialogView.findViewById(R.id.totalDeliveryFee);
                    RelativeLayout invisiblelayout = alertdialogView.findViewById(R.id.invisiblelayout);

                    final EditText otp = alertdialogView.findViewById(R.id.otpeditText);
                    Button ok = alertdialogView.findViewById(R.id.okview);
                    Button cancel = alertdialogView.findViewById(R.id.cancelview);




                    if (billDetails.getOrderStatus().equals("Delivery is on the way")) {
                        invisiblelayout.setVisibility(View.VISIBLE);
                        ok.setVisibility(View.VISIBLE);
                    } else {
                        invisiblelayout.setVisibility(View.INVISIBLE);
                        ok.setVisibility(View.INVISIBLE);
                    }
                    orderId.setText(String.valueOf(billDetails.getOrderId()));
                    customerName.setText(billDetails.getCustomerName());
                    storeAddress.setText(billDetails.getStoreAddress());
                    deliverAddress.setText(billDetails.getShippingaddress());
                    distance.setText(billDetails.getTotalDistanceTraveled() + " Km");
                    deliveryFee.setText("₹ " + billDetails.getTotalFeeForDeliveryBoy());
                    final String deliverCode = billDetails.getDeliverOtp();


                    dialog = alertDialogBuilder.create();
                    dialog.show();
                    dialog.setCancelable(BOOLEAN_FALSE);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (otp.getText().toString().equals("")) {
                                otp.setError("Required");
                            } else if (!deliverCode.equals(otp.getText().toString())) {
                                errorDialog = new SweetAlertDialog( ViewOrdersActivity.this, SweetAlertDialog.ERROR_TYPE );
                                errorDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                                errorDialog.setCancelable ( false );
                                errorDialog
                                        .setContentText ( "Invalid PIN" ).setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener () {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        errorDialog.dismiss ();
                                    }
                                } ).show ();
                            } else if (deliverCode.equals(otp.getText().toString())) {
                                Intent intent = getIntent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                DatabaseReference ref = orderDetailsRef.child(String.valueOf(billDetails.getOrderId()));
                                ref.child("orderStatus").setValue("Delivered");
                                dialog.dismiss();
                            }
                        }
                    });
            }
        } );
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId ();
        if (id == R.id.View_Orders_id) {
            item.setChecked ( BOOLEAN_TRUE );
            item.setCheckable ( BOOLEAN_TRUE );
            item.setEnabled ( BOOLEAN_TRUE );

        } else if (id == R.id.checkin_checkout_id) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyAttendanceActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( ViewOrdersActivity.this );
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
                    Intent intent = new Intent ( ViewOrdersActivity.this, RegisterOtp.class );
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

        } else if (id == R.id.View_Profile_id) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyProfileActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.attendanceHistory) {
            Intent intent = new Intent ( getApplicationContext (), AttendanceHistory.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.payments) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyPaymentActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
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
    public void onBackPressed()
    {
        Intent intent = new Intent ( ViewOrdersActivity.this, DeliveryBoyProfileActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }
    public void createNotification(String res,String orderid) {
        int count=0;

        if (count==0) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ViewOrdersActivity.this, default_notification_channel_id)
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
                                                                if (!((Activity) ViewOrdersActivity.this).isFinishing()) {
                                                                    DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                            .getReference("OrderDetails").child(billDetails.getOrderId());
                                                                    orderDetailsRef.child("notificationStatus").setValue("true");
                                                                    notify = false;
                                                                }

                                                                if (!((Activity) ViewOrdersActivity.this).isFinishing()) {
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