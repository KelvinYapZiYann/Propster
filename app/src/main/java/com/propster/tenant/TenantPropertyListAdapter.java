package com.propster.tenant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.propster.R;

import java.util.List;

public class TenantPropertyListAdapter extends ArrayAdapter<TenantPropertyListItem> {

    public TenantPropertyListAdapter(Context context, List<TenantPropertyListItem> propertyListItemList) {
        super(context, 0, propertyListItemList);
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_tenant_property_item, parent, false);
        }
        TenantPropertyListItem propertyListItem = getItem(position);
        TextView tenantManagePropertyListItemName = convertView.findViewById(R.id.tenantManagePropertyListItemName);
        TextView tenantManagePropertyListItemPayment = convertView.findViewById(R.id.tenantManagePropertyListItemPayment);
        TextView tenantManagePropertyListItemAge = convertView.findViewById(R.id.tenantManagePropertyListItemAge);
        tenantManagePropertyListItemName.setText(propertyListItem.getPropertyName());
        tenantManagePropertyListItemPayment.setText(Float.toString(propertyListItem.getPayment()));
        tenantManagePropertyListItemAge.setText(Integer.toString(propertyListItem.getAge()));
        return convertView;
    }
}
