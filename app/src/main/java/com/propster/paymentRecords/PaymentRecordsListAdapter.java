package com.propster.paymentRecords;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.propster.R;

import java.util.List;

public class PaymentRecordsListAdapter extends ArrayAdapter<PaymentRecordsListItem> {

    public PaymentRecordsListAdapter(Context context, List<PaymentRecordsListItem> paymentRecordsListItemList) {
        super(context, 0, paymentRecordsListItemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_payment_records_item, parent, false);
        }
        PaymentRecordsListItem paymentRecordsListItem = getItem(position);
        TextView paymentRecordsListItemDescription = convertView.findViewById(R.id.paymentRecordsListItemDescription);
        ImageView paymentRecordsListItemDirection = convertView.findViewById(R.id.paymentRecordsListItemDirection);
        TextView paymentRecordsListItemPersonName = convertView.findViewById(R.id.paymentRecordsListItemPersonName);
        TextView paymentRecordsListItemPaymentType = convertView.findViewById(R.id.paymentRecordsListItemPaymentType);
        TextView paymentRecordsListItemPaymentDate = convertView.findViewById(R.id.paymentRecordsListItemPaymentDate);
        TextView paymentRecordsListItemPaymentAmount = convertView.findViewById(R.id.paymentRecordsListItemPaymentAmount);
        paymentRecordsListItemDescription.setText(paymentRecordsListItem.getDescription());
        if (paymentRecordsListItem.getIsDirectionIn()) {
            paymentRecordsListItemPersonName.setText(paymentRecordsListItem.getSenderName());
        } else {
            paymentRecordsListItemDirection.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_right_red));
            paymentRecordsListItemPersonName.setText(paymentRecordsListItem.getRecipientName());
        }
        paymentRecordsListItemPaymentType.setText(paymentRecordsListItem.getPaymentType());
        String date = paymentRecordsListItem.getPaymentDate();
        paymentRecordsListItemPaymentDate.setText(date.length() > 10 ? date.substring(0, 10) : date);
        paymentRecordsListItemPaymentAmount.setText(paymentRecordsListItem.getAmount());
        return convertView;
    }

}
