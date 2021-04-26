package com.propster.landlord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.propster.R;

import java.util.List;

public class LandlordPropertyListAdapter extends ArrayAdapter<LandlordPropertyListItem> {

    public LandlordPropertyListAdapter(Context context, List<LandlordPropertyListItem> propertyListItemList) {
        super(context, 0, propertyListItemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_landlord_property_item, parent, false);
        }
        LandlordPropertyListItem propertyListItem = getItem(position);
        TextView landlordManagePropertyListItemName = convertView.findViewById(R.id.landlordManagePropertyListItemName);
        RatingBar landlordManagePropertyListItemTenantCount = convertView.findViewById(R.id.landlordManagePropertyListItemTenantCount);
        TextView landlordManagePropertyListItemPayment = convertView.findViewById(R.id.landlordManagePropertyListItemPayment);
        TextView landlordManagePropertyListItemAge = convertView.findViewById(R.id.landlordManagePropertyListItemAge);
        landlordManagePropertyListItemName.setText(propertyListItem.getPropertyName());
        landlordManagePropertyListItemTenantCount.setNumStars(propertyListItem.getTotalTenantCount());
        landlordManagePropertyListItemTenantCount.setRating(propertyListItem.getTenantCount());
        landlordManagePropertyListItemPayment.setText(Float.toString(propertyListItem.getPayment()));
        landlordManagePropertyListItemAge.setText(Integer.toString(propertyListItem.getAge()));
        return convertView;
    }

}
