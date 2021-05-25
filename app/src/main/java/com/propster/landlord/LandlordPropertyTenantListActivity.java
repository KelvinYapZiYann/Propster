package com.propster.landlord;

import android.app.Activity;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.nambimobile.widgets.efab.FabOption;
import com.propster.R;
import com.propster.allRoles.PropertyExpensesListActivity;
import com.propster.allRoles.PropertyTenureContractsListActivity;
import com.propster.content.NotificationActivity;
import com.propster.content.UserProfileActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LandlordPropertyTenantListActivity extends AppCompatActivity {

    private LandlordPropertyTenantListAdapter propertyTenantListAdapter;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private Button landlordManagePropertyAddTenantButton;
    private FabOption landlordManagePropertyDetailButton;
    private FabOption landlordManagePropertyExpensesButton;
    private FabOption landlordManagePropertyPaymentRecordsButton;
    private FabOption landlordManagePropertyTenureContractButton;
    private FabOption landlordManagePropertyRemovePropertyButton;

    private IntentIntegrator qrScanIntentIntegrator;

    private RequestQueue requestQueue;

    private String tenantListAllTenants;
    private int propertyId;
    private String propertyName;
    private int[] tenantIdArray;
//    private int[] propertyExpensesIdArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_tenant_list);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.tenantIdArray = null;
            this.propertyId = -1;
            this.propertyName = null;
            this.tenantListAllTenants = null;
//            this.propertyExpensesIdArray = null;
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
            this.tenantIdArray = extras.getIntArray(Constants.INTENT_EXTRA_TENANT_ID);
            this.tenantListAllTenants = extras.getString(Constants.INTENT_EXTRA_LIST_ALL_TENANTS, null);
//            this.propertyExpensesIdArray = extras.getIntArray(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_EXPENSES_ID);
        }

        this.backgroundView = findViewById(R.id.landlordPropertyTenantListBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyTenantListLoadingSpinner);

        this.qrScanIntentIntegrator = new IntentIntegrator(this);
        this.qrScanIntentIntegrator.setRequestCode(Constants.REQUEST_CODE_LANDLORD_PROPERTY_ADD_TENANT);
        this.qrScanIntentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        this.qrScanIntentIntegrator.setPrompt("Scan Tenant's QR Code");
        this.qrScanIntentIntegrator.setOrientationLocked(false);

        this.requestQueue = Volley.newRequestQueue(this);

        ArrayList<LandlordPropertyTenantListItem> tenantListItemList = new ArrayList<>();
        this.propertyTenantListAdapter = new LandlordPropertyTenantListAdapter(this, tenantListItemList);
        ListView propertyTenantListView = findViewById(R.id.landlordPropertyTenantList);
        propertyTenantListView.setAdapter(this.propertyTenantListAdapter);
        propertyTenantListView.setOnItemClickListener((parent, view, position, id) -> {
            LandlordPropertyTenantListItem landlordPropertyTenantListItem = ((LandlordPropertyTenantListAdapter) parent.getAdapter()).getItem(position);
            Intent propertyTenantListDetail = new Intent(LandlordPropertyTenantListActivity.this, LandlordPropertyTenantDetailActivity.class);
            propertyTenantListDetail.putExtra(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_TENANT_ID, landlordPropertyTenantListItem.getTenantId());
            startActivityForResult(propertyTenantListDetail, Constants.REQUEST_CODE_LANDLORD_PROPERTY_TENANT_DETAIL);
        });
        this.landlordManagePropertyAddTenantButton = findViewById(R.id.landlordManagePropertyAddTenantButton);
        this.landlordManagePropertyAddTenantButton.setOnClickListener(view -> {
            AlertDialog.Builder propertyAddTenantDialog = new AlertDialog.Builder(LandlordPropertyTenantListActivity.this);
            propertyAddTenantDialog.setTitle("Add Tenant");
            propertyAddTenantDialog.setItems(R.array.add_tenant_type, (dialog, which) -> {
                if (which == 0) {
                    qrScanIntentIntegrator.initiateScan();
                } else {
                    Intent landlordPropertyAddTenantIntent = new Intent(LandlordPropertyTenantListActivity.this, LandlordPropertyAddTenantActivity.class);
                    landlordPropertyAddTenantIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, this.propertyId);
                    startActivityForResult(landlordPropertyAddTenantIntent, Constants.REQUEST_CODE_LANDLORD_PROPERTY_ADD_TENANT);
                }
            });
            propertyAddTenantDialog.create().show();
        });
        this.landlordManagePropertyDetailButton = findViewById(R.id.landlordManagePropertyAddTenantDetailButton);
        this.landlordManagePropertyDetailButton.setOnClickListener(view -> {
            Intent propertyDetailIntent = new Intent(LandlordPropertyTenantListActivity.this, LandlordPropertyDetailActivity.class);
            propertyDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, this.propertyId);
            propertyDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, this.propertyName);
            startActivity(propertyDetailIntent);
        });
        this.landlordManagePropertyExpensesButton = findViewById(R.id.landlordManagePropertyAddTenantExpensesButton);
        this.landlordManagePropertyExpensesButton.setOnClickListener(view -> {
            Intent propertyExpensesIntent = new Intent(LandlordPropertyTenantListActivity.this, PropertyExpensesListActivity.class);
            propertyExpensesIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_LIST_PROPERTY_ID, this.propertyId);
            propertyExpensesIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, this.propertyName);
