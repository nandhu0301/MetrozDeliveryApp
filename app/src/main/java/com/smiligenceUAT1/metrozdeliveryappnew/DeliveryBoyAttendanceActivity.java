package com.smiligenceUAT1.metrozdeliveryappnew;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.CommonMethods;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.DateUtils;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.SplashActivity;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.Check_In_Check_Out;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.OrderDetails;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.smiligenceUAT1.metrozdeliveryappnew.Common.Constant.*;


public class DeliveryBoyAttendanceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener
{

    Switch checkInBox, checkOutBox;
    TextView checkInDateTextView, checkInTimeTextvView, checkInDayTextView;
    TextView checkOutDateTextView, checkOutTimeTextvView, checkOutDayTextView;
    String DbDate, DbTime, DbDay;
    Spinner permissionSpinner;
    private boolean running;
    Switch startInterval, endInterval;
    SimpleDateFormat format;
    DatabaseReference deliveryBoyAttendanceDataRef;
    Check_In_Check_Out check_in_check_out = new Check_In_Check_Out ();
    long deliveryBoyAttendanceMaxId = 0;
    long diff1, diff2, diff3, diff4, diff5;
    int dayFirstBreak, hourFirstBreak, minFirstBreak;
    int daySecondBreak, hourSecondBreak, minSecondBreak;
    int dayLunchBreak, hourLunchBreak, minLunchBreak;
    int dayPermissionBreak, hourPermissionBreak, minPermissionBreak;
    int dayCheckInOut, hourCheckInOut, minCheckInOut;
    TextView start_Interval_Time, end_Interval_Time;
    TextView breakFromWorkTxt, totalCheckedInTime, actualWorkingHours;
    TextView breakOneTxt, breakTwoTxt, lunchTxt, permissionTxt;
    View mHeaderView;
    AlertDialog dialog;


    boolean checkInStatusStr;
    String checkInTimeStr, checkOutTimeStr;
    String eveningTeaBreakStart, eveningTeaBreakStop;
    String lunchBreakStart, lunchBreakStop;
    String permissionStart, permissionEnd;
    String teaBreakMorningStr, teaBreakEveningStr, lunch_Dinner_Str, otherPermissionStr;
    String teaBreakMorningStartTime, teaBreakEveningStartTime, lunch_Dinner_StartTime, otherPermissionStartTime;
    String teaBreakMorningEndTime, teaBreakEveningEndTime, lunch_Dinner_EndTime, otherPermissionEndTime;


    ArrayList<String> arrayList = new ArrayList<> ();
    SimpleDateFormat simpleDateFormat;
    private long pauseOffset;
    Thread thread;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    boolean check = true;
    boolean notify=false;
    OrderDetails billDetails;

