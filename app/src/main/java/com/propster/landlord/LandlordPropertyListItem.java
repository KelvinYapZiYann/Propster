package com.propster.landlord;

public class LandlordPropertyListItem {

    private final String propertyName;
    private final int propertyId;
    private final int[] tenantIdArray;
    private final int[] propertyExpensesIdArray;
//    private final int tenantCount;
//    private final int totalTenantCount;
    private final float payment;
    private final int age;

//    public LandlordPropertyListItem(String propertyName, int propertyId, int tenantCount, int totalTenantCount, float payment, int age) {
//        this.propertyName = propertyName;
//        this.propertyId = propertyId;
//        this.tenantCount = tenantCount;
//        this.totalTenantCount = totalTenantCount;
//        this.payment = payment;
//        this.age = age;
//    }

    public LandlordPropertyListItem(String propertyName, int propertyId, int[] tenantIdArray, int[] propertyExpensesIdArray, float payment, int age) {
        this.propertyName = propertyName;
        this.propertyId = propertyId;
        this.tenantIdArray = tenantIdArray;
        this.propertyExpensesIdArray = propertyExpensesIdArray;
        this.payment = payment;
        this.age = age;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public int[] getTenantIdArray() {
        return tenantIdArray;
    }

    public int[] getPropertyExpensesIdArray() {
        return propertyExpensesIdArray;
    }

    //    public int getTenantCount() {
//        return tenantCount;
//    }
//
//    public int getTotalTenantCount() {
//        return totalTenantCount;
//    }

    public float getPayment() {
        return payment;
    }

    public int getAge() {
        return age;
    }

//    public void setPropertyName(String propertyName) {
//        this.propertyName = propertyName;
//    }
//
//    public void setTenantCount(int tenantCount) {
//        this.tenantCount = tenantCount;
//    }
//
//    public void setPayment(float payment) {
//        this.payment = payment;
//    }
}
