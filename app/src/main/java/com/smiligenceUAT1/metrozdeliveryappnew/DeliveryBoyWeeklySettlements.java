package com.smiligenceUAT1.metrozdeliveryappnew;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.SplashActivity;
import com.smiligenceUAT1.metrozdeliveryappnew.adapter.ReportAdapter;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.DeliveryPartnerCharge;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.ItemDetails;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.OrderDetails;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.PaymentDetails;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

import static com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant.BOOLEAN_TRUE;

public class DeliveryBoyWeeklySettlements extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    View mHeaderView;
    NavigationView navigationView;
    TextView textViewUsername;
    DatabaseReference categoryRef, itemDetailsRef;
    DatabaseReference orderdetailRef;
    ImageView imageView;
    String sellerIdIntent,storeNameIntent,storeImage;
    TextView totalSalesAmount;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetailsArrayList=new ArrayList<>();
    final ArrayList<String> list = new ArrayList<String>();
    final ArrayList<String> orderListSize = new ArrayList<String>();
    int resultTotalAmount=0;
    DatabaseReference paymentRef,deliveryDataRef,deliveryPaymentsRef;
    String startDateMon,endDateSunday;
    PaymentDetails paymentDetails=new PaymentDetails();
    ArrayList<PaymentDetails> paymentDetailsArrayList=new ArrayList<>();
    ListView list_details;
    String storeType;
    int Percentage;
    PaymentDetails sellerPaymentDetailsConstant;
    AlertDialog.Builder dialogBuilder,cardDialogBuilder;
    ImageView cancelDialog;
    ImageView receiptImage;
    AlertDialog cashDialog,cardDialog;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    boolean check = true;
    boolean notify=false;
    OrderDetails billDetails;
    DatabaseReference DeliveryBoyCharges,DeliveryBoyChargesQuery;
    int distanceDeliveryBoyToStore=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_weekly_settlements);



        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        androidx.appcompat.widget.Toolbar toolbar1 = findViewById ( R.id.toolbar );
        toolbar1.setTitle (Constant.WEEKLY_PAYMENT_SETTLEMENTS);
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.setDrawerListener ( toggle );
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.White ) );
        toggle.syncState ();
        NavigationView navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( DeliveryBoyWeeklySettlements.this );



        DeliveryBoyProfileActivity.menuNav = navigationView.getMenu ();
        mHeaderView = navigationView.getHeaderView ( 0 );
        navigationView.setCheckedItem ( R.id.weeklypayments );

        TextView textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name123 );
        ImageView image = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader123 );
        categoryRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "Category" );
        totalSalesAmount=findViewById(R.id.totalSalesamount);


        orderdetailRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "OrderDetails" );
        deliveryDataRef=FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("DeliveryBoyLoginDetails");
        deliveryPaymentsRef=FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("DeliveryBoyPayments");
        list_details=findViewById(R.id.list_details);

        DeliveryBoyCharges=FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("DeliveryBoyChargeDetails");
        DeliveryBoyChargesQuery=FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("DeliveryBoyChargeDetails").child("DeliveryBoyCharges");
        if (DeliveryBoyProfileActivity.username != null && !"".equals ( DeliveryBoyProfileActivity.username )) {
            textViewUsername.setText ( DeliveryBoyProfileActivity.username );
        }
        if (DeliveryBoyProfileActivity.image != null && !"".equals ( DeliveryBoyProfileActivity.image )) {
            Picasso.get ().load ( String.valueOf ( Uri.parse ( DeliveryBoyProfileActivity.image ) ) ).into ( image );
        }


        Calendar c = Calendar.getInstance();
        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        System.out.println();
        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        startDateMon=df.format(c.getTime());

        list.add(df.format(c.getTime()));
        for (int i = 0; i <6; i++) {
            c.add(Calendar.DATE, 1);
            list.add(df.format(c.getTime()));
        }
        endDateSunday=df.format(c.getTime());


        loadFunction();
        DeliveryBoyChargesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    DeliveryPartnerCharge otherCategoryDetails = dataSnapshot.getValue(DeliveryPartnerCharge.class);
                    if (otherCategoryDetails != null) {
                        if (!"".equals(otherCategoryDetails.getDeliveryChargeFromDeliveryBoyToStore())) {
                            distanceDeliveryBoyToStore = otherCategoryDetails.getDeliveryChargeFromDeliveryBoyToStore();
                        }
                        orderdetailRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount()>0) {

                                    orderListSize.clear();
                                    for (DataSnapshot itemSnap : dataSnapshot.getChildren()) {
                                        orderDetails = itemSnap.getValue(OrderDetails.class);

                                        if (orderDetails.getDeliverboyId() != null && !orderDetails.getDeliverboyId().equals("")) {
                                            if (orderDetails.getDeliverboyId().equals(DeliveryBoyProfileActivity.saved_id)) {
                                                for (int i = 0; i < list.size(); i++) {
                                                    if (orderDetails.getOrderStatus().equals("Delivered")) {
                                                        if (orderDetails.getFormattedDate() != null && !orderDetails.getFormattedDate().equals("")) {
                                                            if (orderDetails.getFormattedDate().equals(list.get(i))) {
                                                                int DS=orderDetails.getTotalDistanceDeliveryBoyFromCurrentLocationToStore()*distanceDeliveryBoyToStore;
                                                                resultTotalAmount = resultTotalAmount + orderDetails.getTotalFeeForDeliveryBoy()+DS + orderDetails.getTipAmount();
                                                                orderListSize.add(orderDetails.getOrderId());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }totalSalesAmount.setText("₹ " + resultTotalAmount);
                                    DatabaseReference startTimeDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                            .getReference("DeliveryBoyPayments").child(DeliveryBoyProfileActivity.saved_id).child(startDateMon);
                                    startTimeDataRef.child("startDate").setValue(startDateMon);
                                    startTimeDataRef.child("endDate").setValue(endDateSunday);
                                    startTimeDataRef.child("totalAmount").setValue(resultTotalAmount);
                                    startTimeDataRef.child("orderCount").setValue(String.valueOf(orderListSize.size()));
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        deliveryPaymentsRef.child(DeliveryBoyProfileActivity.saved_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    paymentDetailsArrayList.clear();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        PaymentDetails paymentDetails = snap.getValue(PaymentDetails.class);
                        paymentDetailsArrayList.add(paymentDetails);
                        Collections.reverse(paymentDetailsArrayList);
                    }
                    ReportAdapter paymentAdapter = new ReportAdapter(DeliveryBoyWeeklySettlements.this, paymentDetailsArrayList);
                    list_details.setAdapter(paymentAdapter);
                    paymentAdapter.notifyDataSetChanged();
                    if (paymentAdapter != null) {
                        int totalHeight = 0;

                        for (int i = 0; i < paymentAdapter.getCount(); i++) {
                            View listItem = paymentAdapter.getView(i, null, list_details);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }

                        ViewGroup.LayoutParams params = list_details.getLayoutParams();
                        params.height = totalHeight + (list_details.getDividerHeight() * (paymentAdapter.getCount() - 1));
                        list_details.setLayoutParams(params);
                        list_details.requestLayout();
                        list_details.setAdapter(paymentAdapter);
                        paymentAdapter.notifyDataSetChanged();


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        list_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sellerPaymentDetailsConstant = paymentDetailsArrayList.get(i);


                if (sellerPaymentDetailsConstant.getPaymentStatus()!=null) {
                    if (sellerPaymentDetailsConstant.getPaymentStatus().equals("Settled")) {

                        dialogBuilder = new AlertDialog.Builder(DeliveryBoyWeeklySettlements.this);
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.bill_details, null);
                        dialogBuilder.setView(dialogView);
                        cancelDialog = dialogView.findViewById(R.id.Cancel);
                        receiptImage = dialogView.findViewById(R.id.receiptImageview);


                        if (sellerPaymentDetailsConstant.getReceiptURL()!=null && !sellerPaymentDetailsConstant.getReceiptURL().equals("")) {
                            String drivingLicenseProofUri = String.valueOf(Uri.parse(sellerPaymentDetailsConstant.getReceiptURL()));
                            Picasso.get().load(drivingLicenseProofUri).into(receiptImage);
                            cashDialog = dialogBuilder.create();
                            cashDialog.show();
                        }



                        cancelDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cashDialog.dismiss();
                            }
                        });

                    }
                }
            }
        });

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
        } else if (id == R.id.weeklypayments) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyWeeklySettlements.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( DeliveryBoyWeeklySettlements.this );
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
        }else if (id == R.id.weeklypayments) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyWeeklySettlements.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }else if (id == R.id.payments) {
            item.setChecked ( BOOLEAN_TRUE );
            item.setCheckable ( BOOLEAN_TRUE );
            item.setEnabled ( BOOLEAN_TRUE );
            Intent intent = new Intent(getApplicationContext(), DeliveryBoyPaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return false;
    }
    public void createNotification(String res,String orderid) {
        int count=0;

        if (count==0) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DeliveryBoyWeeklySettlements.this, default_notification_channel_id)
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
                                                                if (!((Activity) DeliveryBoyWeeklySettlements.this).isFinishing()) {
                                                                    DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                            .getReference("OrderDetails").child(billDetails.getOrderId());
                                                                    orderDetailsRef.child("notificationStatus").setValue("true");
                                                                    notify = false;
                                                                }

                                                                if (!((Activity) DeliveryBoyWeeklySettlements.this).isFinishing()) {
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