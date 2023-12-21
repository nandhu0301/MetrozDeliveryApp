package com.smiligenceUAT1.metrozdeliveryappnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.PhoneAuthOptions;
import com.smiligenceUAT1.metrozdeliveryappnew.Common.DateUtils;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegiterLogin extends AppCompatActivity {
    EditText otpverificationEdt;
    Button proceddButton;
    TextView resendOtp;
    TextView numberText;
    TextView resendOtpTimer;
    long deliveryBoyMaxId = 0;
    String verificationId;
    FirebaseAuth mAuth;
    DatabaseReference deliveryBoyLoginDetails;
    SweetAlertDialog errorDialog;
    String getPhoneNumberIntent;
    String customerId;
    String customerPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_resiter_login );

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        deliveryBoyLoginDetails = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference ( "DeliveryBoyLoginDetails" );

        otpverificationEdt = findViewById ( R.id.inputCode );
        proceddButton = findViewById ( R.id.smsVerificationButton );
        resendOtp = findViewById ( R.id.resend_timer );
        numberText = findViewById ( R.id.numberText );
        resendOtpTimer = findViewById ( R.id.resend_timer );


        mAuth = FirebaseAuth.getInstance ();


        getPhoneNumberIntent = getIntent ().getStringExtra ( "phonenumber" );
        sendVerificationCode ( "+91" + getPhoneNumberIntent );
        numberText.setText ( "+91 " + getPhoneNumberIntent );
        resendOtpTimer ();


        proceddButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String code = otpverificationEdt.getText ().toString ().trim ();

                if (code.isEmpty () || code.length () < 6) {
                    otpverificationEdt.setError ( "Enter valid code..." );
                    otpverificationEdt.requestFocus ();
                    return;
                }
                verifyCode ( code );
            }
        } );

        resendOtp.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

            }
        } );

        deliveryBoyLoginDetails.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryBoyMaxId = dataSnapshot.getChildrenCount ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        resendOtpTimer.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                sendVerificationCode ( "+91" + getPhoneNumberIntent );

            }
        } );


    }

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential ( verificationId, code );
            signInWithCredential ( credential );
        } catch (Exception e) {
            Toast toast = Toast.makeText ( getApplicationContext (), "Verification Code is wrong, try again", Toast.LENGTH_SHORT );
            toast.setGravity ( Gravity.CENTER, 0, 0 );
            toast.show ();
        }

    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential ( credential )
                .addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ()) {

                            Query query = deliveryBoyLoginDetails.orderByChild ( "mobileNumber" ).equalTo ( getPhoneNumberIntent );
                            query.addValueEventListener ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount () > 0) {

                                        for ( DataSnapshot detailsSnap : dataSnapshot.getChildren () )
                                        {
                                            UserDetails deliveryBoy = detailsSnap.getValue ( UserDetails.class );
                                            customerId = deliveryBoy.getDeliveryBoyId ();
                                            customerPhoneNumber = deliveryBoy.getMobileNumber ();
                                        }
                                        SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                                        SharedPreferences.Editor editor = sharedPreferences.edit ();
                                        editor.putString ( "customerPhoneNumber", customerPhoneNumber );
                                        editor.putString ( "customerId", customerId );
                                        editor.commit ();
                                        Intent HomeActivity = new Intent ( RegiterLogin.this, OnBoardScreenActivity.class );
                                        setResult ( RESULT_OK, null );
                                        startActivity ( HomeActivity );
                                    } else {
                                        UserDetails deliveryBoyDetails =
                                                new UserDetails ();
                                        String createdDate = DateUtils.fetchCurrentDateAndTime ();
                                        deliveryBoyDetails.setAddedDate ( createdDate );
                                        deliveryBoyDetails.setDeliveryBoyId ( String.valueOf ( deliveryBoyMaxId + 1 ) );
                                        deliveryBoyDetails.setFirstName ( "" );
                                        deliveryBoyDetails.setLastName ( "" );
                                        deliveryBoyDetails.setEmailId ( "" );
                                        deliveryBoyDetails.setMobileNumber ( getPhoneNumberIntent );
                                        deliveryBoyDetails.setZipcode ( "" );
                                        deliveryBoyDetails.setBankName ( "" );
                                        deliveryBoyDetails.setBankBranchName ( "" );
                                        deliveryBoyDetails.setBankIfscCode ( "" );
                                        deliveryBoyDetails.setAccountNumber ( "" );
                                        deliveryBoyDetails.setAadharNumber ( "" );
                                        deliveryBoyDetails.setBikeNumber ( "" );
                                        deliveryBoyDetails.setBikeLicenseNumber ( "" );
                                        deliveryBoyDetails.setDeliveryBoyProfile ( "" );
                                        deliveryBoyDetails.setDrivingLicenseProof ( "" );
                                        deliveryBoyDetails.setAadharIdProof ( "" );
                                        deliveryBoyDetails.setDeliveryboyApprovalStatus ( "" );
                                        deliveryBoyDetails.setCommentsIfAny ( "" );

                                        deliveryBoyLoginDetails.child ( String.valueOf ( deliveryBoyMaxId + 1 ) ).setValue ( deliveryBoyDetails );
                                        SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                                        SharedPreferences.Editor editor = sharedPreferences.edit ();
                                        editor.putString ( "customerPhoneNumber", getPhoneNumberIntent );
                                        editor.putString ( "customerId", String.valueOf ( deliveryBoyMaxId + 1 ) );
                                        editor.commit ();
                                        Intent HomeActivity = new Intent ( RegiterLogin.this, DeliveryBoyProfileActivity.class );
                                        setResult ( RESULT_OK, null );
                                        startActivity ( HomeActivity );
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );
                        } else {
                            errorDialog = new SweetAlertDialog ( RegiterLogin.this, SweetAlertDialog.ERROR_TYPE );
                            errorDialog.getProgressHelper().setBarColor(Color.parseColor("#3F0300"));
                            errorDialog.setCancelable ( false );
                            errorDialog
                                    .setContentText ( "Invalid OTP" ).setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener () {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    errorDialog.dismiss ();
                                }
                            } ).show ();
                        }
                    }
                } );


    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks () {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent ( s, forceResendingToken );
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode ();
            if (code != null) {
                otpverificationEdt.setText ( code );
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText ( RegiterLogin.this, e.getMessage (), Toast.LENGTH_LONG ).show ();
        }
    };

    private void resendOtpTimer()
    {
        resendOtpTimer.setClickable ( false );
        resendOtpTimer.setEnabled ( false );
        resendOtpTimer.setTextColor ( ContextCompat.getColor ( RegiterLogin.this, R.color.White ) );
        new CountDownTimer ( 60000, 1000 ) {
            int secondsLeft = 0;

            public void onTick(long ms)
            {
                if (Math.round ( (float) ms / 1000.0f ) != secondsLeft)
                {
                    secondsLeft = Math.round ( (float) ms / 1000.0f );
                    resendOtpTimer.setText ( "Resend OTP ( " + secondsLeft + " )" );
                }
            }

            public void onFinish() {
                resendOtpTimer.setClickable ( true );
                resendOtpTimer.setEnabled ( true );
                resendOtpTimer.setText ( "Resend OTP" );
                resendOtpTimer.setTextColor ( ContextCompat.getColor ( RegiterLogin.this, R.color.White ) );

            }
        }.start ();


    }

}