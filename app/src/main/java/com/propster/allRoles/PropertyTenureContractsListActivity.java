package com.propster.allRoles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
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
import com.propster.content.UserProfileActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;

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

    private String propertyTenureContractsAll = null;
    private int propertyId;
    private String propertyName;
    private int[] propertyExpensesIdArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_tenure_contracts_list);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            this.propertyTenureContractsAll = null;
            this.propertyId = -1;
            this.propertyName = null;
            this.propertyExpensesIdArray = null;
        } else {
            this.propertyTenureContractsAll = extras.getString(Constants.INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_LIST, null);
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
            this.propertyExpensesIdArray = extras.getIntArray(Constants.INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_ID);
        }

        this.backgroundView = findViewById(R.id.propertyTenureContractsListBackground);
        this.loadingSpinner = findViewById(R.id.propertyTenureContractsListLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        ListView propertyTenureContractsList = findViewById(R.id.propertyTenureContractsList);
        ArrayList<PropertyTenureContractsListItem> propertyTenureContractsListItemArrayList = new ArrayList<>();
        this.propertyTenureContractsListAdapter = new PropertyTenureContractsListAdapter(this, propertyTenureContractsListItemArrayList);
        propertyTenureContractsList.setAdapter(this.propertyTenureContractsListAdapter);
        propertyTenureContractsList.setOnItemClickListener((parent, view, position, id) -> {
//            PropertyExpensesListItem propertyExpensesListItem = ((PropertyExpensesListAdapter) parent.getAdapter()).getItem(position);
//            Intent propertyTenantDetailIntent = new Intent(PropertyExpensesListActivity.this, PropertyExpensesDetailActivity.class);
//            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_PROPERTY_ID, propertyExpensesListItem.getPropertyId());
//            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_ID, propertyExpensesListItem.getPropertyExpensesId());
//            startActivityForResult(propertyTenantDetailIntent, Constants.REQUEST_CODE_LANDLORD_PROPERTY_TENANT_DETAIL);
        });

        this.propertyTenureContractsListAddContractsButton = findViewById(R.id.propertyTenureContractsListAddContractsButton);
        this.propertyTenureContractsListAddContractsButton.setOnClickListener(v -> {
//            Intent landlordAddPropertyIntent = new Intent(LandlordPropertyListActivity.this, LandlordAddPropertyActivity.class);
//            startActivityForResult(landlordAddPropertyIntent, Constants.REQUEST_CODE_LANDLORD_ADD_PROPERTY);
//            landlordManageFirstTimeLoginLabel.setVisibility(View.GONE);
        });

        if (propertyTenureContractsAll == null || !propertyTenureContractsAll.equals(Constants.INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_LIST_ALL)) {
            this.propertyTenureContractsListAddContractsButton.setVisibility(View.VISIBLE);
        } else {
            this.propertyTenureContractsListAddContractsButton.setVisibility(View.GONE);
        }

        Toolbar mainToolbar = findViewById(R.id.propertyTenureContractsListToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.mainMenuUser) {
                Intent userProfileIntent = new Intent(PropertyTenureContractsListActivity.this, UserProfileActivity.class);
                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            } else if (item.getItemId() == R.id.mainMenuNotification) {
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
//        if (requestCode == Constants.REQUEST_CODE_LANDLORD_PROPERTY_TENANT_DETAIL) {
//            this.refreshPropertyExpensesList();
//        }
    }

    private void refreshPropertyTenureContractsList() {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getPropertyTenureContractsListFailed("Please relogin.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY, null, response -> {
            try {
                if (!response.has("data")) {
                    getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONArray dataJsonArray = response.getJSONArray("data");
                JSONObject dataJsonObject;
                JSONObject dataFieldsJsonObject;
                JSONArray dataTenureContractsJsonArray;
                JSONObject dataTenureContractsJsonObject;
                JSONObject dataTenureContractsFieldsJsonObject;
                List<PropertyTenureContractsListItem> propertyTenureContractsListItemList = new ArrayList<>();
                for (int i = 0; i < dataJsonArray.length(); i++) {
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
                    dataTenureContractsJsonArray = dataFieldsJsonObject.getJSONArray("Tenure Contract");
                    for (int j = 0; j < dataTenureContractsJsonArray.length(); j++) {
                        dataTenureContractsJsonObject = dataTenureContractsJsonArray.getJSONObject(j);
                        if (!dataTenureContractsJsonObject.has("id")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataTenureContractsJsonObject.has("fields")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        dataTenureContractsFieldsJsonObject = dataTenureContractsJsonObject.getJSONObject("fields");
                        if (!dataTenureContractsFieldsJsonObject.has("Contract Name")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataTenureContractsFieldsJsonObject.has("Tenure End Date")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataTenureContractsFieldsJsonObject.has("Month Rental Currency ISO")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataTenureContractsFieldsJsonObject.has("Monthly Rental Amount")) {
                            getPropertyTenureContractsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
//                        propertyTenureContractsListItemList.add(new PropertyTenureContractsListItem(dataJsonObject.getInt("id"), dataExpensesJsonObject.getInt("id"),
//                                dataExpensesFieldsJsonObject.getString("Payment Description"), dataFieldsJsonObject.getString("Asset Nickname"),
//                                dataExpensesFieldsJsonObject.getString("Date Of Expense"),
//                                dataExpensesFieldsJsonObject.getString("Currency Iso") + dataExpensesFieldsJsonObject.getInt("Amount")));
                    }
                }
                getPropertyTenureContractsListSuccess(propertyTenureContractsListItemList);
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

    private void getPropertyTenureContractsListSuccess(List<PropertyTenureContractsListItem> propertyTenureContractsListItemList) {
        this.refreshPropertyTenureContractsAdapterList();
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
