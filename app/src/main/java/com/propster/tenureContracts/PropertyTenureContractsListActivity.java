package com.propster.tenureContracts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;
import com.propster.utils.CurrencyConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyTenureContractsListActivity extends AppCompatActivity {

    private PropertyTenureContractsListAdapter propertyTenureContractsListAdapter;

    private Button propertyTenureContractsListAddContractsButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private String tenureContractsAll = null;
    private int propertyId;
    private String propertyName;
    private int tenantId;
    private String tenantName;

    private int paginationLastItem = 0;
    private boolean paginationHasNextPage = false;
    private int paginationId = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_tenure_contracts_list);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            this.tenureContractsAll = null;
            this.propertyId = -1;
            this.propertyName = null;
            this.tenantId = -1;
            this.tenantName = null;
        } else {
            this.tenureContractsAll = extras.getString(Constants.INTENT_EXTRA_LIST_TENURE_CONTRACTS, null);
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
            this.tenantId = extras.getInt(Constants.INTENT_EXTRA_TENANT_ID, -1);
            this.tenantName = extras.getString(Constants.INTENT_EXTRA_TENANT_NAME, null);
        }

        this.backgroundView = findViewById(R.id.propertyTenureContractsListBackground);
        this.loadingSpinner = findViewById(R.id.propertyTenureContractsListLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.paginationLastItem = 0;
        this.paginationHasNextPage = false;
        this.paginationId = 1;

        ListView propertyTenureContractsList = findViewById(R.id.propertyTenureContractsList);
        ArrayList<PropertyTenureContractsListItem> propertyTenureContractsListItemArrayList = new ArrayList<>();
        this.propertyTenureContractsListAdapter = new PropertyTenureContractsListAdapter(this, propertyTenureContractsListItemArrayList);
        propertyTenureContractsList.setAdapter(this.propertyTenureContractsListAdapter);
        propertyTenureContractsList.setOnItemClickListener((parent, view, position, id) -> {
            PropertyTenureContractsListItem propertyTenureContractsListItem = ((PropertyTenureContractsListAdapter) parent.getAdapter()).getItem(position);
            Intent propertyTenantDetailIntent = new Intent(PropertyTenureContractsListActivity.this, PropertyTenureContractsDetailActivity.class);
            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, propertyTenureContractsListItem.getPropertyId());
            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, propertyTenureContractsListItem.getPropertyName());
            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_TENANT_ID, propertyTenureContractsListItem.getTenantId());
            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_TENANT_NAME, propertyTenureContractsListItem.getTenantName());
            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_TENURE_CONTRACTS_ID, propertyTenureContractsListItem.getPropertyTenureContractsId());
            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_TENURE_CONTRACTS_NAME, propertyTenureContractsListItem.getPropertyTenureContractsName());
            startActivityForResult(propertyTenantDetailIntent, Constants.REQUEST_CODE_TENURE_CONTRACTS_DETAIL);
        });

        propertyTenureContractsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if(lastItem == totalItemCount) {
                    if(paginationLastItem != lastItem) {
                        paginationLastItem = lastItem;
                        if (paginationHasNextPage) {
                            getTenureContracts();
                        }
                    }
                }
            }
        });

        this.propertyTenureContractsListAddContractsButton = findViewById(R.id.propertyTenureContractsListAddContractsButton);
        this.propertyTenureContractsListAddContractsButton.setOnClickListener(v -> {
            Intent addPropertyTenureContractsIntent = new Intent(PropertyTenureContractsListActivity.this, AddPropertyTenureContractsActivity.class);
            addPropertyTenureContractsIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, this.propertyId);
            addPropertyTenureContractsIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, this.propertyName);
            addPropertyTenureContractsIntent.putExtra(Constants.INTENT_EXTRA_TENANT_ID, this.tenantId);
            addPropertyTenureContractsIntent.putExtra(Constants.INTENT_EXTRA_TENANT_NAME, this.tenantName);
            startActivityForResult(addPropertyTenureContractsIntent, Constants.REQUEST_CODE_ADD_TENURE_CONTRACTS);
        });

        if (this.isShowingAllTenureContactsOfAllPropertiesAndTenants()) {
            this.propertyTenureContractsListAddContractsButton.setVisibility(View.GONE);
        } else {
            this.propertyTenureContractsListAddContractsButton.setVisibility(View.VISIBLE);
        }

        Toolbar mainToolbar = findViewById(R.id.propertyTenureContractsListToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            if (this.isShowingAllTenureContactsOfAllPropertiesAndTenants()) {
                getSupportActionBar().setTitle(R.string.app_name);
            } else {
                getSupportActionBar().setTitle(this.tenantName);
            }
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.mainMenuUser) {
//                Intent userProfileIntent = new Intent(PropertyTenureContractsListActivity.this, UserProfileActivity.class);
//                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//            } else
            if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(PropertyTenureContractsListActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshPropertyTenureContractsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_TENURE_CONTRACTS_DETAIL) {
            this.refreshPropertyTenureContractsList();
        } else if (requestCode == Constants.REQUEST_CODE_ADD_TENURE_CONTRACTS) {
            this.refreshPropertyTenureContractsList();
        }
    }

    private void refreshPropertyTenureContractsList() {
        this.startLoadingSpinner();
        this.paginationId = 1;
        this.refreshPropertyTenureContractsAdapterList();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getPropertyTenureContractsListFailed("Please relogin.");
            return;
        }
        this.paginationId = 1;
        this.getTenureContracts();
    }

    private void getTenureContracts() {
        this.startLoadingSpinner();
        this.paginationHasNextPage = false;
        if (this.isShowingAllTenureContactsOfAllPropertiesAndTenants()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_TENURE_CONTRACTS + "?" + Constants.PAGE + "=" + this.paginationId, null, response -> {
                try {
                    if (!response.has("data")) {
                        getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    JSONArray dataJsonArray = response.getJSONArray("data");
                    JSONObject dataJsonObject;
                    JSONObject dataFieldsJsonObject;
                    JSONObject dataFieldsAssetJsonObject;
                    JSONObject dataFieldsTenantJsonObject;
                    List<PropertyTenureContractsListItem> propertyTenureContractsListItemList = new ArrayList<>();
                    for (int i = dataJsonArray.length() - 1; i >= 0; i--) {
                        dataJsonObject = dataJsonArray.getJSONObject(i);
                        if (!dataJsonObject.has("id")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
//                        if (!dataJsonObject.has("asset_id")) {
//                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
//                            return;
//                        }
//                        if (!dataJsonObject.has("tenant_id")) {
//                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
//                            return;
//                        }
//                        if (dataJsonObject.getInt("asset_id") != this.propertyId) {
//                            continue;
//                        }
//                        if (dataJsonObject.getInt("tenant_id") != this.tenantId) {
//                            continue;
//                        }
                        if (!dataJsonObject.has("fields")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                        if (!dataFieldsJsonObject.has("asset")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("tenant")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("contract_name")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("tenure_end_date")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("monthly_rental_currency_iso")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("monthly_rental_amount")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        dataFieldsAssetJsonObject = dataFieldsJsonObject.getJSONObject("asset");
                        if (!dataFieldsAssetJsonObject.has("asset_nickname")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsAssetJsonObject.has("id")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (dataFieldsAssetJsonObject.isNull("asset_nickname")) {
                            continue;
                        }
                        if (dataFieldsAssetJsonObject.isNull("id")) {
                            continue;
                        }
                        dataFieldsTenantJsonObject = dataFieldsJsonObject.getJSONObject("tenant");
                        if (!dataFieldsTenantJsonObject.has("first_name")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsTenantJsonObject.has("last_name")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsTenantJsonObject.has("id")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (dataFieldsTenantJsonObject.isNull("first_name")) {
                            continue;
                        }
                        if (dataFieldsTenantJsonObject.isNull("last_name")) {
                            continue;
                        }
                        if (dataFieldsTenantJsonObject.isNull("id")) {
                            continue;
                        }
                        System.out.println("dataFieldsAssetJsonObject.getString(\"asset_nickname\") = " + dataFieldsAssetJsonObject.getString("asset_nickname"));
                        System.out.println("dataFieldsTenantJsonObject.getString(\"first_name\") = " + dataFieldsTenantJsonObject.getString("first_name"));
                        propertyTenureContractsListItemList.add(new PropertyTenureContractsListItem(
                                dataFieldsAssetJsonObject.getInt("id"),
                                dataFieldsAssetJsonObject.getString("asset_nickname"),
                                dataFieldsTenantJsonObject.getInt("id"),
                                dataFieldsTenantJsonObject.getString("first_name"),
                                dataJsonObject.getInt("id"),
                                dataFieldsJsonObject.getString("contract_name"),
                                dataFieldsJsonObject.getString("tenure_end_date"),
                                CurrencyConverter.convertCurrency(dataFieldsJsonObject.getString("monthly_rental_currency_iso")) + dataFieldsJsonObject.getInt("monthly_rental_amount")));
                    }
//                    updateTenantNameToTenureContractsListItem(propertyTenureContractsListItemList);
                    getPropertyTenureContractsListSuccess(propertyTenureContractsListItemList);

                    if (!response.has("meta")) {
                        return;
                    }
                    JSONObject metaJsonObject = response.getJSONObject("meta");
                    if (!metaJsonObject.has("current_page")) {
                        return;
                    }
                    if (!metaJsonObject.has("last_page")) {
                        return;
                    }
                    if (metaJsonObject.getInt("last_page") > metaJsonObject.getInt("current_page")) {
                        this.paginationHasNextPage = true;
                        this.paginationId += 1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                }
            }, error -> getPropertyTenureContractsListFailed(Constants.ERROR_COMMON)) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if (SplashActivity.SESSION_ID.isEmpty()) {
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        SplashActivity.SESSION_ID = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, "");
                    }
                    Map<String, String> headerParams = new HashMap<>();
                    headerParams.put("Accept", "application/json");
                    headerParams.put("Content-Type", "application/json");
                    headerParams.put("X-Requested-With", "XMLHttpRequest");
                    headerParams.put("Authorization", SplashActivity.SESSION_ID);
                    return headerParams;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.requestQueue.add(jsonObjectRequest);
        } else {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_TENANT + "/" + this.tenantId + "/" + Constants.TENURE_CONTRACTS + "?" + Constants.PAGE + "=" + this.paginationId, null, response -> {
                try {
                    if (!response.has("data")) {
                        getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    JSONArray dataJsonArray = response.getJSONArray("data");
                    JSONObject dataJsonObject;
                    JSONObject dataFieldsJsonObject;
                    List<PropertyTenureContractsListItem> propertyTenureContractsListItemList = new ArrayList<>();
                    for (int i = dataJsonArray.length() - 1; i >= 0; i--) {
                        dataJsonObject = dataJsonArray.getJSONObject(i);
                        if (!dataJsonObject.has("id")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataJsonObject.has("fields")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
//                        if (!dataFieldsJsonObject.has("asset_id")) {
//                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
//                            return;
//                        }
//                        if (!dataFieldsJsonObject.has("tenant_id")) {
//                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
//                            return;
//                        }
//                        if (dataFieldsJsonObject.getInt("asset_id") != this.propertyId) {
//                            continue;
//                        }
//                        if (dataFieldsJsonObject.getInt("tenant_id") != this.tenantId) {
//                            continue;
//                        }
                        if (!dataFieldsJsonObject.has("contract_name")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("tenure_end_date")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("monthly_rental_currency_iso")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("monthly_rental_amount")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        propertyTenureContractsListItemList.add(new PropertyTenureContractsListItem(this.propertyId, this.propertyName, this.tenantId, this.tenantName,
                                dataJsonObject.getInt("id"), dataFieldsJsonObject.getString("contract_name"), dataFieldsJsonObject.getString("tenure_end_date"),
                                CurrencyConverter.convertCurrency(dataFieldsJsonObject.getString("monthly_rental_currency_iso")) + dataFieldsJsonObject.getInt("monthly_rental_amount")));
                    }
                    getPropertyTenureContractsListSuccess(propertyTenureContractsListItemList);
                    if (!response.has("meta")) {
                        return;
                    }
                    JSONObject metaJsonObject = response.getJSONObject("meta");
                    if (!metaJsonObject.has("to")) {
                        return;
                    }
                    if (!metaJsonObject.has("total")) {
                        return;
                    }
                    if (metaJsonObject.getInt("total") > metaJsonObject.getInt("to")) {
                        this.paginationHasNextPage = true;
                        this.paginationId += 1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                }
            }, error -> getPropertyTenureContractsListFailed(Constants.ERROR_COMMON)) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if (SplashActivity.SESSION_ID.isEmpty()) {
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        SplashActivity.SESSION_ID = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, "");
                    }
                    Map<String, String> headerParams = new HashMap<>();
                    headerParams.put("Accept", "application/json");
                    headerParams.put("Content-Type", "application/json");
                    headerParams.put("X-Requested-With", "XMLHttpRequest");
                    headerParams.put("Authorization", SplashActivity.SESSION_ID);
                    return headerParams;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.requestQueue.add(jsonObjectRequest);
        }
    }

//    private void updatePropertyNameToTenureContractsListItem(List<PropertyTenureContractsListItem> propertyTenureContractsListItemList) {
//        List<Integer> propertyIdList = new ArrayList<>();
//        boolean doesPropertyIdExist;
//        for (PropertyTenureContractsListItem propertyTenureContractsListItem : propertyTenureContractsListItemList) {
//            doesPropertyIdExist = false;
//            for (Integer propertyId : propertyIdList) {
//                if (propertyId.equals(propertyTenureContractsListItem.getPropertyId())) {
//                    doesPropertyIdExist = true;
//                    break;
//                }
//            }
//            if (!doesPropertyIdExist) {
//                propertyIdList.add(propertyTenureContractsListItem.getPropertyId());
//            }
//        }
//        List<String> propertyNameList = new ArrayList<>();
//        getPropertyNameFromPropertyId(propertyTenureContractsListItemList, propertyIdList, propertyNameList, 0);
//    }
//
//    private void getPropertyNameFromPropertyId(List<PropertyTenureContractsListItem> propertyTenureContractsListItemList, List<Integer> propertyIdList, List<String> propertyNameList, int index) {
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY + "/" + propertyIdList.get(index) + "/" + Constants.FIELD_VALUE + "?" + Constants.FIELDS + "=" + "asset_nickname", null, response -> {
//            try {
//                if (!response.has("asset_nickname")) {
//                    getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
//                }
//                propertyNameList.add(response.getString("asset_nickname"));
//            } catch (JSONException e) {
//                getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
//            }
//            if (index < propertyIdList.size() - 1) {
//                getPropertyNameFromPropertyId(propertyTenureContractsListItemList, propertyIdList, propertyNameList, index+1);
//            } else {
//                if (propertyIdList.size() != propertyNameList.size()) {
//                    getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
//                } else {
//                    for (int i = 0; i < propertyTenureContractsListItemList.size(); i++) {
//                        for (int j = 0; j < propertyNameList.size(); j++) {
//                            if (propertyTenureContractsListItemList.get(i).getPropertyId() == propertyIdList.get(j)) {
//                                propertyTenureContractsListItemList.get(i).setPropertyName(propertyNameList.get(j));
//                            }
//                        }
//                    }
//                    updateTenantNameToTenureContractsListItem(propertyTenureContractsListItemList);
//                }
//            }
//        }, error -> {
//            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                if (SplashActivity.SESSION_ID.isEmpty()) {
//                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//                    SplashActivity.SESSION_ID = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, "");
//                }
//                Map<String, String> headerParams = new HashMap<>();
//                headerParams.put("Accept", "application/json");
//                headerParams.put("Content-Type", "application/json");
//                headerParams.put("X-Requested-With", "XMLHttpRequest");
//                headerParams.put("Authorization", SplashActivity.SESSION_ID);
//                return headerParams;
//            }
//        };
//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        this.requestQueue.add(jsonObjectRequest);
//    }

    private void updateTenantNameToTenureContractsListItem(List<PropertyTenureContractsListItem> propertyTenureContractsListItemList) {
        List<Integer> tenantIdList = new ArrayList<>();
        boolean doesTenantIdExist;
        for (PropertyTenureContractsListItem propertyTenureContractsListItem : propertyTenureContractsListItemList) {
            doesTenantIdExist = false;
            for (Integer tenantId : tenantIdList) {
                if (tenantId.equals(propertyTenureContractsListItem.getTenantId())) {
                    doesTenantIdExist = true;
                    break;
                }
            }
            if (!doesTenantIdExist) {
                tenantIdList.add(propertyTenureContractsListItem.getTenantId());
            }
        }
        List<String> tenantNameList = new ArrayList<>();
        getTenantNameFromPropertyId(propertyTenureContractsListItemList, tenantIdList, tenantNameList, 0);
    }

    private void getTenantNameFromPropertyId(List<PropertyTenureContractsListItem> propertyTenureContractsListItemList, List<Integer> tenantIdList, List<String> tenantNameList, int index) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_TENANT + "/" + tenantIdList.get(index) + "/" + Constants.FIELD_VALUE + "?" + Constants.FIELDS + "=" + "first_name,last_name", null, response -> {
            try {
                if (!response.has("first_name")) {
                    getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                }
                if (!response.has("last_name")) {
                    getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                }
                tenantNameList.add(response.getString("first_name") + " " + response.getString("last_name"));
            } catch (JSONException e) {
                getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
            }
            if (index < tenantIdList.size() - 1) {
                getTenantNameFromPropertyId(propertyTenureContractsListItemList, tenantIdList, tenantNameList, index+1);
            } else {
                if (tenantIdList.size() != tenantNameList.size()) {
                    getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                } else {
                    for (int i = 0; i < propertyTenureContractsListItemList.size(); i++) {
                        for (int j = 0; j < tenantNameList.size(); j++) {
                            if (propertyTenureContractsListItemList.get(i).getTenantId() == tenantIdList.get(j)) {
                                propertyTenureContractsListItemList.get(i).setTenantName(tenantNameList.get(j));
                            }
                        }
                    }
                    getPropertyTenureContractsListSuccess(propertyTenureContractsListItemList);
                }
            }
        }, error -> {
            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (SplashActivity.SESSION_ID.isEmpty()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    SplashActivity.SESSION_ID = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, "");
                }
                Map<String, String> headerParams = new HashMap<>();
                headerParams.put("Accept", "application/json");
                headerParams.put("Content-Type", "application/json");
                headerParams.put("X-Requested-With", "XMLHttpRequest");
                headerParams.put("Authorization", SplashActivity.SESSION_ID);
                return headerParams;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(jsonObjectRequest);
    }

    private boolean isShowingAllTenureContactsOfAllPropertiesAndTenants() {
        if (this.tenureContractsAll == null) {
            return false;
        } else {
            return this.tenureContractsAll.equals(Constants.INTENT_EXTRA_LIST_ALL);
        }
    }

    private void getPropertyTenureContractsListSuccess(List<PropertyTenureContractsListItem> propertyTenureContractsListItemList) {
        this.stopLoadingSpinner();
        for (int i = 0; i < propertyTenureContractsListItemList.size(); i++) {
            this.addPropertyTenureContractsItemIntoList(propertyTenureContractsListItemList.get(i));
        }
    }

    private void getPropertyTenureContractsListFailed(String propertyTenureContractsListFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Property Tenure Contracts List Failed");
        loginFailedDialog.setMessage(propertyTenureContractsListFailed);
        loginFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        loginFailedDialog.create().show();
    }

    private void refreshPropertyTenureContractsAdapterList() {
        if (this.propertyTenureContractsListAdapter == null) {
            return;
        }
        this.propertyTenureContractsListAdapter.clear();
    }

    private void addPropertyTenureContractsItemIntoList(PropertyTenureContractsListItem propertyTenureContractsListItem) {
        if (this.propertyTenureContractsListAdapter == null) {
            return;
        }
        this.propertyTenureContractsListAdapter.add(propertyTenureContractsListItem);
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.propertyTenureContractsListAddContractsButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.propertyTenureContractsListAddContractsButton.setEnabled(true);
    }

}
