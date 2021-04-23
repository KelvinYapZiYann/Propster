package com.propster.landlord;

public class LandlordPropertyListItem {

    private String propertyName;
    private int tenantCount;
    private float payment;

    public LandlordPropertyListItem(String propertyName, int tenantCount, float payment) {
        this.propertyName = propertyName;
        this.tenantCount = tenantCount;
        this.payment = payment;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public int getTenantCount() {
        return tenantCount;
    }

    public float getPayment() {
        return payment;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setTenantCount(int tenantCount) {
        this.tenantCount = tenantCount;
    }

    public void setPayment(float payment) {
        this.payment = payment;
    }
}
