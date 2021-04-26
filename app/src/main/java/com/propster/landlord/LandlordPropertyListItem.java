package com.propster.landlord;

public class LandlordPropertyListItem {

    private String propertyName;
    private int tenantCount;
    private int totalTenantCount;
    private float payment;
    private int age;

    public LandlordPropertyListItem(String propertyName, int tenantCount, int totalTenantCount, float payment, int age) {
        this.propertyName = propertyName;
        this.tenantCount = tenantCount;
        this.totalTenantCount = totalTenantCount;
        this.payment = payment;
        this.age = age;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public int getTenantCount() {
        return tenantCount;
    }

    public int getTotalTenantCount() {
        return totalTenantCount;
    }

    public float getPayment() {
        return payment;
    }

    public int getAge() {
        return age;
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
