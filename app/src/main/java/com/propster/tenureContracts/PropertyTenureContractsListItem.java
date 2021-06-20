package com.propster.tenureContracts;

public class PropertyTenureContractsListItem {

    private final int propertyId;
    private String propertyName;
    private final int tenantId;
    private String tenantName;
    private final int propertyTenureContractsId;
    private final String propertyTenureContractsName;
    private final String propertyTenureContractsEndDate;
    private final String propertyTenureContractsRentalAmount;

    public PropertyTenureContractsListItem(int propertyId, String propertyName, int tenantId, String tenantName, int propertyTenureContractsId,
                                           String propertyTenureContractsName, String propertyTenureContractsEndDate, String propertyTenureContractsRentalAmount) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.propertyTenureContractsId = propertyTenureContractsId;
        this.propertyTenureContractsName = propertyTenureContractsName;
        this.propertyTenureContractsEndDate = propertyTenureContractsEndDate;
        this.propertyTenureContractsRentalAmount = propertyTenureContractsRentalAmount;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public int getTenantId() {
        return tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public int getPropertyTenureContractsId() {
        return propertyTenureContractsId;
    }

    public String getPropertyTenureContractsName() {
        return propertyTenureContractsName;
    }

    public String getPropertyTenureContractsEndDate() {
        return propertyTenureContractsEndDate;
    }

    public String getPropertyTenureContractsRentalAmount() {
        return propertyTenureContractsRentalAmount;
    }
}
