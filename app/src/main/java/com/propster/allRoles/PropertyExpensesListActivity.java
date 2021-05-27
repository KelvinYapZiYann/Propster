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

public class PropertyExpensesListActivity extends AppCompatActivity {

    private PropertyExpensesListAdapter propertyExpensesListAdapter;

    private Button propertyExpensesListAddExpensesButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private String propertyExpensesAll = null;
    private int propertyId;
    private String propertyName;
//    private int[] propertyExpensesIdArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_expenses_list);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.propertyExpensesAll = null;
            this.propertyId = -1;
            this.propertyName = null;
//            this.propertyExpensesIdArray = null;
        } else {
            this.propertyExpensesAll = extras.getString(Constants.INTENT_EXTRA_LIST_ALL_PROPERTY_EXPENSES, null);
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
//            this.propertyExpensesIdArray = extras.getIntArray(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_LIST_PROPERTY_EXPENSES_ID);
        }

        this.backgroundView = findViewById(R.id.propertyExpensesListBackground);
        this.loadingSpinner = findViewById(R.id.propertyExpensesListLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        ListView propertyExpensesList = findViewById(R.id.propertyExpensesList);
        ArrayList<PropertyExpensesListItem> propertyExpensesListItemArrayList = new ArrayList<>();
        this.propertyExpensesListAdapter = new PropertyExpensesListAdapter(this, propertyExpensesListItemArrayList);
        propertyExpensesList.setAdapter(this.propertyExpensesListAdapter);
        propertyExpensesList.setOnItemClickListener((parent, view, position, id) -> {
            PropertyExpensesListItem propertyExpensesListItem = ((PropertyExpensesListAdapter) parent.getAdapter()).getItem(position);
            Intent propertyTenantDetailIntent = new Intent(PropertyExpensesListActivity.this, PropertyExpensesDetailActivity.class);
            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, propertyExpensesListItem.getPropertyId());
            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, propertyExpensesListItem.getPropertyName());
            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_ID, propertyExpensesListItem.getPropertyExpensesId());
            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_NAME, propertyExpensesListItem.getPropertyExpensesDescription());
            startActivityForResult(propertyTenantDetailIntent, Constants.REQUEST_CODE_LANDLORD_PROPERTY_TENANT_DETAIL);
        });

        this.propertyExpensesListAddExpensesButton = findViewById(R.id.propertyExpensesListAddExpensesButton);
        this.propertyExpensesListAddExpensesButton.setOnClickListener(v -> {
            Intent addPropertyExpensesIntent = new Intent(PropertyExpensesListActivity.this, AddPropertyExpensesActivity.class);
            addPropertyExpensesIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, this.propertyId);
            addPropertyExpensesIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, this.propertyName);
            startActivityForResult(addPropertyExpensesIntent, Constants.REQUEST_CODE_ADD_PROPERTY_EXPENSES);
        });

        if (this.isShowingPropertyExpensesOfAllProperties()) {
            this.propertyExpensesListAddExpensesButton.setVisibility(View.GONE);
        } else {
            this.propertyExpensesListAddExpensesButton.setVisibility(View.VISIBLE);
        }

        Toolbar mainToolbar = findViewById(R.id.propertyExpensesListToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            if (this.isShowingPropertyExpensesOfAllProperties()) {
                getSupportActionBar().setTitle(R.string.app_name);
            } else {
                getSupportActionBar().setTitle(this.propertyName);
            }
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.mainMenuUser) {
                Intent userProfileIntent = new Intent(PropertyExpensesListActivity.this, UserProfileActivity.class);
                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            } else if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(PropertyExpensesListActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshPropertyExpensesList();
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
        if (requestCode == Constants.REQUEST_CODE_LANDLORD_PROPERTY_TENANT_DETAIL) {
            this.refreshPropertyExpensesList();
        } else if (requestCode == Constants.REQUEST_CODE_ADD_PROPERTY_EXPENSES) {
            this.refreshPropertyExpensesList();
        }
    }

    private void refreshPropertyExpensesList() {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getPropertyExpensesListFailed("Please relogin.");
            return;
        }
        if (this.isShowingPropertyExpensesOfAllProperties()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY_EXPENSES, null, response -> {
                try {
                    if (!response.has("data")) {
                        getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    JSONArray dataJsonArray = response.getJSONArray("data");
                    JSONObject dataJsonObject;
                    JSONObject dataFieldsJsonObject;
                    List<PropertyExpensesListItem> propertyExpensesListItemList = new ArrayList<>();
                    for (int i = dataJsonArray.length() - 1; i >= 0; i--) {
                        dataJsonObject = dataJsonArray.getJSONObject(i);
                        if (!dataJsonObject.has("id")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataJsonObject.has("asset_id")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataJsonObject.has("fields")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                        if (!dataFieldsJsonObject.has("payment_description")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("date_of_expense")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("currency_iso")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("amount")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        propertyExpensesListItemList.add(new PropertyExpensesListItem(dataJsonObject.getInt("asset_id"), "", dataJsonObject.getInt("id"),
                                dataFieldsJsonObject.getString("payment_description"), dataFieldsJsonObject.getString("date_of_expense"),
                                dataFieldsJsonObject.getString("currency_iso") + dataFieldsJsonObject.getInt("amount")));
                    }
                    getPropertyExpensesListSuccess(propertyExpensesListItemList);

                } catch (JSONException e) {
                    getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                }
            }, error -> getPropertyExpensesListFailed(Constants.ERROR_COMMON)) {
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
            this.refreshPropertyExpensesAdapterList();
            if (this.propertyId == -1) {
                this.stopLoadingSpinner();
                getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                return;
            }
            if (this.propertyName == null) {
                this.stopLoadingSpinner();
                getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                return;
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY_PROPERTY_EXPENSES + "/" + this.propertyId, null, response -> {
                try {
                    if (!response.has("data")) {
                        getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    List<PropertyExpensesListItem> propertyExpensesListItemList = new ArrayList<>();
                    JSONArray dataJsonArray = response.getJSONArray("data");
                    JSONObject dataJsonObject;
                    for (int i = dataJsonArray.length() - 1; i >= 0; i--) {
                        dataJsonObject = dataJsonArray.getJSONObject(i);
                        if (!dataJsonObject.has("id")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataJsonObject.has("fields")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                        if (!dataFieldsJsonObject.has("payment_description")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("date_of_expense")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("currency_iso")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("amount")) {
                            getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        propertyExpensesListItemList.add(new PropertyExpensesListItem(this.propertyId, this.propertyName, dataJsonObject.getInt("id"),
                                dataFieldsJsonObject.getString("payment_description"), dataFieldsJsonObject.getString("date_of_expense"),
                                dataFieldsJsonObject.getString("currency_iso") + dataFieldsJsonObject.getDouble("amount")));
                    }
                    getPropertyExpensesListSuccess(propertyExpensesListItemList);
                } catch (JSONException e) {
                    getPropertyExpensesListFailed(Constants.ERROR_COMMON);
                }
            }, error -> getPropertyExpensesListFailed(Constants.ERROR_COMMON)) {
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

    private boolean isShowingPropertyExpensesOfAllProperties() {
        if (this.propertyExpensesAll == null) {
            return false;
        } else {
            return this.propertyExpensesAll.equals(Constants.INTENT_EXTRA_LIST_ALL_PROPERTY_EXPENSES);
        }
    }

    private void getPropertyExpensesListSuccess(List<PropertyExpensesListItem> propertyExpensesListItemList) {
        this.refreshPropertyExpensesAdapterList();
        this.stopLoadingSpinner();
        for (int i = 0; i < propertyExpensesListItemList.size(); i++) {
            this.addPropertyExpensesItemIntoList(propertyExpensesListItemList.get(i));
        }
    }

    private void getPropertyExpensesListFailed(String landlordGetPropertyListFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Property Expenses List Failed");
        loginFailedDialog.setMessage(landlordGetPropertyListFailed);
        loginFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        loginFailedDialog.create().show();
    }

    private void refreshPropertyExpensesAdapterList() {
        if (this.propertyExpensesListAdapter == null) {
            return;
        }
        this.propertyExpensesListAdapter.clear();
    }

//    private void addPropertyItemIntoList(String propertyName, int propertyId, int[] tenantIdArray, float payment, int age) {
//        if (this.propertyListAdapter == null) {
//            return;
//        }
//        this.propertyListAdapter.add(new LandlordPropertyListItem(propertyName, propertyId, tenantIdArray, payment, age));
////        this.propertyListAdapter.add(new LandlordPropertyListItem(propertyName, propertyId, tenantCount, totalTenantCount, payment, age));
//    }

    private void addPropertyExpensesItemIntoList(PropertyExpensesListItem propertyExpensesListItem) {
        if (this.propertyExpensesListAdapter == null) {
            return;
        }
        this.propertyExpensesListAdapter.add(propertyExpensesListItem);
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.propertyExpensesListAddExpensesButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.propertyExpensesListAddExpensesButton.setEnabled(true);
    }
}
