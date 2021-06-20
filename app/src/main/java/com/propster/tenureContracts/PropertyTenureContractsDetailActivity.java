package com.propster.tenureContracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.imageview.ShapeableImageView;
import com.nambimobile.widgets.efab.FabOption;
import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;
import com.propster.utils.FileNameConverter;
import com.propster.utils.PreviewImageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PropertyTenureContractsDetailActivity extends AppCompatActivity {

    private EditText tenureContractsDetailPropertyName;
    private EditText tenureContractsDetailTenantName;
    private EditText tenureContractsDetailName;
    private EditText tenureContractsDetailDescription;
    private EditText tenureContractsDetailMonthlyRentalAmount;
    private EditText tenureContractsDetailTenureStartDate;
    private EditText tenureContractsDetailTenureEndDate;
    private ShapeableImageView tenureContractsDetailUploadedFile;
    private String imageUrl;

    private Button tenureContractsDetailEditButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int propertyId = -1;
    private String propertyName = null;
    private int tenantId = -1;
    private String tenantName = null;
    private int tenureContractId = -1;
    private String tenureContractName = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_tenure_contracts_detail);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.propertyId = -1;
            this.propertyName = null;
            this.tenantId = -1;
            this.tenantName = null;
            this.tenureContractId = -1;
            this.tenureContractName = null;
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
            this.tenantId = extras.getInt(Constants.INTENT_EXTRA_TENANT_ID, -1);
            this.tenantName = extras.getString(Constants.INTENT_EXTRA_TENANT_NAME, null);
            this.tenureContractId = extras.getInt(Constants.INTENT_EXTRA_TENURE_CONTRACTS_ID, -1);
            this.tenureContractName = extras.getString(Constants.INTENT_EXTRA_TENURE_CONTRACTS_NAME, null);
        }

        this.tenureContractsDetailPropertyName = findViewById(R.id.tenureContractsDetailPropertyName);
        this.tenureContractsDetailTenantName = findViewById(R.id.tenureContractsDetailTenantName);
        this.tenureContractsDetailName = findViewById(R.id.tenureContractsDetailName);
        this.tenureContractsDetailDescription = findViewById(R.id.tenureContractsDetailDescription);
        this.tenureContractsDetailMonthlyRentalAmount = findViewById(R.id.tenureContractsDetailMonthlyRentalAmount);
        this.tenureContractsDetailTenureStartDate = findViewById(R.id.tenureContractsDetailTenureStartDate);
        this.tenureContractsDetailTenureEndDate = findViewById(R.id.tenureContractsDetailTenureEndDate);

        this.tenureContractsDetailUploadedFile = findViewById(R.id.tenureContractsDetailUploadedFile);
        this.tenureContractsDetailUploadedFile.setOnClickListener(v -> {
            if (this.imageUrl == null) {
                return;
            }
            if (!(this.tenureContractsDetailUploadedFile.getDrawable() instanceof BitmapDrawable)) {
                return;
            }
            Intent previewImageIntent = new Intent(this, PreviewImageActivity.class);
            previewImageIntent.putExtra(Constants.INTENT_EXTRA_IMAGE_URL, this.imageUrl);
            previewImageIntent.putExtra(Constants.INTENT_EXTRA_IMAGE_NAME, FileNameConverter.convertFileName(this.tenantName + "_image_" + System.currentTimeMillis()));
            startActivity(previewImageIntent);
        });

        this.backgroundView = findViewById(R.id.tenureContractsDetailBackground);
        this.loadingSpinner = findViewById(R.id.tenureContractsDetailLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.tenureContractsDetailEditButton = findViewById(R.id.tenureContractsDetailEditButton);
        this.tenureContractsDetailEditButton.setOnClickListener(v -> {
            Intent tenureContractsEditTenant = new Intent(PropertyTenureContractsDetailActivity.this, PropertyTenureContractsEditActivity.class);
            tenureContractsEditTenant.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, this.propertyId);
            tenureContractsEditTenant.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, this.propertyName);
            tenureContractsEditTenant.putExtra(Constants.INTENT_EXTRA_TENANT_ID, this.tenantId);
            tenureContractsEditTenant.putExtra(Constants.INTENT_EXTRA_TENANT_NAME, this.tenantName);
            tenureContractsEditTenant.putExtra(Constants.INTENT_EXTRA_TENURE_CONTRACTS_ID, this.tenureContractId);
            tenureContractsEditTenant.putExtra(Constants.INTENT_EXTRA_TENURE_CONTRACTS_NAME, this.tenureContractName);
            startActivityForResult(tenureContractsEditTenant, Constants.REQUEST_CODE_TENURE_CONTRACTS_DETAIL);
        });

        FabOption tenureContractsDetailRemoveExpenseButton = findViewById(R.id.tenureContractsDetailRemoveContractButton);
        tenureContractsDetailRemoveExpenseButton.setOnClickListener(v -> {
            this.doRemovePropertyExpenses();
        });

        Toolbar mainToolbar = findViewById(R.id.tenureContractsDetailToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(this.tenureContractName);
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.mainMenuUser) {
//                Intent userProfileIntent = new Intent(tenureContractsDetailActivity.this, UserProfileActivity.class);
//                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//            } else
            if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(PropertyTenureContractsDetailActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshTenureContracts();
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
            this.refreshTenureContracts();
        }
    }

    private void refreshTenureContracts() {
        if (this.tenureContractId == -1) {
            getTenureContractsDetailFailed(Constants.ERROR_COMMON);
            return;
        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getTenureContractsDetailFailed("Please relogin.");
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_TENURE_CONTRACTS + "/" + this.tenureContractId, null, response -> {
            try {
                if (!response.has("data")) {
                    getTenureContractsDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (!dataJsonObject.has("id")) {
                    getTenureContractsDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataJsonObject.has("fields")) {
                    getTenureContractsDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                if (!dataFieldsJsonObject.has("contract_name")) {
                    getTenureContractsDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("contract_description")) {
                    getTenureContractsDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("monthly_rental_amount")) {
                    getTenureContractsDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("tenure_start_date")) {
                    getTenureContractsDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("tenure_end_date")) {
                    getTenureContractsDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                this.tenureContractsDetailPropertyName.setText(this.propertyName);
                this.tenureContractsDetailTenantName.setText(this.tenantName);
                this.tenureContractName = dataFieldsJsonObject.getString("contract_name");
                this.tenureContractsDetailName.setText(this.tenureContractName);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(this.tenureContractName);
                }
                this.tenureContractsDetailDescription.setText(dataFieldsJsonObject.getString("contract_description"));
                this.tenureContractsDetailMonthlyRentalAmount.setText(dataFieldsJsonObject.getString("monthly_rental_amount"));
                this.tenureContractsDetailTenureStartDate.setText(dataFieldsJsonObject.getString("tenure_start_date"));
                this.tenureContractsDetailTenureEndDate.setText(dataFieldsJsonObject.getString("tenure_end_date"));
                this.stopLoadingSpinner();
                if (!dataFieldsJsonObject.has("media")) {
                    return;
                }
                JSONArray dataFieldsMediaJsonArray = dataFieldsJsonObject.getJSONArray("media");
                if (dataFieldsMediaJsonArray.length() <= 0) {
                    return;
                }
                JSONObject dataFieldsMediaJsonObject = dataFieldsMediaJsonArray.getJSONObject(0);
                if (!dataFieldsMediaJsonObject.has("temporary_url")) {
                    return;
                }
                String mediaTemporaryUrl = dataFieldsMediaJsonObject.getString("temporary_url");
                displayImageFromUrl(mediaTemporaryUrl);
            } catch (JSONException e) {
                e.printStackTrace();
                getTenureContractsDetailFailed(Constants.ERROR_COMMON);
            }
        }, error -> getTenureContractsDetailFailed(Constants.ERROR_COMMON)) {
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

    private void displayImageFromUrl(String mediaTemporaryUrl) {
        this.startLoadingSpinner();
        ImageRequest imageRequest = new ImageRequest(mediaTemporaryUrl, response -> {
            tenureContractsDetailUploadedFile.setImageBitmap(response);
            this.imageUrl = mediaTemporaryUrl;
            this.stopLoadingSpinner();
        }, 0, 0, null, null, error -> {
            getTenureContractsDetailFailed(Constants.ERROR_IMAGE_DOCUMENT_FILE_NOT_LOADED);
        });
        imageRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(imageRequest);
    }

    private void getTenureContractsDetailFailed(String propertyExpensesDetailFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder propertyExpensesDetailFailedDialog = new AlertDialog.Builder(this);
        propertyExpensesDetailFailedDialog.setCancelable(false);
        propertyExpensesDetailFailedDialog.setTitle("Tenure Contracts Failed");
        propertyExpensesDetailFailedDialog.setMessage(propertyExpensesDetailFailed);
        propertyExpensesDetailFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        propertyExpensesDetailFailedDialog.create().show();
    }

    private void doRemovePropertyExpenses() {
        AlertDialog.Builder removePropertyExpensesFailedDialog = new AlertDialog.Builder(this);
        removePropertyExpensesFailedDialog.setCancelable(false);
        removePropertyExpensesFailedDialog.setTitle("Remove Property Expenses Failed");
        removePropertyExpensesFailedDialog.setMessage("Are you sure to remove this Asset Expenses?");
        removePropertyExpensesFailedDialog.setPositiveButton("Yes", (dialog, which) -> {
            dialog.cancel();
            this.startLoadingSpinner();
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constants.URL_LANDLORD_TENURE_CONTRACTS + "/" + this.tenureContractId, response -> removePropertyExpensesSuccess(),
                    error -> removePropertyExpensesFailed()) {
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
        });
        removePropertyExpensesFailedDialog.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        removePropertyExpensesFailedDialog.create().show();
    }

    private void removePropertyExpensesSuccess() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void removePropertyExpensesFailed() {
        this.stopLoadingSpinner();
        AlertDialog.Builder propertyExpensesDetailFailedDialog = new AlertDialog.Builder(this);
        propertyExpensesDetailFailedDialog.setCancelable(false);
        propertyExpensesDetailFailedDialog.setTitle("Remove Property Expenses Failed");
        propertyExpensesDetailFailedDialog.setMessage(Constants.ERROR_COMMON);
        propertyExpensesDetailFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        propertyExpensesDetailFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.tenureContractsDetailEditButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.tenureContractsDetailEditButton.setEnabled(true);
    }
}
