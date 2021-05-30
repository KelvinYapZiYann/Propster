package com.propster.landlord;

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
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandlordPropertyListActivity extends AppCompatActivity {

    private TextView landlordManageFirstTimeLoginLabel;
    private LandlordPropertyListAdapter propertyListAdapter;

    private Button landlordManageAddPropertyButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_list);

        boolean firstTime;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            firstTime = false;
        } else {
            firstTime = extras.getBoolean(Constants.INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN);
        }

        this.backgroundView = findViewById(R.id.landlordPropertyListBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyListLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.landlordManageFirstTimeLoginLabel = findViewById(R.id.landlordManageFirstTimeLoginLabel);
        ListView landlordManagePropertyList = findViewById(R.id.landlordManagePropertyList);
        ArrayList<LandlordPropertyListItem> propertyListItemArrayList = new ArrayList<>();
        this.propertyListAdapter = new LandlordPropertyListAdapter(this, propertyListItemArrayList);
        landlordManagePropertyList.setAdapter(this.propertyListAdapter);
        landlordManagePropertyList.setOnItemClickListener((parent, view, position, id) -> {
            LandlordPropertyListItem landlordPropertyListItem = ((LandlordPropertyListAdapter) parent.getAdapter()).getItem(position);
            Intent propertyTenantListIntent = new Intent(LandlordPropertyListActivity.this, LandlordPropertyTenantListActivity.class);
            propertyTenantListIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, landlordPropertyListItem.getPropertyId());
            propertyTenantListIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, landlordPropertyListItem.getPropertyName());
            propertyTenantListIntent.putExtra(Constants.INTENT_EXTRA_TENANT_ID, landlordPropertyListItem.getTenantIdArray());
//            propertyTenantListIntent.putExtra(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_EXPENSES_ID, landlordPropertyListItem.getPropertyExpensesIdArray());
            startActivityForResult(propertyTenantListIntent, Constants.REQUEST_CODE_LANDLORD_PROPERTY_TENANT_LIST);
        });

        this.landlordManageAddPropertyButton = findViewById(R.id.landlordManageAddPropertyButton);
        this.landlordManageAddPropertyButton.setOnClickListener(v -> {
            Intent landlordAddPropertyIntent = new Intent(LandlordPropertyListActivity.this, LandlordAddPropertyActivity.class);
            startActivityForResult(landlordAddPropertyIntent, Constants.REQUEST_CODE_LANDLORD_ADD_PROPERTY);
            landlordManageFirstTimeLoginLabel.setVisibility(View.GONE);
        });

        this.refreshPropertyListView();
        if (this.propertyListAdapter.getCount() <= 0) {
            if (firstTime) {
                this.landlordManageFirstTimeLoginLabel.setVisibility(View.VISIBLE);
            } else {
                this.landlordManageFirstTimeLoginLabel.setVisibility(View.GONE);
            }
        }
        Toolbar mainToolbar = findViewById(R.id.landlordManagePropertyListToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.mainMenuUser) {
//                Intent userProfileIntent = new Intent(LandlordPropertyListActivity.this, UserProfileActivity.class);
//                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//            } else
            if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(LandlordPropertyListActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());
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
        if (requestCode == Constants.REQUEST_CODE_LANDLORD_ADD_PROPERTY) {
//            if (resultCode == Activity.RESULT_OK) {
                this.landlordManageFirstTimeLoginLabel.setVisibility(View.GONE);
                this.refreshPropertyListView();
//            }
        } else if (requestCode == Constants.REQUEST_CODE_LANDLORD_PROPERTY_TENANT_LIST) {
            this.refreshPropertyListView();
        }
    }

    private void refreshPropertyListView() {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.landlordManageGetPropertyListFailed("Please relogin.");
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY, null, response -> {
            try {
                if (!response.has("data")) {
                    landlordManageGetPropertyListFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONArray dataJsonArray = response.getJSONArray("data");
                JSONObject dataJsonObject;
                JSONObject dataFieldsJsonObject;
                int[] tenantIdArray;
//                int[] expensesIdArray;
                JSONArray dataTenantsJsonArray;
                JSONObject dataTenantJsonObject;
//                JSONArray dataExpensesJsonArray;
//                JSONObject dataExpensesJsonObject;
                List<LandlordPropertyListItem> propertyListItems = new ArrayList<>();
                for (int i = 0; i < dataJsonArray.length(); i++) {
                    dataJsonObject = dataJsonArray.getJSONObject(i);
                    if (!dataJsonObject.has("id")) {
                        landlordManageGetPropertyListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    if (!dataJsonObject.has("fields")) {
                        landlordManageGetPropertyListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                    if (!dataFieldsJsonObject.has("landlord_id")) {
                        landlordManageGetPropertyListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    if (!dataFieldsJsonObject.has("asset_nickname")) {
                        landlordManageGetPropertyListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    if (!dataJsonObject.has("tenants")) {
                        landlordManageGetPropertyListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    dataTenantsJsonArray = dataJsonObject.getJSONArray("tenants");
                    tenantIdArray = new int[dataTenantsJsonArray.length()];
                    for (int j = 0; j < dataTenantsJsonArray.length(); j++) {
                        dataTenantJsonObject = dataTenantsJsonArray.getJSONObject(j);
                        tenantIdArray[j] = dataTenantJsonObject.getInt("id");
                    }
//                    if (!dataFieldsJsonObject.has("asset_expenses")) {
//                        landlordManageGetPropertyListFailed(Constants.ERROR_COMMON);
//                    }
//                    dataExpensesJsonArray = dataFieldsJsonObject.getJSONArray("asset_expenses");
//                    expensesIdArray = new int[dataExpensesJsonArray.length()];
//                    for (int j = 0; j < dataExpensesJsonArray.length(); j++) {
//                        dataExpensesJsonObject = dataExpensesJsonArray.getJSONObject(j);
//                        expensesIdArray[j] = dataExpensesJsonObject.getInt("id");
//                    }
                    propertyListItems.add(new LandlordPropertyListItem(dataFieldsJsonObject.getString("asset_nickname"), dataJsonObject.getInt("id"), tenantIdArray,0, 0));
                }
                landlordManageGetPropertyListSuccess(propertyListItems);
            } catch (JSONException e) {
                landlordManageGetPropertyListFailed(Constants.ERROR_COMMON);
            }
        }, error -> landlordManageGetPropertyListFailed(Constants.ERROR_COMMON)) {
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

    private void landlordManageGetPropertyListSuccess(List<LandlordPropertyListItem> propertyListItemList) {
        this.refreshPropertyList();
        this.stopLoadingSpinner();
        for (int i = 0; i < propertyListItemList.size(); i++) {
            this.addPropertyItemIntoList(propertyListItemList.get(i));
        }
    }

    private void landlordManageGetPropertyListFailed(String landlordGetPropertyListFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Property List Failed");
        loginFailedDialog.setMessage(landlordGetPropertyListFailed);
        loginFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        loginFailedDialog.create().show();
    }

    private void refreshPropertyList() {
        if (this.propertyListAdapter == null) {
            return;
        }
        this.propertyListAdapter.clear();
    }

//    private void addPropertyItemIntoList(String propertyName, int propertyId, int[] tenantIdArray, float payment, int age) {
//        if (this.propertyListAdapter == null) {
//            return;
//        }
//        this.propertyListAdapter.add(new LandlordPropertyListItem(propertyName, propertyId, tenantIdArray, payment, age));
////        this.propertyListAdapter.add(new LandlordPropertyListItem(propertyName, propertyId, tenantCount, totalTenantCount, payment, age));
//    }

    private void addPropertyItemIntoList(LandlordPropertyListItem propertyListItem) {
        if (this.propertyListAdapter == null) {
            return;
        }
        this.propertyListAdapter.add(propertyListItem);
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.landlordManageAddPropertyButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordManageAddPropertyButton.setEnabled(true);
    }

}
