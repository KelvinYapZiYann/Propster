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
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_DETAIL_PROPERTY_ID, -1);
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
        this.landlordPropertyDetailType = findViewById(R.id.landlordPropertyDetailType);
        this.landlordPropertyDetailOwnershipType = findViewById(R.id.landlordPropertyDetailOwnershipType);
        this.landlordPropertyDetailPurchaseValue = findViewById(R.id.landlordPropertyDetailPurchaseValue);
        this.landlordPropertyDetailCurrentValue = findViewById(R.id.landlordPropertyDetailCurrentValue);
        this.landlordPropertyDetailPurchaseDate = findViewById(R.id.landlordPropertyDetailPurchaseDate);
        this.landlordPropertyDetailIsActive = findViewById(R.id.landlordPropertyDetailIsActive);
        this.landlordPropertyDetailInterestRate = findViewById(R.id.landlordPropertyDetailInterestRate);
        this.landlordPropertyDetailOutstandingAmount = findViewById(R.id.landlordPropertyDetailOutstandingAmount);
        this.landlordPropertyDetailTotalYear = findViewById(R.id.landlordPropertyDetailTotalYear);
        this.landlordPropertyDetailEditButton = findViewById(R.id.landlordPropertyDetailEditButton);

        this.backgroundView = findViewById(R.id.landlordPropertyDetailBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyDetailLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        Toolbar mainToolbar = findViewById(R.id.landlordPropertyDetailToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
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

    private void refreshPropertyDetail() {
        this.startLoadingSpinner();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY_DETAIL + "/" + this.propertyId, null, response -> {
            try {
                if (!response.has("data")) {
                    landlordGetPropertyDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (!dataJsonObject.has("id")) {
                    landlordGetPropertyDetailFailed(Constants.ERROR_COMMON);
                }
                if (dataJsonObject.getInt("id") != this.propertyId) {
                    landlordGetPropertyDetailFailed(Constants.ERROR_USER_PROPERTY_DETAIL_ID_NOT_MATCHED);
                }
                JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                this.landlordPropertyDetailName.setText(dataFieldsJsonObject.getString("Asset Nickname"));
                JSONObject dataFieldsLocationJsonObject = dataFieldsJsonObject.getJSONObject("Location Details");
                this.landlordPropertyDetailUnitName.setText(dataFieldsLocationJsonObject.getString("Asset Unit Number"));
                this.landlordPropertyDetailAddressLine1.setText(dataFieldsLocationJsonObject.getString("Asset Addres Line"));
                this.landlordPropertyDetailCity.setText(dataFieldsLocationJsonObject.getString("Asset City"));
                this.landlordPropertyDetailState.setText(dataFieldsLocationJsonObject.getString("Asset State"));
                this.landlordPropertyDetailPostcode.setText(dataFieldsLocationJsonObject.getString("Asset Postal Code"));
                this.landlordPropertyDetailCountry.setText(dataFieldsLocationJsonObject.getString("Asset Country"));
                this.landlordPropertyDetailNumberOfRooms.setText(Integer.toString(dataFieldsJsonObject.getInt("Number Of Rooms")));
                this.landlordPropertyDetailNumberOfBathrooms.setText(Integer.toString(dataFieldsJsonObject.getInt("Number Of Bathroom(s)")));
                this.landlordPropertyDetailType.setText(dataFieldsJsonObject.getString("Asset Type"));
                this.landlordPropertyDetailOwnershipType.setText(dataFieldsJsonObject.getString("Asset Ownership Type"));
                JSONObject dataFieldsFinancialJsonObject = dataFieldsJsonObject.getJSONObject("Financial Details");
                this.landlordPropertyDetailPurchaseValue.setText(dataFieldsFinancialJsonObject.getString("Asset Purchased Value"));
                this.landlordPropertyDetailCurrentValue.setText(dataFieldsFinancialJsonObject.getString("Asset Current Calue"));
                String purchaseDate = dataFieldsFinancialJsonObject.getString("Purchased Date");
                this.landlordPropertyDetailPurchaseDate.setText(purchaseDate.length() > 10 ? purchaseDate.substring(0, 10) : purchaseDate);
                this.landlordPropertyDetailIsActive.setText(dataFieldsFinancialJsonObject.getInt("Loan Is Active") == 1 ? "Yes" : "No");
                this.landlordPropertyDetailInterestRate.setText(dataFieldsFinancialJsonObject.getString("Loan Interest Rate"));
                this.landlordPropertyDetailOutstandingAmount.setText(dataFieldsFinancialJsonObject.getString("Loan Outstanding Amount"));
                this.landlordPropertyDetailTotalYear.setText(dataFieldsFinancialJsonObject.getString("Loan Total Year"));
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
