package com.propster.allRoles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.propster.R;

import java.util.List;

public class PropertyTenureContractsListAdapter extends ArrayAdapter<PropertyTenureContractsListItem> {

    public PropertyTenureContractsListAdapter(Context context, List<PropertyTenureContractsListItem> propertyTenureContractsListItemList) {
        super(context, 0, propertyTenureContractsListItemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_property_tenure_contracts_item, parent, false);
        }
        PropertyTenureContractsListItem propertyTenureContractsListItemList = getItem(position);
        TextView propertyTenureContractsListItemName = convertView.findViewById(R.id.propertyTenureContractsListItemName);
        TextView propertyTenureContractsListItemPropertyName = convertView.findViewById(R.id.propertyTenureContractsListItemPropertyName);
        TextView propertyTenureContractsListItemTenantName = convertView.findViewById(R.id.propertyTenureContractsListItemTenantName);
        TextView propertyTenureContractsListItemEndDate = convertView.findViewById(R.id.propertyTenureContractsListItemEndDate);
        TextView propertyTenureContractsListItemRentalAmount = convertView.findViewById(R.id.propertyTenureContractsListItemRentalAmount);
        propertyTenureContractsListItemName.setText(propertyTenureContractsListItemList.getPropertyTenureContractsName());
        propertyTenureContractsListItemPropertyName.setText(propertyTenureContractsListItemList.getPropertyName());
        propertyTenureContractsListItemTenantName.setText(propertyTenureContractsListItemList.getTenantName());
        String date = propertyTenureContractsListItemList.getPropertyTenureContractsEndDate();
        propertyTenureContractsListItemEndDate.setText(date.length() > 10 ? date.substring(0, 10) : date);
        propertyTenureContractsListItemRentalAmount.setText(Float.toString(propertyTenureContractsListItemList.getPropertyTenureContractsRentalAmount()));
        return convertView;
    }

}
