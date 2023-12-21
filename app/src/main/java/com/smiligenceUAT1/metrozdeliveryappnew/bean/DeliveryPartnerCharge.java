package com.smiligenceUAT1.metrozdeliveryappnew.bean;

public class DeliveryPartnerCharge
{
    String creationDate;
    int deliveryChargeFromStoreToCustomer;
    int deliveryChargeFromDeliveryBoyToStore;

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getDeliveryChargeFromStoreToCustomer() {
        return deliveryChargeFromStoreToCustomer;
    }

    public void setDeliveryChargeFromStoreToCustomer(int deliveryChargeFromStoreToCustomer) {
        this.deliveryChargeFromStoreToCustomer = deliveryChargeFromStoreToCustomer;
    }

    public int getDeliveryChargeFromDeliveryBoyToStore() {
        return deliveryChargeFromDeliveryBoyToStore;
    }

    public void setDeliveryChargeFromDeliveryBoyToStore(int deliveryChargeFromDeliveryBoyToStore) {
        this.deliveryChargeFromDeliveryBoyToStore = deliveryChargeFromDeliveryBoyToStore;
    }
}
