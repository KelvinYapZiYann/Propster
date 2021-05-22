package com.propster.allRoles;

public class PropertyExpensesListItem {

    private final int propertyExpensesId;
    private final String propertyExpensesDescription;
    private final String propertyExpensesPropertyName;
    private final String propertyExpensesDate;
    private final String propertyExpensesAmount;

    public PropertyExpensesListItem(int propertyExpensesId, String propertyExpensesDescription, String propertyExpensesPropertyName, String propertyExpensesDate, String propertyExpensesAmount) {
        this.propertyExpensesId = propertyExpensesId;
        this.propertyExpensesDescription = propertyExpensesDescription;
        this.propertyExpensesPropertyName = propertyExpensesPropertyName;
        this.propertyExpensesDate = propertyExpensesDate;
        this.propertyExpensesAmount = propertyExpensesAmount;
    }

    public int getPropertyExpensesId() {
        return propertyExpensesId;
    }

    public String getPropertyExpensesDescription() {
        return propertyExpensesDescription;
    }

    public String getPropertyExpensesPropertyName() {
        return propertyExpensesPropertyName;
    }

    public String getPropertyExpensesDate() {
        return propertyExpensesDate;
    }

    public String getPropertyExpensesAmount() {
        return propertyExpensesAmount;
    }
}
