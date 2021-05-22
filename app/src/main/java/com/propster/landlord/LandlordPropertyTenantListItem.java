package com.propster.landlord;

public class LandlordPropertyTenantListItem {

    private final int tenantId;
    private final String tenantFirstName;
    private final String tenantLastName;
    private final String tenureEndDate;
    private final float payment;
    private final int age;

    public LandlordPropertyTenantListItem(int tenantId, String tenantFirstName, String tenantLastName, String tenureEndDate, float payment, int age) {
        this.tenantId = tenantId;
        this.tenantFirstName = tenantFirstName;
        this.tenantLastName = tenantLastName;
        this.tenureEndDate = tenureEndDate;
        this.payment = payment;
        this.age = age;
    }

    public int getTenantId() {
        return tenantId;
    }

    public String getTenantFirstName() {
        return tenantFirstName;
    }

    public String getTenantLastName() {
        return tenantLastName;
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
