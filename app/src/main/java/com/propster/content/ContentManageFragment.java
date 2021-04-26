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
import com.propster.landlord.LandlordPropertyListActivity;
import com.propster.landlord.LandlordPropertyListAdapter;
import com.propster.landlord.LandlordPropertyListItem;
import com.propster.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContentManageFragment extends Fragment {

    private final ContentTabPageAdapter contentTabPageAdapter;
    private final boolean firstTime;

    // landlord
    private Button landlordContentManagePropertyPropertyExpensesButton;
    private Button landlordContentManagePropertyPropertyListButton;
    private Button landlordContentManagePropertyPaymentRecordsButton;
    private Button landlordContentManagePropertyTenantsButton;
    private Button landlordContentManagePropertyTenureContractsButton;

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

        }
    }

    private void initializeLandlordManageView(View view) {
        this.landlordContentManagePropertyPropertyExpensesButton = view.findViewById(R.id.landlordContentManagePropertyPropertyExpensesButton);
        this.landlordContentManagePropertyPropertyExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        this.landlordContentManagePropertyPropertyListButton = view.findViewById(R.id.landlordContentManagePropertyPropertyListButton);
        this.landlordContentManagePropertyPropertyListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent landlordPropertyListIntent = new Intent(getContext(), LandlordPropertyListActivity.class);
                landlordPropertyListIntent.putExtra(Constants.INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN, firstTime);
                startActivity(landlordPropertyListIntent);
            }
        });
        this.landlordContentManagePropertyPaymentRecordsButton = view.findViewById(R.id.landlordContentManagePropertyPaymentRecordsButton);
        this.landlordContentManagePropertyPaymentRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        this.landlordContentManagePropertyTenantsButton = view.findViewById(R.id.landlordContentManagePropertyTenantsButton);
        this.landlordContentManagePropertyTenantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        this.landlordContentManagePropertyTenureContractsButton = view.findViewById(R.id.landlordContentManagePropertyTenureContractsButton);
        this.landlordContentManagePropertyTenureContractsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if (this.firstTime) {
            this.landlordContentManagePropertyPropertyListButton.performClick();
        }
    }




}
