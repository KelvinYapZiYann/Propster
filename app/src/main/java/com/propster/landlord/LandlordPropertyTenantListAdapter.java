package com.propster.landlord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.propster.R;

import java.util.List;

public class LandlordPropertyTenantListAdapter extends ArrayAdapter<LandlordPropertyTenantListItem> {

    public LandlordPropertyTenantListAdapter(@NonNull Context context, List<LandlordPropertyTenantListItem> propertyTenantListItemList) {
        super(context, 0, propertyTenantListItemList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_landlord_property_tenant_item, parent, false);
        }
        LandlordPropertyTenantListItem propertyTenantListItem = getItem(position);
        TextView landlordManagePropertyTenantListItemName = convertView.findViewById(R.id.landlordManagePropertyTenantListItemName);
        TextView landlordManagePropertyTenantListItemTenureEndDate = convertView.findViewById(R.id.landlordManagePropertyTenantListItemTenureEndDate);
        TextView landlordManagePropertyTenantListItemPayment = convertView.findViewById(R.id.landlordManagePropertyTenantListItemPayment);
        TextView landlordManagePropertyTenantListItemAge = convertView.findViewById(R.id.landlordManagePropertyTenantListItemAge);
        String name = propertyTenantListItem.getTenantFirstName() + " " + propertyTenantListItem.getTenantLastName();
        landlordManagePropertyTenantListItemName.setText(name);
        landlordManagePropertyTenantListItemTenureEndDate.setText(propertyTenantListItem.getTenureEndDate());
        landlordManagePropertyTenantListItemPayment.setText(Float.toString(propertyTenantListItem.getPayment()));
        landlordManagePropertyTenantListItemAge.setText(Integer.toString(propertyTenantListItem.getAge()));
        return convertView;
    }
}
