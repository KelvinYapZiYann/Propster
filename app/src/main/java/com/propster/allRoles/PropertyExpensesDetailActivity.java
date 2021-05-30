package com.propster.allRoles;

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
import com.google.android.material.imageview.ShapeableImageView;
import com.nambimobile.widgets.efab.FabOption;
import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PropertyExpensesDetailActivity extends AppCompatActivity {

    private EditText propertyExpensesDetailDescription;
    private EditText propertyExpensesDetailPropertyName;
    private EditText propertyExpensesDetailType;
    private EditText propertyExpensesDetailVendor;
    private EditText propertyExpensesDetailAmount;
    private EditText propertyExpensesDetailDateOfExpense;
    private ShapeableImageView propertyExpensesDetailUploadedFile;

    private Button propertyExpensesDetailEditButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int propertyId = -1;
    private String propertyName = null;
    private int expenseId = -1;
    private String expenseName = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_expenses_detail);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.propertyId = -1;
            this.propertyName = null;
            this.expenseId = -1;
            this.expenseName = null;
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
            this.expenseId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_ID, -1);
            this.expenseName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_NAME, null);
        }

        this.propertyExpensesDetailDescription = findViewById(R.id.propertyExpensesDetailDescription);
        this.propertyExpensesDetailPropertyName = findViewById(R.id.propertyExpensesDetailPropertyName);
        this.propertyExpensesDetailType = findViewById(R.id.propertyExpensesDetailType);
        this.propertyExpensesDetailVendor = findViewById(R.id.propertyExpensesDetailVendor);
        this.propertyExpensesDetailAmount = findViewById(R.id.propertyExpensesDetailAmount);
        this.propertyExpensesDetailDateOfExpense = findViewById(R.id.propertyExpensesDetailDateOfExpense);

        this.propertyExpensesDetailUploadedFile = findViewById(R.id.propertyExpensesDetailUploadedFile);
//        this.propertyExpensesDetailUploadedFile.setOnClickListener(v -> {
//
//        });

        this.backgroundView = findViewById(R.id.propertyExpensesDetailBackground);
        this.loadingSpinner = findViewById(R.id.propertyExpensesDetailLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.propertyExpensesDetailEditButton = findViewById(R.id.propertyExpensesDetailEditButton);
        this.propertyExpensesDetailEditButton.setOnClickListener(v -> {
            Intent propertyExpensesEditTenant = new Intent(PropertyExpensesDetailActivity.this, PropertyExpensesEditActivity.class);
            propertyExpensesEditTenant.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, this.propertyId);
            propertyExpensesEditTenant.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, this.propertyName);
            propertyExpensesEditTenant.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_ID, this.expenseId);
            propertyExpensesEditTenant.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_NAME, this.expenseName);
            startActivityForResult(propertyExpensesEditTenant, Constants.REQUEST_CODE_PROPERTY_EXPENSES_DETAIL);
        });

        FabOption propertyExpensesDetailRemoveExpenseButton = findViewById(R.id.propertyExpensesDetailRemoveExpenseButton);
        propertyExpensesDetailRemoveExpenseButton.setOnClickListener(v -> {
            this.doRemovePropertyExpenses();
        });

        Toolbar mainToolbar = findViewById(R.id.propertyExpensesDetailToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(this.expenseName);
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.mainMenuUser) {
//                Intent userProfileIntent = new Intent(PropertyExpensesDetailActivity.this, UserProfileActivity.class);
//                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//            } else
            if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(PropertyExpensesDetailActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshPropertyExpenses();
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
        if (requestCode == Constants.REQUEST_CODE_PROPERTY_EXPENSES_DETAIL) {
            this.refreshPropertyExpenses();
        }
    }

    private void refreshPropertyExpenses() {
        if (this.expenseId == -1) {
            getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
            return;
        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getPropertyExpensesDetailFailed("Please relogin.");
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY_EXPENSES + "/" + this.expenseId, null, response -> {
            try {
                if (!response.has("data")) {
                    getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (!dataJsonObject.has("id")) {
                    getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataJsonObject.has("fields")) {
                    getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                if (!dataFieldsJsonObject.has("payment_description")) {
                    getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("expense_type")) {
                    getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("vendor")) {
                    getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("amount")) {
                    getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("date_of_expense")) {
                    getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
                    return;
                }
                this.expenseName = dataFieldsJsonObject.getString("payment_description");
                this.propertyExpensesDetailDescription.setText(this.expenseName);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(this.expenseName);
                }
                this.propertyExpensesDetailType.setText(dataFieldsJsonObject.getString("expense_type"));
                this.propertyExpensesDetailVendor.setText(dataFieldsJsonObject.getString("vendor"));
                this.propertyExpensesDetailAmount.setText(dataFieldsJsonObject.getString("amount"));
                this.propertyExpensesDetailDateOfExpense.setText(dataFieldsJsonObject.getString("date_of_expense"));
//                this.doGetPropertyExpensePropertyName();
                this.propertyExpensesDetailPropertyName.setText(this.propertyName);
                this.stopLoadingSpinner();
            } catch (JSONException e) {
                e.printStackTrace();
                getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
            }
        }, error -> getPropertyExpensesDetailFailed(Constants.ERROR_COMMON)) {
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

//    private void doGetPropertyExpensePropertyName() {
//        this.startLoadingSpinner();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY + "/" + this.propertyId, null, response -> {
//            try {
//                if (!response.has("data")) {
//                    getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
//                    return;
//                }
//                JSONObject dataJsonObject = response.getJSONObject("data");
//                if (!dataJsonObject.has("id")) {
//                    getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
//                    return;
//                }
//                JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
//                this.propertyExpensesDetailPropertyName.setText(dataFieldsJsonObject.getString("asset_nickname"));
//                this.stopLoadingSpinner();
//            } catch (JSONException e) {
//                getPropertyExpensesDetailFailed(Constants.ERROR_COMMON);
//            }
//        }, error -> getPropertyExpensesDetailFailed(Constants.ERROR_COMMON)) {
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

    private void getPropertyExpensesDetailFailed(String propertyExpensesDetailFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder propertyExpensesDetailFailedDialog = new AlertDialog.Builder(this);
        propertyExpensesDetailFailedDialog.setCancelable(false);
        propertyExpensesDetailFailedDialog.setTitle("Property Expenses Detail Failed");
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
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constants.URL_LANDLORD_PROPERTY_EXPENSES + "/" + this.expenseId, response -> removePropertyExpensesSuccess(),
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
        this.propertyExpensesDetailEditButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.propertyExpensesDetailEditButton.setEnabled(true);
    }

}
