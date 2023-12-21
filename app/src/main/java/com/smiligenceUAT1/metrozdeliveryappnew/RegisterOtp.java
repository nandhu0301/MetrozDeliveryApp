package com.smiligenceUAT1.metrozdeliveryappnew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterOtp extends AppCompatActivity {

    EditText otpEditText;
    Button sendOtpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_register_otp );


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        otpEditText = findViewById ( R.id.phoneNumber );
        sendOtpText = findViewById ( R.id.smsVerificationButton );


        sendOtpText.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v)
            {
                if ("".equals ( otpEditText.getText ().toString () ))
                {
                    otpEditText.setError ( "Required" );
                }
                else {
                    Intent intent = new Intent ( RegisterOtp.this, RegiterLogin.class );
                    intent.putExtra ( "phonenumber", otpEditText.getText ().toString () );
                    startActivity ( intent );
                    Toast.makeText ( RegisterOtp.this, "Otp Sent Succesfully", Toast.LENGTH_SHORT ).show ();
                }
            }
        } );


    }

}