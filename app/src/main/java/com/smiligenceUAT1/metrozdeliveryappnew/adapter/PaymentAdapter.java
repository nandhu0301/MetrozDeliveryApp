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
import com.smiligenceUAT1.metrozdeliveryappnew.bean.OrderDetails;

import java.util.List;

public class PaymentAdapter extends ArrayAdapter<OrderDetails> {

    Activity context;
    List<OrderDetails> billDetailsList;
    TextView orderId, orderDate, distance, amount;


    public PaymentAdapter(@NonNull Activity context, List<OrderDetails> billDetailsList) {
        super ( context, R.layout.payment_details_layout, billDetailsList );
        this.context = context;
        this.billDetailsList = billDetailsList;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater ();
        View listview = inflater.inflate ( R.layout.payment_details_layout, null, true );

        orderId = (TextView) listview.findViewById ( R.id.OrderId );
        orderDate = (TextView) listview.findViewById ( R.id.OrderDate );
        distance = (TextView) listview.findViewById ( R.id.Distance );
        amount = (TextView) listview.findViewById ( R.id.Amount );
        final OrderDetails billDetails = billDetailsList.get ( position );
        orderId.setText ( String.valueOf ( billDetails.getOrderId () ) );
        orderDate.setText ( billDetails.getPaymentDate () );
        distance.setText ( String.valueOf ( billDetails.getTotalDistanceTraveled ()) +" Km" );
        amount.setText ( "₹ "+String.valueOf (billDetails.getTotalFeeForDeliveryBoy ()) );
        return listview;
    }
}