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
import android.widget.EditText;
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
import com.nambimobile.widgets.efab.FabOption;
import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.content.UserProfileActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LandlordPropertyTenantDetailActivity extends AppCompatActivity {

    private EditText landlordPropertyTenantDetailFirstName;
    private EditText landlordPropertyTenantDetailLastName;
    private EditText landlordPropertyTenantDetailGender;
    private EditText landlordPropertyTenantDetailDateOfBirth;
    private EditText landlordPropertyTenantDetailIsBusiness;
    private EditText landlordPropertyTenantDetailSalaryRange;

    private FabOption landlordPropertyTenantDetailEditButton;
    private FabOption landlordPropertyTenantDetailRemoveTenantButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int tenantId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_tenant_detail);

        this.landlordPropertyTenantDetailFirstName = findViewById(R.id.landlordPropertyTenantDetailFirstName);
        this.landlordPropertyTenantDetailLastName = findViewById(R.id.landlordPropertyTenantDetailLastName);
        this.landlordPropertyTenantDetailGender = findViewById(R.id.landlordPropertyTenantDetailGender);
        this.landlordPropertyTenantDetailDateOfBirth = findViewById(R.id.landlordPropertyTenantDetailDateOfBirth);
        this.landlordPropertyTenantDetailIsBusiness = findViewById(R.id.landlordPropertyTenantDetailIsBusiness);
        this.landlordPropertyTenantDetailSalaryRange = findViewById(R.id.landlordPropertyTenantDetailSalaryRange);

        this.backgroundView = findViewById(R.id.landlordPropertyTenantDetailBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyTenantDetailLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.landlordPropertyTenantDetailEditButton = findViewById(R.id.landlordPropertyTenantDetailEditButton);


        this.landlordPropertyTenantDetailRemoveTenantButton = findViewById(R.id.landlordPropertyTenantDetailRemoveTenantButton);
        this.landlordPropertyTenantDetailRemoveTenantButton.setOnClickListener(v -> doRemoveTenant());

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.tenantId = -1;
        } else {
            this.tenantId = extras.getInt(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_TENANT_ID, -1);
        }

        Toolbar mainToolbar = findViewById(R.id.landlordPropertyTenantDetailToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.mainMenuUser) {
                Intent userProfileIntent = new Intent(LandlordPropertyTenantDetailActivity.this, UserProfileActivity.class);
                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            } else if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(LandlordPropertyTenantDetailActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshTenantDetail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    public void refreshTenantDetail() {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getTenantDetailFailed("Please relogin.");
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY_TENANT_DETAIL + "/" + this.tenantId, null, response -> getTenantDetailSuccess(response),
                error -> getTenantDetailFailed(Constants.ERROR_COMMON)) {
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

    private void getTenantDetailSuccess(JSONObject response) {
        try {
            if (!response.has("data")) {
                getTenantDetailFailed(Constants.ERROR_COMMON);
                return;
            }
            JSONObject dataJsonObject = response.getJSONObject("data");
            if (!dataJsonObject.has("id")) {
                getTenantDetailFailed(Constants.ERROR_COMMON);
            }
            if (dataJsonObject.getInt("id") != this.tenantId) {
                getTenantDetailFailed(Constants.ERROR_USER_TENANT_DETAIL_ID_NOT_MATCHED);
            }
            JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("field");
            this.landlordPropertyTenantDetailFirstName.setText(dataFieldsJsonObject.getString("First Name"));
            this.landlordPropertyTenantDetailLastName.setText(dataFieldsJsonObject.getString("Last Name"));
            this.landlordPropertyTenantDetailGender.setText(dataFieldsJsonObject.getString("Gender"));
            this.landlordPropertyTenantDetailIsBusiness.setText(dataFieldsJsonObject.getInt("Is Business") == 1 ? "COMMERCIAL" : "RESIDENTIAL");
            String dateOfBirth = dataFieldsJsonObject.getString("Date Of Birth");
            this.landlordPropertyTenantDetailDateOfBirth.setText(dateOfBirth.length() > 10 ? dateOfBirth.substring(0, 10) : dateOfBirth);
            this.stopLoadingSpinner();
        } catch (JSONException e) {
            e.printStackTrace();
            this.getTenantDetailFailed(Constants.ERROR_USER_TENANT_DETAIL_ID_NOT_MATCHED);
        }
    }

    private void getTenantDetailFailed(String getTenantDetailFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("Tenant Detail Failed");
        saveUserProfileFailedDialog.setMessage(getTenantDetailFailedCause);
        saveUserProfileFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        saveUserProfileFailedDialog.create().show();
    }

    private void doRemoveTenant() {
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Remove Tenant");
        loginFailedDialog.setMessage("Are you sure to remove this (" + this.landlordPropertyTenantDetailFirstName.getText().toString() + " " + this.landlordPropertyTenantDetailLastName.getText().toString() +") tenant?");
        loginFailedDialog.setPositiveButton("Yes", (dialog, which) -> {
            this.startLoadingSpinner();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
            if (sessionId == null) {
                this.removeTenantFailed("Please relogin.");
                return;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constants.URL_LANDLORD_PROPERTY_REMOVE_TENANT + "/" + this.tenantId, response -> removeTenantSuccess(),
                    error -> removeTenantFailed(Constants.ERROR_COMMON)) {
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

    private void removeTenantSuccess() {
        this.stopLoadingSpinner();
        setResult(Activity.RESULT_OK);
        this.finish();
    }

    private void removeTenantFailed(String removeTenantFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("Remove Tenant Failed");
        saveUserProfileFailedDialog.setMessage(removeTenantFailedCause);
        saveUserProfileFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        saveUserProfileFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.landlordPropertyTenantDetailEditButton.setEnabled(false);
        this.landlordPropertyTenantDetailRemoveTenantButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordPropertyTenantDetailEditButton.setEnabled(true);
        this.landlordPropertyTenantDetailRemoveTenantButton.setEnabled(true);
    }

}