//            propertyExpensesIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_LIST_PROPERTY_EXPENSES_ID, this.propertyExpensesIdArray);
            startActivity(propertyExpensesIntent);
        });
        this.landlordManagePropertyPaymentRecordsButton = findViewById(R.id.landlordManagePropertyAddTenantPaymentRecordsButton);
        this.landlordManagePropertyPaymentRecordsButton.setOnClickListener(view -> {

        });
        this.landlordManagePropertyTenureContractButton = findViewById(R.id.landlordManagePropertyAddTenantTenureContractButton);
        this.landlordManagePropertyTenureContractButton.setOnClickListener(view -> {
            Intent propertyTenureContractsIntent = new Intent(LandlordPropertyTenantListActivity.this, PropertyTenureContractsListActivity.class);
            startActivity(propertyTenureContractsIntent);
        });
        this.landlordManagePropertyRemovePropertyButton = findViewById(R.id.landlordManagePropertyAddTenantRemovePropertyButton);
        this.landlordManagePropertyRemovePropertyButton.setOnClickListener(view -> {
            doRemoveProperty();
        });

        if (this.isShowingAllTenantListsOfAllProperties()) {
            this.landlordManagePropertyAddTenantButton.setVisibility(View.GONE);
            this.landlordManagePropertyDetailButton.setVisibility(View.GONE);
            this.landlordManagePropertyExpensesButton.setVisibility(View.GONE);
            this.landlordManagePropertyPaymentRecordsButton.setVisibility(View.GONE);
            this.landlordManagePropertyTenureContractButton.setVisibility(View.GONE);
            this.landlordManagePropertyRemovePropertyButton.setVisibility(View.GONE);
        } else {
            this.landlordManagePropertyAddTenantButton.setVisibility(View.VISIBLE);
            this.landlordManagePropertyDetailButton.setVisibility(View.VISIBLE);
            this.landlordManagePropertyExpensesButton.setVisibility(View.VISIBLE);
            this.landlordManagePropertyPaymentRecordsButton.setVisibility(View.VISIBLE);
            this.landlordManagePropertyTenureContractButton.setVisibility(View.VISIBLE);
            this.landlordManagePropertyRemovePropertyButton.setVisibility(View.VISIBLE);
        }

        Toolbar mainToolbar = findViewById(R.id.landlordManagePropertyTenantListToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            if (this.isShowingAllTenantListsOfAllProperties()) {
                getSupportActionBar().setTitle(R.string.app_name);
            } else {
                getSupportActionBar().setTitle(this.propertyName);
            }
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.mainMenuUser) {
                Intent userProfileIntent = new Intent(LandlordPropertyTenantListActivity.this, UserProfileActivity.class);
                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            } else if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(LandlordPropertyTenantListActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshTenantListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_LANDLORD_ADD_PROPERTY) {
            if (resultCode == Activity.RESULT_OK) {
                this.refreshTenantListView();
            }
        } else if (requestCode == Constants.REQUEST_CODE_LANDLORD_PROPERTY_ADD_TENANT) {
            this.refreshTenantListView();
        } else if (requestCode == Constants.REQUEST_CODE_LANDLORD_PROPERTY_TENANT_DETAIL) {
            this.refreshTenantListView();
        }
    }

    private void refreshTenantListView() {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.landlordManagePropertyGetTenantListFailed("Please relogin.");
            return;
        }
        if (this.isShowingAllTenantListsOfAllProperties()) {
            this.refreshTenantList();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_TENANT, null, response -> {
                try {
                    if (!response.has("data")) {
                        landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    JSONArray dataJsonArray = response.getJSONArray("data");
                    JSONObject dataJsonObject;
                    for (int i = 0; i < dataJsonArray.length(); i++) {
                        dataJsonObject = dataJsonArray.getJSONObject(i);
                        if (!dataJsonObject.has("id")) {
                            landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataJsonObject.has("field")) {
                            landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        JSONObject dataFieldJsonObject = dataJsonObject.getJSONObject("field");
                        if (!dataFieldJsonObject.has("First Name")) {
                            landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldJsonObject.has("Last Name")) {
                            landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        this.addTenantItemIntoList(new LandlordPropertyTenantListItem(dataJsonObject.getInt("id"), dataFieldJsonObject.getString("First Name"), dataFieldJsonObject.getString("Last Name"), "two months", 0, 30));
                    }
                    this.stopLoadingSpinner();
                } catch (JSONException e) {
                    landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                }
            }, error -> landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON)) {
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
            if (this.tenantIdArray == null || this.tenantIdArray.length == 0) {
                this.stopLoadingSpinner();
                return;
            }

            this.refreshTenantList();
            for (int value : this.tenantIdArray) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_TENANT + "/" + value, null, response -> {
                    try {
                        if (!response.has("data")) {
                            landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        JSONObject dataJsonObject = response.getJSONObject("data");
                        if (!dataJsonObject.has("id")) {
                            landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataJsonObject.has("field")) {
                            landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        JSONObject dataFieldJsonObject = dataJsonObject.getJSONObject("field");
                        if (!dataFieldJsonObject.has("first_name")) {
                            landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldJsonObject.has("last_name")) {
                            landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        this.stopLoadingSpinner();
                        this.addTenantItemIntoList(new LandlordPropertyTenantListItem(dataJsonObject.getInt("id"), dataFieldJsonObject.getString("first_name"), dataFieldJsonObject.getString("last_name"), "two months", 0, 30));
                    } catch (JSONException e) {
                        landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON);
                    }
                }, error -> landlordManagePropertyGetTenantListFailed(Constants.ERROR_COMMON)) {
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
    }

//    private void landlordManagePropertyGetTenantListSuccess(List<LandlordPropertyTenantListItem> propertyTenantListItemList) {
//        this.refreshTenantList();
//        this.stopLoadingSpinner();
//        for (int i = 0; i < propertyTenantListItemList.size(); i++) {
//            this.addTenantItemIntoList(propertyTenantListItemList.get(i));
//        }
//    }

    private boolean isShowingAllTenantListsOfAllProperties() {
        if (this.tenantListAllTenants == null) {
            return false;
        } else {
            return this.tenantListAllTenants.equals(Constants.INTENT_EXTRA_LIST_ALL_TENANTS);
        }
    }

    private void landlordManagePropertyGetTenantListFailed(String landlordPropertyGetTenantListFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Tenant List Failed");
        loginFailedDialog.setMessage(landlordPropertyGetTenantListFailed);
        loginFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        loginFailedDialog.create().show();
    }

    private void doRemoveProperty() {
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Remove Property");
        loginFailedDialog.setMessage("Are you sure to remove this property?");
        loginFailedDialog.setPositiveButton("Yes", (dialog, which) -> {
            this.startLoadingSpinner();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
            if (sessionId == null) {
                this.landlordManagePropertyGetTenantListFailed("Please relogin.");
                return;
            }
            if (this.propertyId == -1) {
                this.landlordManagePropertyRemovePropertyFailed(Constants.ERROR_COMMON);
                return;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constants.URL_LANDLORD_PROPERTY + "/" + this.propertyId,
                    response -> landlordManagePropertyRemovePropertySuccess(), error -> landlordManagePropertyRemovePropertyFailed(Constants.ERROR_COMMON)) {
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.requestQueue.add(stringRequest);
            dialog.cancel();
        });
        loginFailedDialog.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        loginFailedDialog.create().show();
    }

    private void landlordManagePropertyRemovePropertySuccess() {
        this.stopLoadingSpinner();
        finish();
    }

    private void landlordManagePropertyRemovePropertyFailed(String landlordRemovePropertyFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Remove Property Failed");
        loginFailedDialog.setMessage(landlordRemovePropertyFailed);
        loginFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        loginFailedDialog.create().show();
    }

    private void refreshTenantList() {
        if (this.propertyTenantListAdapter == null) {
            return;
        }
        this.propertyTenantListAdapter.clear();
    }

    private void addTenantItemIntoList(LandlordPropertyTenantListItem propertyTenantListItem) {
        if (this.propertyTenantListAdapter == null) {
            return;
        }
        this.propertyTenantListAdapter.add(propertyTenantListItem);
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.landlordManagePropertyAddTenantButton.setEnabled(false);
        this.landlordManagePropertyDetailButton.setEnabled(false);
        this.landlordManagePropertyExpensesButton.setEnabled(false);
        this.landlordManagePropertyPaymentRecordsButton.setEnabled(false);
        this.landlordManagePropertyTenureContractButton.setEnabled(false);
        this.landlordManagePropertyRemovePropertyButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordManagePropertyAddTenantButton.setEnabled(true);
        this.landlordManagePropertyDetailButton.setEnabled(true);
        this.landlordManagePropertyExpensesButton.setEnabled(true);
        this.landlordManagePropertyPaymentRecordsButton.setEnabled(true);
        this.landlordManagePropertyTenureContractButton.setEnabled(true);
        this.landlordManagePropertyRemovePropertyButton.setEnabled(true);
    }
}
