package com.propster.allRoles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.propster.R;

import java.util.List;

public class PropertyExpensesListAdapter extends ArrayAdapter<PropertyExpensesListItem> {

    public PropertyExpensesListAdapter(Context context, List<PropertyExpensesListItem> propertyExpensesListItemList) {
        super(context, 0, propertyExpensesListItemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_property_expenses_item, parent, false);
        }
        PropertyExpensesListItem propertyExpensesListItem = getItem(position);
        TextView propertyExpensesListItemDescription = convertView.findViewById(R.id.propertyExpensesListItemDescription);
        TextView propertyExpensesListItemPropertyName = convertView.findViewById(R.id.propertyExpensesListItemPropertyName);
        TextView propertyExpensesListItemDate = convertView.findViewById(R.id.propertyExpensesListItemDate);
        TextView propertyExpensesListItemAmount = convertView.findViewById(R.id.propertyExpensesListItemAmount);
        propertyExpensesListItemDescription.setText(propertyExpensesListItem.getPropertyExpensesDescription());
        propertyExpensesListItemPropertyName.setText(propertyExpensesListItem.getPropertyName());
        String date = propertyExpensesListItem.getPropertyExpensesDate();
        propertyExpensesListItemDate.setText(date.length() > 10 ? date.substring(0, 10) : date);
        propertyExpensesListItemAmount.setText(propertyExpensesListItem.getPropertyExpensesAmount());
        return convertView;
    }

}
