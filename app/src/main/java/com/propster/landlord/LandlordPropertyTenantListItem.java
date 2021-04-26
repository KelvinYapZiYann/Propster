package com.propster.landlord;

public class LandlordPropertyTenantListItem {

    private final String tenantName;
    private final String tenureEndDate;
    private final float payment;
    private final int age;

    public LandlordPropertyTenantListItem(String tenantName, String tenureEndDate, float payment, int age) {
        this.tenantName = tenantName;
        this.tenureEndDate = tenureEndDate;
        this.payment = payment;
        this.age = age;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getTenureEndDate() {
        return tenureEndDate;
    }

    public float getPayment() {
        return payment;
    }

    public int getAge() {
        return age;
    }
}
