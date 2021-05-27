package com.propster.allRoles;

public class PropertyExpensesListItem {

    private final int propertyId;
    private final String propertyName;
    private final int propertyExpensesId;
    private final String propertyExpensesDescription;
    private final String propertyExpensesDate;
    private final String propertyExpensesAmount;

    public PropertyExpensesListItem(int propertyId, String propertyName, int propertyExpensesId, String propertyExpensesDescription, String propertyExpensesDate, String propertyExpensesAmount) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.propertyExpensesId = propertyExpensesId;
        this.propertyExpensesDescription = propertyExpensesDescription;
        this.propertyExpensesDate = propertyExpensesDate;
        this.propertyExpensesAmount = propertyExpensesAmount;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public int getPropertyExpensesId() {
        return propertyExpensesId;
    }

    public String getPropertyExpensesDescription() {
        return propertyExpensesDescription;
    }

    public String getPropertyExpensesDate() {
        return propertyExpensesDate;
    }

    public String getPropertyExpensesAmount() {
        return propertyExpensesAmount;
    }
}
