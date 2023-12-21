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

public class DeliveryBoyAdapter  extends ArrayAdapter<OrderDetails> {

    Activity context;
    List<OrderDetails> orderDetailsList;
    TextView orderId, storeName, placedDate, orderStatus, assigenedToStatus;

    public DeliveryBoyAdapter(@NonNull Activity context, List<OrderDetails> orderDetailsList) {
        super ( context, R.layout.delivery_boy_details_layout, orderDetailsList );
        this.context = context;
        this.orderDetailsList = orderDetailsList;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater ();
        View listview = inflater.inflate ( R.layout.delivery_boy_details_layout, null, true );

        orderId = (TextView) listview.findViewById ( R.id.orderId );
        storeName = (TextView) listview.findViewById ( R.id.storeName );
        placedDate = (TextView) listview.findViewById ( R.id.placedDate );
        orderStatus = (TextView) listview.findViewById ( R.id.Status );
        assigenedToStatus = (TextView) listview.findViewById ( R.id.storeAddress );

        final OrderDetails orderDetails = orderDetailsList.get ( position );

        orderId.setText ( String.valueOf ( orderDetails.getOrderId () ) );
        storeName.setText ( orderDetails.getStoreName () );
        placedDate.setText ( orderDetails.getPaymentDate () );
        orderStatus.setText ( orderDetails.getOrderStatus () );
        assigenedToStatus.setText ( orderDetails.getStoreAddress () );
        return listview;
    }
}