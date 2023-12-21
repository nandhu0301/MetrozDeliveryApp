package com.smiligenceUAT1.metrozdeliveryappnew.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.smiligenceUAT1.metrozdeliveryappnew.DeliveryBoyProfileActivity;
import com.smiligenceUAT1.metrozdeliveryappnew.R;
import com.smiligenceUAT1.metrozdeliveryappnew.RegisterOtp;

public class SplashActivity extends AppCompatActivity {
    String userName;
    SharedPreferences.Editor editor;
    TextView poweredByTExt;

    private Handler handler;
    private long startTime, currentTime, finishedTime = 0L;
    private int duration = 22000 / 4;
    private int endTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_splash );
        poweredByTExt=findViewById(R.id.powredBySmiligence);
        poweredByTExt.setText("Powered by Smiligence ");


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        new Handler ().postDelayed ( new Runnable () {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                editor = sharedPreferences.edit ();
                userName = sharedPreferences.getString ( "customerId", "" );

                if (!"".equals ( userName )) {
                    Intent intent = new Intent ( SplashActivity.this, DeliveryBoyProfileActivity.class );
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                    finish ();
                }
                else if ("".equals ( userName ) || userName.equals ( null ))
                {
                    Intent intent = new Intent ( SplashActivity.this, RegisterOtp.class );
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                }
            }
        }, 5000 );

        handler = new Handler();
        startTime = Long.valueOf(System.currentTimeMillis());
        currentTime = startTime;

        handler.postDelayed(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {

                currentTime = Long.valueOf(System.currentTimeMillis());
                finishedTime = Long.valueOf(currentTime)
                        - Long.valueOf(startTime);

                if (finishedTime >= duration + 30) {

                } else {
                    endTime = (int) (finishedTime / 250);// divide this by

                    Spannable spannableString = new SpannableString(poweredByTExt
                            .getText());
                    spannableString.setSpan(new ForegroundColorSpan(
                                    getResources().getColor(R.color.cyanbase)), 0, endTime,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    poweredByTExt.setText(spannableString);
                    handler.postDelayed(this, 10);
                }
            }
        }, 10);
    }

}