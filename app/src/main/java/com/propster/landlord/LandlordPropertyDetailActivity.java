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
import com.android.volley.toolbox.Volley;
import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.content.UserProfileActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LandlordPropertyDetailActivity extends AppCompatActivity {

    private EditText landlordPropertyDetailName;
    private EditText landlordPropertyDetailUnitName;
    private EditText landlordPropertyDetailAddressLine1;
    private EditText landlordPropertyDetailCity;
    private EditText landlordPropertyDetailPostcode;
    private EditText landlordPropertyDetailState;
    private EditText landlordPropertyDetailCountry;
    private EditText landlordPropertyDetailNumberOfRooms;
    private EditText landlordPropertyDetailNumberOfBathrooms;
    private EditText landlordPropertyDetailSize;
    private EditText landlordPropertyDetailType;
    private EditText landlordPropertyDetailOwnershipType;
    private EditText landlordPropertyDetailPurchaseValue;
    private EditText landlordPropertyDetailCurrentValue;
    private EditText landlordPropertyDetailPurchaseDate;
    private EditText landlordPropertyDetailIsActive;
    private EditText landlordPropertyDetailInterestRate;
    private EditText landlordPropertyDetailOutstandingAmount;
    private EditText landlordPropertyDetailTotalYear;

    private Button landlordPropertyDetailEditButton;

    private int propertyId;
    private String propertyName;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_detail);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.propertyId = -1;
            this.propertyName = null;
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
        }

        this.landlordPropertyDetailName = findViewById(R.id.landlordPropertyDetailName);
        this.landlordPropertyDetailUnitName = findViewById(R.id.landlordPropertyDetailUnitName);
        this.landlordPropertyDetailAddressLine1 = findViewById(R.id.landlordPropertyDetailAddressLine1);
        this.landlordPropertyDetailCity = findViewById(R.id.landlordPropertyDetailCity);
        this.landlordPropertyDetailPostcode = findViewById(R.id.landlordPropertyDetailPostcode);
        this.landlordPropertyDetailState = findViewById(R.id.landlordPropertyDetailState);
        this.landlordPropertyDetailCountry = findViewById(R.id.landlordPropertyDetailCountry);
        this.landlordPropertyDetailNumberOfRooms = findViewById(R.id.landlordPropertyDetailNumberOfRooms);
        this.landlordPropertyDetailNumberOfBathrooms = findViewById(R.id.landlordPropertyDetailNumberOfBathrooms);
        this.landlordPropertyDetailSize = findViewById(R.id.landlordPropertyDetailSize);
        this.landlordPropertyDetailType = findViewById(R.id.landlordPropertyDetailType);
        this.landlordPropertyDetailOwnershipType = findViewById(R.id.landlordPropertyDetailOwnershipType);
        this.landlordPropertyDetailPurchaseValue = findViewById(R.id.landlordPropertyDetailPurchaseValue);
        this.landlordPropertyDetailCurrentValue = findViewById(R.id.landlordPropertyDetailCurrentValue);
        this.landlordPropertyDetailPurchaseDate = findViewById(R.id.landlordPropertyDetailPurchaseDate);
        this.landlordPropertyDetailIsActive = findViewById(R.id.landlordPropertyDetailIsActive);
        this.landlordPropertyDetailInterestRate = findViewById(R.id.landlordPropertyDetailInterestRate);
        this.landlordPropertyDetailOutstandingAmount = findViewById(R.id.landlordPropertyDetailOutstandingAmount);
        this.landlordPropertyDetailTotalYear = findViewById(R.id.landlordPropertyDetailTotalYear);

        this.backgroundView = findViewById(R.id.landlordPropertyDetailBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyDetailLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.landlordPropertyDetailEditButton = findViewById(R.id.landlordPropertyDetailEditButton);
        this.landlordPropertyDetailEditButton.setOnClickListener(v -> {
            Intent propertyDetailIntent = new Intent(LandlordPropertyDetailActivity.this, LandlordPropertyEditActivity.class);
            propertyDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, this.propertyId);
            propertyDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, this.propertyName);
            startActivityForResult(propertyDetailIntent, Constants.REQUEST_CODE_LANDLORD_PROPERTY_DETAIL);
        });

        Toolbar mainToolbar = findViewById(R.id.landlordPropertyDetailToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            if (this.propertyName != null) {
                getSupportActionBar().setTitle(this.propertyName);
            } else {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.mainMenuUser) {
                Intent userProfileIntent = new Intent(LandlordPropertyDetailActivity.this, UserProfileActivity.class);
                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            } else if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(LandlordPropertyDetailActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());
        this.refreshPropertyDetail();
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
        if (requestCode == Constants.REQUEST_CODE_LANDLORD_PROPERTY_DETAIL) {
            this.refreshPropertyDetail();
        }
    }

    private void refreshPropertyDetail() {
        this.startLoadingSpinner();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY + "/" + this.propertyId, null, response -> {
            try {
                if (!response.has("data")) {
                    landlordGetPropertyDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (!dataJsonObject.has("id")) {
                    landlordGetPropertyDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (dataJsonObject.getInt("id") != this.propertyId) {
                    landlordGetPropertyDetailFailed(Constants.ERROR_USER_PROPERTY_DETAIL_ID_NOT_MATCHED);
                    return;
                }
                JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                this.landlordPropertyDetailName.setText(dataFieldsJsonObject.getString("asset_nickname"));
                JSONObject dataFieldsLocationJsonObject = dataFieldsJsonObject.getJSONObject("location_details");
                this.landlordPropertyDetailUnitName.setText(dataFieldsLocationJsonObject.getString("asset_unit_no"));
                this.landlordPropertyDetailAddressLine1.setText(dataFieldsLocationJsonObject.getString("asset_address_line"));
                this.landlordPropertyDetailCity.setText(dataFieldsLocationJsonObject.getString("asset_city"));
                this.landlordPropertyDetailState.setText(dataFieldsLocationJsonObject.getString("asset_state"));
                this.landlordPropertyDetailPostcode.setText(dataFieldsLocationJsonObject.getString("asset_postal_code"));
                this.landlordPropertyDetailCountry.setText(dataFieldsLocationJsonObject.getString("asset_country").equalsIgnoreCase("MY") ? "Malaysia" : dataFieldsLocationJsonObject.getString("asset_country"));
                this.landlordPropertyDetailNumberOfRooms.setText(Integer.toString(dataFieldsJsonObject.getInt("number_of_rooms")));
                this.landlordPropertyDetailNumberOfBathrooms.setText(Integer.toString(dataFieldsJsonObject.getInt("number_of_bathrooms")));
                this.landlordPropertyDetailSize.setText(Double.toString(dataFieldsJsonObject.getDouble("asset_size")));
                this.landlordPropertyDetailType.setText(dataFieldsJsonObject.getString("asset_type").equalsIgnoreCase("RESIDENTIAL") ? "Residential" : "Commercial");
                this.landlordPropertyDetailOwnershipType.setText(dataFieldsJsonObject.getString("asset_ownership_type").equalsIgnoreCase("FREEHOLD") ? "Freehold" : "Leasehold");
                JSONObject dataFieldsFinancialJsonObject = dataFieldsJsonObject.getJSONObject("financial_details");
                this.landlordPropertyDetailPurchaseValue.setText(dataFieldsFinancialJsonObject.getString("asset_purchased_value"));
                this.landlordPropertyDetailCurrentValue.setText(dataFieldsFinancialJsonObject.getString("asset_current_value"));
                String purchaseDate = dataFieldsFinancialJsonObject.getString("purchased_date");
                this.landlordPropertyDetailPurchaseDate.setText(purchaseDate.length() > 10 ? purchaseDate.substring(0, 10) : purchaseDate);
                this.landlordPropertyDetailIsActive.setText(dataFieldsFinancialJsonObject.getInt("loan_is_active") == 1 ? "Yes" : "No");
                this.landlordPropertyDetailInterestRate.setText(dataFieldsFinancialJsonObject.getString("loan_interest_rate"));
                this.landlordPropertyDetailOutstandingAmount.setText(dataFieldsFinancialJsonObject.getString("loan_outstanding_amount"));
                this.landlordPropertyDetailTotalYear.setText(dataFieldsFinancialJsonObject.getString("loan_total_year"));
                this.landlordGetPropertyDetailSuccess();
            } catch (JSONException e) {
                e.printStackTrace();
                landlordGetPropertyDetailFailed(Constants.ERROR_USER_PROPERTY_DETAIL_ID_NOT_MATCHED);
            }
        }, error -> {
            try {
                String errorResponseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorResponseBodyJsonObject = new JSONObject(errorResponseBody);
                if (!errorResponseBodyJsonObject.has("message")) {
                    landlordGetPropertyDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                landlordGetPropertyDetailFailed(errorResponseBodyJsonObject.getString("message"));
            } catch (Exception e) {
                landlordGetPropertyDetailFailed(Constants.ERROR_COMMON);
            }
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

    private void landlordGetPropertyDetailSuccess() {
        this.stopLoadingSpinner();
    }

    private void landlordGetPropertyDetailFailed(String landlordGetPropertyDetailFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Property Detail Failed");
        loginFailedDialog.setMessage(landlordGetPropertyDetailFailed);
        loginFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        loginFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.landlordPropertyDetailEditButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordPropertyDetailEditButton.setEnabled(true);
    }
}
