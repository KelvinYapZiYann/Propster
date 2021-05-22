package com.propster.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.propster.R;
import com.propster.allRoles.PropertyExpensesListActivity;
import com.propster.landlord.LandlordPropertyListActivity;
import com.propster.landlord.LandlordPropertyListAdapter;
import com.propster.landlord.LandlordPropertyListItem;
import com.propster.landlord.LandlordPropertyTenantListActivity;
import com.propster.tenant.TenantPropertyListActivity;
import com.propster.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContentManageFragment extends ContentFragment {

    private final ContentTabPageAdapter contentTabPageAdapter;
    private final boolean firstTime;

    public ContentManageFragment(ContentTabPageAdapter contentTabPageAdapter, boolean firstTime) {
        this.contentTabPageAdapter = contentTabPageAdapter;
        this.firstTime = firstTime;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this.contentTabPageAdapter.getRole() == Constants.ROLE_LANDLORD) {
            return inflater.inflate(R.layout.activity_landlord_content_manage, container, false);
        } else {
            return inflater.inflate(R.layout.activity_tenant_content_manage, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.contentTabPageAdapter.getRole() == Constants.ROLE_LANDLORD) {
            this.initializeLandlordManageView(view);
        } else {
            this.initializeTenantManageView(view);
        }
    }

    private void initializeLandlordManageView(View view) {
        Button landlordContentManagePropertyPropertyExpensesButton = view.findViewById(R.id.landlordContentManagePropertyPropertyExpensesButton);
        landlordContentManagePropertyPropertyExpensesButton.setOnClickListener(view12 -> {
            Intent propertyExpensesListIntent = new Intent(getContext(), PropertyExpensesListActivity.class);
            propertyExpensesListIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_LIST, Constants.INTENT_EXTRA_PROPERTY_EXPENSES_LIST_ALL);
            startActivity(propertyExpensesListIntent);
        });
        Button landlordContentManagePropertyPropertyListButton = view.findViewById(R.id.landlordContentManagePropertyPropertyListButton);
        landlordContentManagePropertyPropertyListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent landlordPropertyListIntent = new Intent(getContext(), LandlordPropertyListActivity.class);
                landlordPropertyListIntent.putExtra(Constants.INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN, firstTime);
                startActivity(landlordPropertyListIntent);
            }
        });
        Button landlordContentManagePropertyPaymentRecordsButton = view.findViewById(R.id.landlordContentManagePropertyPaymentRecordsButton);
        landlordContentManagePropertyPaymentRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Button landlordContentManagePropertyTenantsButton = view.findViewById(R.id.landlordContentManagePropertyTenantsButton);
        landlordContentManagePropertyTenantsButton.setOnClickListener(view1 -> {
            Intent landlordPropertyTenantListIntent = new Intent(getContext(), LandlordPropertyTenantListActivity.class);
            landlordPropertyTenantListIntent.putExtra(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_ALL_TENANTS, Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_ALL_TENANTS);
            startActivity(landlordPropertyTenantListIntent);
        });
        Button landlordContentManagePropertyTenureContractsButton = view.findViewById(R.id.landlordContentManagePropertyTenureContractsButton);
        landlordContentManagePropertyTenureContractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if (this.firstTime) {
            landlordContentManagePropertyPropertyListButton.performClick();
        }
    }

    private void initializeTenantManageView(View view) {
        Button tenantContentManagePropertyPropertyExpensesButton = view.findViewById(R.id.tenantContentManagePropertyPropertyExpensesButton);
        tenantContentManagePropertyPropertyExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Button tenantContentManagePropertyPropertyListButton = view.findViewById(R.id.tenantContentManagePropertyPropertyListButton);
        tenantContentManagePropertyPropertyListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tenantPropertyListIntent = new Intent(getContext(), TenantPropertyListActivity.class);
                tenantPropertyListIntent.putExtra(Constants.INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN, firstTime);
                startActivity(tenantPropertyListIntent);
            }
        });
        Button tenantContentManagePropertyPaymentRecordsButton = view.findViewById(R.id.tenantContentManagePropertyPaymentRecordsButton);
        tenantContentManagePropertyPaymentRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Button tenantContentManagePropertyTenureContractsButton = view.findViewById(R.id.tenantContentManagePropertyTenureContractsButton);
        tenantContentManagePropertyTenureContractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if (this.firstTime) {
            tenantContentManagePropertyPropertyListButton.performClick();
        }
    }




}