    Button requestLocation,removeUpdated;
    MyBackgroundService myBackgroundService=null;
    boolean mBound=false;
    private final ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBackgroundService.LocalBinder binder=(MyBackgroundService.LocalBinder)iBinder;
            myBackgroundService=binder.getservice();
            mBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myBackgroundService=null;
            mBound=false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);
        MyBackgroundService myBackgroundService=null;
        boolean mBound=false;


    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound=false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_delivery_boys_attendance );


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        breakOneTxt = findViewById ( R.id.teaBreakOne );
        breakTwoTxt = findViewById ( R.id.teaBreakTwo );
        lunchTxt = findViewById ( R.id.lunch );
        permissionTxt = findViewById ( R.id.Permission );
        Dexter.withActivity(this).withPermissions(Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        requestLocation=findViewById(R.id.updateLocation);
                        removeUpdated=findViewById(R.id.declineLocation);

                        requestLocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(getApplicationContext(),DeliveryBoyAttendanceActivity.class);
                                startActivity(intent);
                                myBackgroundService.requestLocationUpdates();
                            }
                        });
                        removeUpdated.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(getApplicationContext(),DeliveryBoyAttendanceActivity.class);
                                startActivity(intent);
                                myBackgroundService.removeLocationUpdates();

                            }
                        });
                        setButtonState(CommonFiles.requestingLocationUpdated(DeliveryBoyAttendanceActivity.this));
                        bindService(new Intent(DeliveryBoyAttendanceActivity.this,MyBackgroundService.class),
                                mServiceConnection,
                                Context.BIND_AUTO_CREATE);
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();
        thread = new Thread () {

            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted ()) {
                        Thread.sleep ( 1000 );
                        runOnUiThread ( new Runnable () {
                            @Override
                            public void run() {
                                DbDay = LocalDate.now ().getDayOfWeek ().name ();
                                DbDate = DateUtils.fetchCurrentDate ();


                                Calendar cal = Calendar.getInstance ();
                                Date currentLocalTime = cal.getTime ();
                                DateFormat date = new SimpleDateFormat ( "hh:mm a" );
                                DbTime = date.format ( currentLocalTime );




                            }
                        } );
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start ();

        androidx.appcompat.widget.Toolbar toolbar1 = findViewById ( R.id.toolbar );
        toolbar1.setTitle (Daily_CheckIn_CheckOut);
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.setDrawerListener ( toggle );
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.White ) );
        toggle.syncState ();
        NavigationView navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( DeliveryBoyAttendanceActivity.this );


        DeliveryBoyProfileActivity.menuNav = navigationView.getMenu ();
        mHeaderView = navigationView.getHeaderView ( 0 );
        navigationView.setCheckedItem ( R.id.checkin_checkout_id );

        TextView textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name123 );
        ImageView image = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader123 );

        if (DeliveryBoyProfileActivity.username != null && !"".equals ( DeliveryBoyProfileActivity.username ))
        {
            textViewUsername.setText ( DeliveryBoyProfileActivity.username );
        }
        if (DeliveryBoyProfileActivity.image != null && !"".equals ( DeliveryBoyProfileActivity.image )) {
            Picasso.get ().load ( String.valueOf ( Uri.parse ( DeliveryBoyProfileActivity.image ) ) ).into ( image );
        }




        deliveryBoyAttendanceDataRef = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE );
        loadFunction();
        loadFunction1();

        checkInDateTextView = findViewById ( R.id.checkinDate );
        checkInTimeTextvView = findViewById ( R.id.checkInTime );
        checkInDayTextView = findViewById ( R.id.checkinDay );

        checkOutDateTextView = findViewById ( R.id.checkOutDate );
        checkOutTimeTextvView = findViewById ( R.id.checkOutTime );
        checkOutDayTextView = findViewById ( R.id.checkOutDay );

        breakFromWorkTxt = findViewById ( R.id.breakFromWork );
        totalCheckedInTime = findViewById ( R.id.totalCheckInhours );
        actualWorkingHours = findViewById ( R.id.totalWorkingHours );

        checkInBox = findViewById ( R.id.checkInSwitch );
        checkOutBox = findViewById ( R.id.checkOutSwitch );
        permissionSpinner = findViewById ( R.id.permissionSpinner );


        startInterval = findViewById ( R.id.intervalStartSwitch );
        endInterval = findViewById ( R.id.intervalStopSwitch );
        start_Interval_Time = findViewById ( R.id.IntervalStartTime );
        end_Interval_Time = findViewById ( R.id.IntervalEndTime );


        final ArrayAdapter<String> permissionAdapter = new ArrayAdapter<String>
                ( this, R.layout.spinner_type, PERMISSION_LIST );
        permissionSpinner.setAdapter ( permissionAdapter );
        permissionAdapter.notifyDataSetChanged ();
        permissionSpinner.setEnabled ( false );

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern ( "MMMM-dd-YYYY" );
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern ( "hh:mm a" );
        simpleDateFormat = new SimpleDateFormat ( "hh:mm a" );



        loadFunction ();




        permissionSpinner.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerClickResult = (String) parent.getItemAtPosition ( position );
                if (spinnerClickResult.equals ( "Select One" )) {
                    intervalFunctionAnother ();

                }
                if (spinnerClickResult.equals ( TEA_BREAK_1 )) {
                    if (spinnerClickResult.equals ( teaBreakMorningStr ) && !"".equals ( teaBreakMorningStartTime ) && "".equals ( teaBreakMorningEndTime )) {


                        startInterval.setEnabled ( BOOLEAN_FALSE );
                        endInterval.setEnabled ( BOOLEAN_TRUE );
                        checkOutBox.setEnabled ( BOOLEAN_FALSE );
                        permissionSpinner.setEnabled ( BOOLEAN_FALSE );
                        breakFunction ( teaBreakMorningStartTime, teaBreakMorningEndTime );
                        endInterval.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                intervalFunctionAnother ();
                                end_Interval_Time.setText ( DbTime );
                                final DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                                ref.child ( "intervalTeaBreakMorningTimeEnd" ).setValue ( DbTime );



                                Intent intent = getIntent ();
                                startActivity ( intent );


                            }
                        } );
                    } else if (spinnerClickResult.equals ( teaBreakMorningStr ) && !"".equals ( teaBreakMorningStartTime ) && !"".equals ( teaBreakMorningEndTime )) {

                        breakFunction ( teaBreakMorningStartTime, teaBreakMorningEndTime );
                        intervalFunctionAnother ();
                        Toast.makeText ( DeliveryBoyAttendanceActivity.this, "Permission already taken", Toast.LENGTH_SHORT ).show ();
                        start_Interval_Time.setText ( teaBreakMorningStartTime );
                        end_Interval_Time.setText ( teaBreakMorningEndTime );


                    } else if (spinnerClickResult.equals ( TEA_BREAK_1 ) && "".equals ( teaBreakMorningStartTime ) && "".equals ( teaBreakMorningEndTime )) {
                        endInterval.setEnabled ( BOOLEAN_FALSE );
                        startInterval.setEnabled ( BOOLEAN_TRUE );
                        breakFunction ( "", "" );

                        startInterval.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                permissionSpinner.setEnabled ( BOOLEAN_FALSE );
                                endInterval.setEnabled ( BOOLEAN_TRUE );
                                startInterval.setEnabled ( BOOLEAN_FALSE );
                                checkOutBox.setEnabled ( BOOLEAN_FALSE );
                                start_Interval_Time.setText ( DbTime );
                                final DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                                ref.child ( "intervalTeaBreakMorningTimeStart" ).setValue ( DbTime );
                                ref.child ( "intervalTypeTeaBreakMorning" ).setValue ( TEA_BREAK_1 );
                                Intent intent = getIntent ();
                                startActivity ( intent );
                            }
                        } );
                    }
                } else if (spinnerClickResult.equals ( TEA_BREAK_2 )) {

                    if (spinnerClickResult.equals ( teaBreakEveningStr ) && !"".equals ( teaBreakEveningStartTime ) && "".equals ( teaBreakEveningEndTime )) {

                        startInterval.setEnabled ( BOOLEAN_FALSE );
                        endInterval.setEnabled ( BOOLEAN_TRUE );
                        checkOutBox.setEnabled ( BOOLEAN_FALSE );
                        permissionSpinner.setEnabled ( BOOLEAN_FALSE );
                        start_Interval_Time.setText ( String.valueOf ( teaBreakEveningStartTime ) );
                        breakFunction ( teaBreakEveningStartTime, teaBreakEveningEndTime );
                        endInterval.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                intervalFunctionAnother ();
                                end_Interval_Time.setText ( DbTime );
                                final DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                                ref.child ( "intervalTeaBreakEveningTimeEnd" ).setValue ( DbTime );

                                Intent intent = getIntent ();
                                startActivity ( intent );

                            }
                        } );
                    } else if (spinnerClickResult.equals ( teaBreakEveningStr ) && !"".equals ( teaBreakEveningStartTime ) && !"".equals ( teaBreakEveningEndTime )) {
                        intervalFunctionAnother ();
                        breakFunction ( teaBreakEveningStartTime, teaBreakEveningEndTime );
                        start_Interval_Time.setText ( teaBreakEveningStartTime );
                        end_Interval_Time.setText ( teaBreakEveningEndTime );
                        Toast.makeText ( DeliveryBoyAttendanceActivity.this, "Permission already taken", Toast.LENGTH_SHORT ).show ();

                    } else if (spinnerClickResult.equals ( TEA_BREAK_2 ) && "".equals ( teaBreakEveningStartTime ) && "".equals ( teaBreakEveningEndTime )) {
                        endInterval.setEnabled ( BOOLEAN_FALSE );
                        breakFunction ( "", "" );
                        startInterval.setEnabled ( BOOLEAN_TRUE );

                        startInterval.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                endInterval.setEnabled ( BOOLEAN_TRUE );
                                permissionSpinner.setEnabled ( BOOLEAN_FALSE );
                                startInterval.setEnabled ( BOOLEAN_FALSE );
                                checkOutBox.setEnabled ( BOOLEAN_FALSE );
                                start_Interval_Time.setText ( DbTime );
                                final DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                                ref.child ( "intervalTeaBreakEveningTimeStart" ).setValue ( DbTime );
                                ref.child ( "intervalTypeTeaBreakEvening" ).setValue ( TEA_BREAK_2 );
                                Intent intent = getIntent ();
                                startActivity ( intent );

                            }
                        } );
                    }
                } else if (spinnerClickResult.equals ( LUNCH_DINNER )) {

                    if (spinnerClickResult.equals ( lunch_Dinner_Str ) && !"".equals ( lunch_Dinner_StartTime ) && "".equals ( lunch_Dinner_EndTime )) {

                        startInterval.setEnabled ( BOOLEAN_FALSE );
                        endInterval.setEnabled ( BOOLEAN_TRUE );
                        permissionSpinner.setEnabled ( BOOLEAN_FALSE );
                        checkOutBox.setEnabled ( BOOLEAN_FALSE );
                        start_Interval_Time.setText ( String.valueOf ( lunch_Dinner_StartTime ) );
                        endInterval.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                intervalFunctionAnother ();
                                end_Interval_Time.setText ( DbTime );
                                breakFunction ( lunch_Dinner_StartTime, lunch_Dinner_EndTime );

                                final DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                                ref.child ( "intervalTypeLunch_DinnerTimeEnd" ).setValue ( DbTime );

                                Intent intent = getIntent ();
                                startActivity ( intent );
                            }
                        } );
                    } else if (spinnerClickResult.equals ( lunch_Dinner_Str ) && !"".equals ( lunch_Dinner_StartTime ) && !"".equals ( lunch_Dinner_EndTime )) {
                        intervalFunctionAnother ();
                        breakFunction ( lunch_Dinner_StartTime, lunch_Dinner_EndTime );
                        start_Interval_Time.setText ( lunch_Dinner_StartTime );
                        end_Interval_Time.setText ( lunch_Dinner_StartTime );
                        Toast.makeText ( DeliveryBoyAttendanceActivity.this, "Permission already taken", Toast.LENGTH_SHORT ).show ();
                    } else if (spinnerClickResult.equals ( LUNCH_DINNER ) && "".equals ( lunch_Dinner_StartTime ) && "".equals ( lunch_Dinner_EndTime )) {
                        endInterval.setEnabled ( BOOLEAN_FALSE );
                        startInterval.setEnabled ( BOOLEAN_TRUE );
                        breakFunction ( "", "" );
                        startInterval.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                endInterval.setEnabled ( BOOLEAN_TRUE );
                                startInterval.setEnabled ( BOOLEAN_FALSE );
                                checkOutBox.setEnabled ( BOOLEAN_FALSE );
                                permissionSpinner.setEnabled ( BOOLEAN_FALSE );
                                start_Interval_Time.setText ( DbTime );
                                final DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                                ref.child ( "intervalTypeLunch_DinnerTimeStart" ).setValue ( DbTime );
                                ref.child ( "intervalTypeLunch_Dinner" ).setValue ( LUNCH_DINNER );
                                Intent intent = getIntent ();
                                startActivity ( intent );
                            }
                        } );
                    }
                } else if (spinnerClickResult.equals ( PERMISSION )) {
                    if (spinnerClickResult.equals ( otherPermissionStr ) && !"".equals ( otherPermissionStartTime ) && "".equals ( otherPermissionEndTime )) {

                        startInterval.setEnabled ( BOOLEAN_FALSE );
                        endInterval.setEnabled ( BOOLEAN_TRUE );
                        checkOutBox.setEnabled ( BOOLEAN_FALSE );
                        permissionSpinner.setEnabled ( BOOLEAN_FALSE );
                        start_Interval_Time.setText ( String.valueOf ( otherPermissionStartTime ) );
                        endInterval.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                breakFunction ( otherPermissionStartTime, otherPermissionEndTime );
                                intervalFunctionAnother ();
                                end_Interval_Time.setText ( DbTime );

                                final DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                                ref.child ( "otherPermissionsTimeEnd" ).setValue ( DbTime );
                                Intent intent = getIntent ();
                                startActivity ( intent );
                            }
                        } );
                    } else if (spinnerClickResult.equals ( otherPermissionStr ) && !"".equals ( otherPermissionStartTime ) && !"".equals ( otherPermissionEndTime )) {
                        intervalFunctionAnother ();
                        breakFunction ( otherPermissionStartTime, otherPermissionEndTime );
                        start_Interval_Time.setText ( otherPermissionStartTime );
                        end_Interval_Time.setText ( otherPermissionEndTime );
                        Toast.makeText ( DeliveryBoyAttendanceActivity.this, "Permission already taken", Toast.LENGTH_SHORT ).show ();
                    } else if (spinnerClickResult.equals ( PERMISSION ) && "".equals ( otherPermissionStartTime ) && "".equals ( otherPermissionEndTime )) {
                        endInterval.setEnabled ( BOOLEAN_FALSE );
                        startInterval.setEnabled ( BOOLEAN_TRUE );
                        breakFunction ( "", "" );
                        startInterval.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                endInterval.setEnabled ( BOOLEAN_TRUE );
                                startInterval.setEnabled ( BOOLEAN_FALSE );
                                checkOutBox.setEnabled ( BOOLEAN_FALSE );
                                permissionSpinner.setEnabled ( BOOLEAN_FALSE );
                                start_Interval_Time.setText ( DbTime );
                                final DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                                ref.child ( "otherPermissionsTimeStart" ).setValue ( DbTime );
                                ref.child ( "otherPermissions" ).setValue ( PERMISSION );
                                Intent intent = getIntent ();
                                startActivity ( intent );
                            }
                        } );
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );


        checkInBox.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                preSetFunction ();
                checkOutBox.setChecked ( BOOLEAN_FALSE );
                checkInBox.setEnabled ( BOOLEAN_FALSE );
                checkOutBox.setEnabled ( BOOLEAN_TRUE );
                checkInDateTextView.setText ( DbDate );
                checkInTimeTextvView.setText ( DbTime );
                checkInDayTextView.setText ( DbDay );
                startInterval.setEnabled ( BOOLEAN_TRUE );
                if (checkInStatusStr == BOOLEAN_FALSE) {
                    final DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                    ref.child ( "checkInTime" ).setValue ( DbTime );
                    ref.child ( "checkInDate" ).setValue ( DbDate );
                    ref.child ( "checkInDay" ).setValue ( DbDay );
                    ref.child ( "checkInStatus" ).setValue ( BOOLEAN_TRUE );
                    Intent intent = getIntent ();
                    startActivity ( intent );
                    loadFunction ();

                }
            }
        } );

        checkOutBox.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AlertDialog.Builder builder = new AlertDialog.Builder ( DeliveryBoyAttendanceActivity.this, R.style.AlertDialogCustom );
                    builder.setTitle ( "Confirmation Message" );
                    builder.setMessage ( "Are you sure you want checkout?" );
                    builder.setNegativeButton ( "Yes", null );
                    builder.setNeutralButton ( "No", null );

                    dialog = builder.create ();
                    dialog.show ();
                    dialog.setCancelable ( BOOLEAN_FALSE );

                    Button positiveButton = dialog.getButton ( AlertDialog.BUTTON_NEGATIVE );
                    Button negativeButton = dialog.getButton ( AlertDialog.BUTTON_NEUTRAL );


                    positiveButton.setTextColor ( Color.WHITE );
                    positiveButton.setBackgroundColor ( getResources ().getColor ( R.color.colorPrimary ) );

                    negativeButton.setTextColor ( Color.WHITE );
                    negativeButton.setBackgroundColor ( getResources ().getColor ( R.color.colorPrimary ) );

                    positiveButton.setOnClickListener ( new View.OnClickListener () {
                        @Override
                        public void onClick(View v) {
                            final DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                            checkOutDateTextView.setText ( DbDate );
                            checkOutTimeTextvView.setText ( DbTime );
                            checkOutDayTextView.setText ( DbDay );

                            ref.child ( "checkOutTime" ).setValue ( DbTime );
                            ref.child ( "checkOutDate" ).setValue ( DbDate );
                            ref.child ( "checkOutDay" ).setValue ( DbDay );
                            ref.child ( "checkOutStatus" ).setValue ( BOOLEAN_TRUE );
                            permissionSpinner.setEnabled ( false );
                            permissionAdapter.notifyDataSetChanged ();
                            checkInBox.setChecked ( BOOLEAN_FALSE );
                            checkOutBox.setEnabled ( BOOLEAN_FALSE );
                            checkInBox.setEnabled ( BOOLEAN_FALSE );
                            startInterval.setEnabled ( BOOLEAN_FALSE );
                            endInterval.setEnabled ( BOOLEAN_FALSE );

                            try {
                                if (checkInTimeStr != null && !"".equals(checkInTimeStr) && (DbTime != null && !"".equals(DbTime))) {
                                    Date date1 = simpleDateFormat.parse(checkInTimeStr);
                                    Date date2 = simpleDateFormat.parse(DbTime);
                                    diff5 = date2.getTime() - date1.getTime();
                                    dayCheckInOut = (int) (diff5 / (1000 * 60 * 60 * 24));
                                    hourCheckInOut = (int) ((diff5 - (1000 * 60 * 60 * 24 * dayCheckInOut)) / (1000 * 60 * 60));
                                    minCheckInOut = (int) (diff5 - (1000 * 60 * 60 * 24 * dayCheckInOut) - (1000 * 60 * 60 * hourCheckInOut)) / (1000 * 60);
                                    hourCheckInOut = (hourCheckInOut < 0 ? -hourCheckInOut : hourCheckInOut);
                                    final DatabaseReference reference = CommonMethods.fetchFirebaseDatabaseReference(DELIVERY_BOY_ATTENDANCE_TABLE).child(DeliveryBoyProfileActivity.saved_id).child(DateUtils.fetchCurrentDate());
                                    reference.child("totalCheckInCheckOutTime").setValue(hourCheckInOut + "hrs " + minCheckInOut + "min");
                                }
                            }catch (ParseException e) {
                                e.printStackTrace ();
                            }
                            try {
                                if (teaBreakMorningStartTime != null && !"".equals(teaBreakMorningStartTime) && (teaBreakMorningEndTime != null && !"".equals(teaBreakMorningEndTime))) {
                                    Date date1 = simpleDateFormat.parse(teaBreakMorningStartTime);
                                    Date date2 = simpleDateFormat.parse(teaBreakMorningEndTime);
                                    diff1 = date2.getTime() - date1.getTime();
                                    dayFirstBreak = (int) (diff1 / (1000 * 60 * 60 * 24));
                                    hourFirstBreak = (int) ((diff1 - (1000 * 60 * 60 * 24 * dayFirstBreak)) / (1000 * 60 * 60));
                                    minFirstBreak = (int) (diff1 - (1000 * 60 * 60 * 24 * dayFirstBreak) - (1000 * 60 * 60 * hourFirstBreak)) / (1000 * 60);
                                    hourFirstBreak = (hourFirstBreak < 0 ? -hourFirstBreak : hourFirstBreak);
                                    final DatabaseReference reference = CommonMethods.fetchFirebaseDatabaseReference(DELIVERY_BOY_ATTENDANCE_TABLE).child(DeliveryBoyProfileActivity.saved_id).child(DateUtils.fetchCurrentDate());
                                    reference.child("totalTeaBreakMorning").setValue(hourFirstBreak + "hrs " + minFirstBreak + "min");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace ();
                            }

                            try {
                                if (lunch_Dinner_StartTime != null && !"".equals(lunch_Dinner_StartTime) && (lunch_Dinner_EndTime != null && !"".equals(lunch_Dinner_EndTime))) {
                                    Date date3 = simpleDateFormat.parse(lunch_Dinner_StartTime);
                                    Date date4 = simpleDateFormat.parse(lunch_Dinner_EndTime);
                                    diff2 = date4.getTime() - date3.getTime();
                                    dayLunchBreak = (int) (diff2 / (1000 * 60 * 60 * 24));
                                    hourLunchBreak = (int) ((diff2 - (1000 * 60 * 60 * 24 * dayLunchBreak)) / (1000 * 60 * 60));
                                    minLunchBreak = (int) (diff2 - (1000 * 60 * 60 * 24 * dayLunchBreak) - (1000 * 60 * 60 * hourLunchBreak)) / (1000 * 60);
                                    hourLunchBreak = (hourLunchBreak < 0 ? -hourLunchBreak : hourLunchBreak);
                                    final DatabaseReference reference = CommonMethods.fetchFirebaseDatabaseReference(DELIVERY_BOY_ATTENDANCE_TABLE).child(DeliveryBoyProfileActivity.saved_id).child(DateUtils.fetchCurrentDate());
                                    reference.child("totalLunch_Dinner_Break").setValue(hourLunchBreak + "hrs " + minLunchBreak + "min");
                                }
                            } catch (ParseException e) {

                            }
                            try {
                                if (otherPermissionStartTime != null && !"".equals(otherPermissionStartTime) && (otherPermissionEndTime != null && !"".equals(lunch_Dinner_EndTime))) {
                                    Date date5 = simpleDateFormat.parse(otherPermissionStartTime);
                                    Date date6 = simpleDateFormat.parse(otherPermissionEndTime);
                                    diff3 = date6.getTime() - date5.getTime();
                                    dayPermissionBreak = (int) (diff3 / (1000 * 60 * 60 * 24));
                                    hourPermissionBreak = (int) ((diff3 - (1000 * 60 * 60 * 24 * dayPermissionBreak)) / (1000 * 60 * 60));
                                    minPermissionBreak = (int) (diff3 - (1000 * 60 * 60 * 24 * dayPermissionBreak) - (1000 * 60 * 60 * hourPermissionBreak)) / (1000 * 60);
                                    hourPermissionBreak = (hourPermissionBreak < 0 ? -hourPermissionBreak : hourPermissionBreak);
                                    final DatabaseReference reference = CommonMethods.fetchFirebaseDatabaseReference(DELIVERY_BOY_ATTENDANCE_TABLE).child(DeliveryBoyProfileActivity.saved_id).child(DateUtils.fetchCurrentDate());
                                    reference.child("otherPermissionBreak").setValue(hourPermissionBreak + "hrs " + minPermissionBreak + "min");
                                }
                            } catch (ParseException e) {

                            }
                            try {  if (teaBreakEveningStartTime != null && !"".equals(teaBreakEveningStartTime) && (teaBreakEveningEndTime != null && !"".equals(teaBreakEveningEndTime))) {
                                Date date7 = simpleDateFormat.parse(teaBreakEveningStartTime);
                                Date date8 = simpleDateFormat.parse(teaBreakEveningEndTime);
                                diff4 = date8.getTime() - date7.getTime();
                                daySecondBreak = (int) (diff4 / (1000 * 60 * 60 * 24));
                                hourSecondBreak = (int) ((diff4 - (1000 * 60 * 60 * 24 * daySecondBreak)) / (1000 * 60 * 60));
                                minSecondBreak = (int) (diff4 - (1000 * 60 * 60 * 24 * daySecondBreak) - (1000 * 60 * 60 * hourSecondBreak)) / (1000 * 60);
                                hourSecondBreak = (hourSecondBreak < 0 ? -hourSecondBreak : hourSecondBreak);
                                final DatabaseReference reference = CommonMethods.fetchFirebaseDatabaseReference(DELIVERY_BOY_ATTENDANCE_TABLE).child(DeliveryBoyProfileActivity.saved_id).child(DateUtils.fetchCurrentDate());
                                reference.child("totalTeaBreakEvening").setValue(hourSecondBreak + "hrs " + minSecondBreak + "min");
                            }
                            } catch (ParseException e) {

                            }
                            int totalBreakTimeHours = ((hourCheckInOut) - (hourLunchBreak + hourFirstBreak + hourPermissionBreak + hourSecondBreak));
                            int totalBreakTimeMin = ((minCheckInOut) - (minLunchBreak + minFirstBreak + minPermissionBreak + minSecondBreak));
                            final DatabaseReference reference = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                            reference.child ( "actutalWorkingTime" ).setValue ( totalBreakTimeHours + "hrs " + totalBreakTimeMin + "min" );
                            final DatabaseReference breakTime = CommonMethods.fetchFirebaseDatabaseReference ( DELIVERY_BOY_ATTENDANCE_TABLE ).child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () );
                            breakTime.child ( "totalBreakTime" ).setValue ( (hourFirstBreak + hourSecondBreak + hourLunchBreak + hourPermissionBreak) + "hrs " + (minFirstBreak + minSecondBreak + minLunchBreak + minPermissionBreak) + "min" );
                            Intent intent = getIntent ();
                            startActivity ( intent );
                            loadFunction ();
                        }

                    } );
                    negativeButton.setOnClickListener ( new View.OnClickListener () {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss ();

                            checkOutBox.setChecked ( BOOLEAN_FALSE );

                        }
                    } );
                }

            }
        } );

        deliveryBoyAttendanceDataRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryBoyAttendanceMaxId = dataSnapshot.getChildrenCount ();
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

            item.setChecked ( BOOLEAN_TRUE );
            item.setCheckable ( BOOLEAN_TRUE );
            item.setEnabled ( BOOLEAN_TRUE );
        } else if (id == R.id.View_Profile_id) {
            Intent intent = new Intent ( getApplicationContext (), DeliveryBoyProfileActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog( DeliveryBoyAttendanceActivity.this);
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

                    Intent intent = new Intent( DeliveryBoyAttendanceActivity.this, RegisterOtp.class);
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
            Intent intent = new Intent ( getApplicationContext (), AttendanceHistory.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id==R.id.payments)
        {
            Intent intent=new Intent ( getApplicationContext (),DeliveryBoyPaymentActivity.class );
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
    public void preSetFunction()
    {
        check_in_check_out.setId ( String.valueOf ( deliveryBoyAttendanceMaxId + 1 ) );
        check_in_check_out.setDeliveryBoyId ( DeliveryBoyProfileActivity.saved_id );
        String creationDate = DateUtils.fetchCurrentDateAndTime ();
        check_in_check_out.setCreationDate ( creationDate );
        check_in_check_out.setCheckInTime ( "" );
        check_in_check_out.setCheckInDate ( "" );
        check_in_check_out.setCheckInDay ( "" );
        check_in_check_out.setCheckOutDay ( "" );
        check_in_check_out.setCheckOutTime ( "" );
        check_in_check_out.setCheckOutDate ( "" );
        check_in_check_out.setCheckInStatus ( BOOLEAN_FALSE );
        check_in_check_out.setCheckOutStatus ( BOOLEAN_FALSE );
        check_in_check_out.setOtherPermissions ( "" );
        check_in_check_out.setIntervalTypeLunch_Dinner ( "" );
        check_in_check_out.setOtherPermissionsTimeStart ( "" );
        check_in_check_out.setIntervalTypeTeaBreakMorning ( "" );
        check_in_check_out.setIntervalTypeTeaBreakEvening ( "" );
        check_in_check_out.setIntervalTeaBreakMorningTimeStart ( "" );
        check_in_check_out.setIntervalTeaBreakEveningTimeStart ( "" );
        check_in_check_out.setIntervalTypeLunch_DinnerTimeStart ( "" );
        check_in_check_out.setIntervalTeaBreakMorningTimeEnd ( "" );
        check_in_check_out.setIntervalTeaBreakEveningTimeEnd ( "" );
        check_in_check_out.setIntervalTypeLunch_DinnerTimeEnd ( "" );
        check_in_check_out.setOtherPermissionsTimeEnd ( "" );
        check_in_check_out.setTotalCheckInCheckOutTime ( "0 hrs 0 min" );
        check_in_check_out.setTotalTeaBreakMorning ( "" );
        check_in_check_out.setTotalTeaBreakEvening ( "" );
        check_in_check_out.setTotalLunch_Dinner_Break ( "" );
        check_in_check_out.setOtherPermissionBreak ( "" );
        check_in_check_out.setTotalBreakTime ( "0 hrs 0 min" );
        check_in_check_out.setActutalWorkingTime ( "0 hrs 0 min" );

        deliveryBoyAttendanceDataRef.child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () ).setValue ( check_in_check_out );
    }


    public void intervalFunctionAnother() {
        startInterval.setEnabled ( BOOLEAN_FALSE );
        endInterval.setEnabled ( BOOLEAN_FALSE );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadFunction() {
        deliveryBoyAttendanceDataRef.child ( DeliveryBoyProfileActivity.saved_id ).child ( DateUtils.fetchCurrentDate () ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount () > 0) {
                    check_in_check_out = dataSnapshot.getValue ( Check_In_Check_Out.class );
                    teaBreakMorningStr = check_in_check_out.getIntervalTypeTeaBreakMorning ();
                    teaBreakEveningStr = check_in_check_out.getIntervalTypeTeaBreakEvening ();
                    lunch_Dinner_Str = check_in_check_out.getIntervalTypeLunch_Dinner ();
                    otherPermissionStr = check_in_check_out.getOtherPermissions ();

                    checkInDateTextView.setText ( check_in_check_out.getCheckInDate () );
                    checkInTimeTextvView.setText ( check_in_check_out.getCheckInTime () );
                    checkInDayTextView.setText ( check_in_check_out.getCheckInDay () );

                    checkOutDateTextView.setText ( check_in_check_out.getCheckOutDate () );
                    checkOutTimeTextvView.setText ( check_in_check_out.getCheckOutTime () );
                    checkOutDayTextView.setText ( check_in_check_out.getCheckOutDay () );


                    checkInTimeStr = check_in_check_out.getCheckInTime ();
                    checkOutTimeStr = check_in_check_out.getCheckOutTime ();



                    teaBreakMorningStartTime = check_in_check_out.getIntervalTeaBreakMorningTimeStart ();
                    teaBreakEveningStartTime = check_in_check_out.getIntervalTeaBreakEveningTimeStart ();
                    lunch_Dinner_StartTime = check_in_check_out.getIntervalTypeLunch_DinnerTimeStart ();
                    otherPermissionStartTime = check_in_check_out.getOtherPermissionsTimeStart ();


                    teaBreakMorningEndTime = check_in_check_out.getIntervalTeaBreakMorningTimeEnd ();
                    teaBreakEveningEndTime = check_in_check_out.getIntervalTeaBreakEveningTimeEnd ();
                    lunch_Dinner_EndTime = check_in_check_out.getIntervalTypeLunch_DinnerTimeEnd ();
                    otherPermissionEndTime = check_in_check_out.getOtherPermissionsTimeEnd ();

                    breakFromWorkTxt.setText ( check_in_check_out.getTotalBreakTime () );
                    totalCheckedInTime.setText ( check_in_check_out.getTotalCheckInCheckOutTime () );
                    actualWorkingHours.setText ( check_in_check_out.getActutalWorkingTime () );

                    breakOneTxt.setText ( check_in_check_out.getTotalTeaBreakMorning () );
                    breakTwoTxt.setText ( check_in_check_out.getTotalTeaBreakEvening () );
                    lunchTxt.setText ( check_in_check_out.getTotalLunch_Dinner_Break () );
                    permissionTxt.setText ( check_in_check_out.getOtherPermissionBreak () );


                    if (teaBreakMorningStr != null && !"".equals ( teaBreakMorningStartTime ) && "".equals ( teaBreakMorningEndTime )) {

                        permissionSpinner.setSelection ( 1 );
                    } else if (teaBreakEveningStr != null && !"".equals ( teaBreakEveningStartTime ) && "".equals ( teaBreakEveningEndTime )) {

                        permissionSpinner.setSelection ( 2 );
                    } else if (lunch_Dinner_Str != null && !"".equals ( lunch_Dinner_StartTime ) && "".equals ( lunch_Dinner_EndTime )) {

                        permissionSpinner.setSelection ( 3 );
                    } else if (otherPermissionStr != null && !"".equals ( otherPermissionStartTime ) && "".equals ( otherPermissionEndTime )) {

                        permissionSpinner.setSelection ( 4 );
                    } else {

                        permissionSpinner.setSelection ( 0 );
                    }


                    boolean checkInStatusStr = check_in_check_out.isCheckInStatus ();
                    boolean checkOutStatusStr = check_in_check_out.isCheckOutStatus ();

                    if (checkInStatusStr == BOOLEAN_TRUE) {
                        checkInBox.setChecked ( BOOLEAN_FALSE );
                        checkInBox.setEnabled ( BOOLEAN_FALSE );
                        checkOutBox.setEnabled ( BOOLEAN_TRUE );
                        permissionSpinner.setEnabled ( true );
                        intervalFunctionAnother ();


                    } else if (checkInStatusStr == BOOLEAN_FALSE) {
                        checkInBox.setChecked ( BOOLEAN_FALSE );
                        checkInBox.setEnabled ( BOOLEAN_TRUE );
                    }
                    if (checkOutStatusStr == BOOLEAN_TRUE) {
                        checkInBox.setEnabled ( BOOLEAN_FALSE );
                        checkOutBox.setEnabled ( BOOLEAN_FALSE );
                        permissionSpinner.setEnabled ( false );
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


    }

    public void breakFunction(String start, String end) {
        start_Interval_Time.setText ( start );
        end_Interval_Time.setText ( end );

    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent( DeliveryBoyAttendanceActivity.this, DeliveryBoyProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void createNotification(String res,String orderid) {
        int count=0;

        if (count==0) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DeliveryBoyAttendanceActivity.this, default_notification_channel_id)
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
    public void loadFunction1()
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
                                                                if (!((Activity) DeliveryBoyAttendanceActivity.this).isFinishing()) {
                                                                    DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                            .getReference("OrderDetails").child(billDetails.getOrderId());
                                                                    orderDetailsRef.child("notificationStatus").setValue("true");
                                                                    notify = false;
                                                                }

                                                                if (!((Activity) DeliveryBoyAttendanceActivity.this).isFinishing()) {
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if ("LocationUpdateEnable".equals(CommonFiles.KEY_REQUESTING_LOCATION_UODATES))
        {
            setButtonState(sharedPreferences.getBoolean(CommonFiles.KEY_REQUESTING_LOCATION_UODATES,false));
        }
    }

    private void setButtonState(boolean aBoolean)
    {
        if (aBoolean)
        {
            requestLocation.setEnabled(false);
            removeUpdated.setEnabled(true);
        }
        else
        {
            requestLocation.setEnabled(true);
            removeUpdated.setEnabled(false);
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event) {
        if (event != null) {
            String data = new StringBuilder().append(event.getLocation().getLatitude()).append(event.getLocation().getLongitude()).toString();
            final DatabaseReference deliveryBoyDetailsDataRef = FirebaseDatabase.getInstance("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("DeliveryBoyLoginDetails").child(DeliveryBoyProfileActivity.saved_id);
            deliveryBoyDetailsDataRef.child("deliveryBoyLatitude").setValue(event.getLocation().getLatitude());
            deliveryBoyDetailsDataRef.child("deliveryBoyLongtitude").setValue(event.getLocation().getLongitude());
        }
    }
}