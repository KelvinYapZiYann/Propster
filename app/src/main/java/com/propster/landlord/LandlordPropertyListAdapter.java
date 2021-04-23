package com.propster.landlord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.propster.R;

import java.util.List;

public class LandlordPropertyListAdapter extends ArrayAdapter<LandlordPropertyListItem> {

    private final List<LandlordPropertyListItem> propertyListItemList;

    public LandlordPropertyListAdapter(Context context, List<LandlordPropertyListItem> propertyListItemList) {
        super(context, 0, propertyListItemList);
        this.propertyListItemList = propertyListItemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_landlord_property_item, parent, false);
        }
        LandlordPropertyListItem propertyListItem = this.propertyListItemList.get(position);
        TextView landlordManagePropertyListItemName = convertView.findViewById(R.id.landlordManagePropertyListItemName);
        TextView landlordManagePropertyListItemTenantCount = convertView.findViewById(R.id.landlordManagePropertyListItemTenantCount);
        TextView landlordManagePropertyListItemPayment = convertView.findViewById(R.id.landlordManagePropertyListItemPayment);
        landlordManagePropertyListItemName.setText(propertyListItem.getPropertyName());
        landlordManagePropertyListItemTenantCount.setText(Integer.toString(propertyListItem.getTenantCount()));
        landlordManagePropertyListItemPayment.setText(Float.toString(propertyListItem.getPayment()));
        return convertView;
    }

    @Override
    public int getCount() {
        return this.propertyListItemList.size();
    }

    @Override
    public LandlordPropertyListItem getItem(int position) {
        return this.propertyListItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
