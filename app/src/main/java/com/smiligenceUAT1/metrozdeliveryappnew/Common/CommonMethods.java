package com.smiligenceUAT1.metrozdeliveryappnew.Common;

import android.view.MenuItem;

import com.smiligenceUAT1.metrozdeliveryappnew.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.smiligenceUAT1.metrozdeliveryappnew.DeliveryBoyProfileActivity.*;


public class CommonMethods
{
    public static DatabaseReference fetchFirebaseDatabaseReference(String FirebaseTableName) {

        DatabaseReference mDataRef = FirebaseDatabase.getInstance ("https://testmetrozproject-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference (  FirebaseTableName );

        return mDataRef;
    }





    public static void screenPermissions()
    {
        if ("Waiting for approval".equals ( approvedStatus ) || "".equals ( approvedStatus ) || "Rejected".equals ( approvedStatus ) )
        {
            MenuItem myAssignedOrders = menuNav.findItem ( R.id.View_Orders_id );
            myAssignedOrders.setVisible ( false );
            MenuItem myDailyAttendance = menuNav.findItem ( R.id.checkin_checkout_id );
            myDailyAttendance.setVisible ( false );
            MenuItem attendanceAndPayments = menuNav.findItem ( R.id.attendanceHistory );
            attendanceAndPayments.setVisible ( false );
            MenuItem myProfile = menuNav.findItem ( R.id.View_Profile_id );
            myProfile.setVisible ( true );
            MenuItem payments = menuNav.findItem ( R.id.payments );
            payments.setVisible ( false );
            MenuItem weeklypayments = menuNav.findItem ( R.id.weeklypayments );
            weeklypayments.setVisible ( false );
            MenuItem logout = menuNav.findItem ( R.id.logout );
            logout.setVisible ( true );

        } else if ("Approved".equals ( approvedStatus )) {
            MenuItem myAssignedOrders = menuNav.findItem ( R.id.View_Orders_id );
            myAssignedOrders.setVisible ( true );
            MenuItem myDailyAttendance = menuNav.findItem ( R.id.checkin_checkout_id );
            myDailyAttendance.setVisible ( true );
            MenuItem attendanceAndPayments = menuNav.findItem ( R.id.attendanceHistory );
            attendanceAndPayments.setVisible ( true );
            MenuItem myProfile = menuNav.findItem ( R.id.View_Profile_id );
            myProfile.setVisible ( true );
            MenuItem logout = menuNav.findItem ( R.id.logout );
            logout.setVisible ( true );
            MenuItem weeklypayments = menuNav.findItem ( R.id.weeklypayments );
            weeklypayments.setVisible ( true );
            MenuItem payments = menuNav.findItem ( R.id.payments );
            payments.setVisible ( true );
        }
    }

}
