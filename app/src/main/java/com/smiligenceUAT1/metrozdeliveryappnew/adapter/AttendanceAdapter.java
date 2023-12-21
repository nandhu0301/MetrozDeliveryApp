package com.smiligenceUAT1.metrozdeliveryappnew.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smiligenceUAT1.metrozdeliveryappnew.R;
import com.smiligenceUAT1.metrozdeliveryappnew.bean.Check_In_Check_Out;

import java.util.List;

public class AttendanceAdapter  extends ArrayAdapter<Check_In_Check_Out> {

    Activity context;
    List<Check_In_Check_Out> billDetailsList;
    TextView date, checkInOutTime, intervalTime;

    public AttendanceAdapter(@NonNull Activity context, List<Check_In_Check_Out> billDetailsList) {
        super ( context, R.layout.attendance_detail_layout, billDetailsList );
        this.context = context;
        this.billDetailsList = billDetailsList;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater ();
        View listview = inflater.inflate ( R.layout.attendance_detail_layout, null, true );

        date = (TextView) listview.findViewById ( R.id.date );
        checkInOutTime = (TextView) listview.findViewById ( R.id.checkInTime );
        intervalTime = (TextView) listview.findViewById ( R.id.checkOutTime );


        final Check_In_Check_Out billDetails = billDetailsList.get ( position );


        date.setText ( String.valueOf ( billDetails.getCheckInDate () ) );
        checkInOutTime.setText ( billDetails.getTotalBreakTime () );
        intervalTime.setText ( billDetails.getTotalCheckInCheckOutTime () );

        return listview;
    }
}
