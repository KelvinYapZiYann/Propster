package com.propster.tenant;

public class TenantPropertyListItem {

    private final String propertyName;
    private final int propertyId;
    private final float payment;
    private final int age;

    public TenantPropertyListItem(String propertyName, int propertyId, float payment, int age) {
        this.propertyName = propertyName;
        this.propertyId = propertyId;
        this.payment = payment;
        this.age = age;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public float getPayment() {
        return payment;
    }

    public int getAge() {
        return age;
    }

}
