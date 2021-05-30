package com.propster.landlord;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.propster.content.UserProfileActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LandlordPropertyTenantEditActivity extends AppCompatActivity {

    private Spinner landlordPropertyTenantEditGender;
    private EditText landlordPropertyTenantEditFirstName;
    private TextView landlordPropertyTenantEditFirstNameAlert;
    private EditText landlordPropertyTenantEditLastName;
    private TextView landlordPropertyTenantEditLastNameAlert;
    private EditText landlordPropertyTenantEditDateOfBirth;
    private TextView landlordPropertyTenantEditDateOfBirthAlert;
    private Spinner landlordPropertyTenantEditSalaryRange;
//    private TextView landlordPropertyTenantEditSalaryRangeAlert;
    private Spinner landlordPropertyTenantEditIsBusiness;

    private Button landlordPropertyTenantEditSaveButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int tenantId;
    private String tenantName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_tenant_edit);

        this.landlordPropertyTenantEditGender = findViewById(R.id.landlordPropertyTenantEditGender);
        this.landlordPropertyTenantEditFirstName = findViewById(R.id.landlordPropertyTenantEditFirstName);
        this.landlordPropertyTenantEditFirstNameAlert = findViewById(R.id.landlordPropertyTenantEditFirstNameAlert);
        this.landlordPropertyTenantEditLastName = findViewById(R.id.landlordPropertyTenantEditLastName);
        this.landlordPropertyTenantEditLastNameAlert = findViewById(R.id.landlordPropertyTenantEditLastNameAlert);
        this.landlordPropertyTenantEditDateOfBirth = findViewById(R.id.landlordPropertyTenantEditDateOfBirth);
        this.landlordPropertyTenantEditDateOfBirthAlert = findViewById(R.id.landlordPropertyTenantEditDateOfBirthAlert);
        this.landlordPropertyTenantEditSalaryRange = findViewById(R.id.landlordPropertyTenantEditSalaryRange);
//        this.landlordPropertyTenantEditSalaryRangeAlert = findViewById(R.id.landlordPropertyTenantEditSalaryRangeAlert);
        this.landlordPropertyTenantEditIsBusiness = findViewById(R.id.landlordPropertyTenantEditIsBusiness);

        this.backgroundView = findViewById(R.id.landlordPropertyTenantEditBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyTenantEditLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.tenantId = -1;
            this.tenantName = null;
        } else {
            this.tenantId = extras.getInt(Constants.INTENT_EXTRA_TENANT_ID, -1);
            this.tenantName = extras.getString(Constants.INTENT_EXTRA_TENANT_NAME, null);
        }

        ArrayAdapter<CharSequence> genderArrayAdapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_item);
        genderArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordPropertyTenantEditGender.setAdapter(genderArrayAdapter);

        ArrayAdapter<CharSequence> isBusinessArrayAdapter = ArrayAdapter.createFromResource(this, R.array.is_business, R.layout.spinner_item);
        isBusinessArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordPropertyTenantEditIsBusiness.setAdapter(isBusinessArrayAdapter);

        ArrayAdapter<CharSequence> salaryRangeArrayAdapter = ArrayAdapter.createFromResource(this, R.array.salary_range, R.layout.spinner_item);
        salaryRangeArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordPropertyTenantEditSalaryRange.setAdapter(salaryRangeArrayAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Date currentDate = new Date();
        Calendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTime(currentDate);
        this.landlordPropertyTenantEditDateOfBirth.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(LandlordPropertyTenantEditActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                landlordPropertyTenantEditDateOfBirth.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        this.landlordPropertyTenantEditSaveButton = findViewById(R.id.landlordPropertyTenantEditSaveButton);
        this.landlordPropertyTenantEditSaveButton.setOnClickListener(v -> doSaveTenant());

        Toolbar mainToolbar = findViewById(R.id.landlordPropertyTenantEditToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            if (this.tenantName != null) {
                getSupportActionBar().setTitle(this.tenantName);
            } else {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.mainMenuUser) {
//                Intent userProfileIntent = new Intent(LandlordPropertyTenantEditActivity.this, UserProfileActivity.class);
//                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//            } else
            if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(LandlordPropertyTenantEditActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshTenantDetail();
    }

    private void doSaveTenant() {
        if (this.tenantId == -1) {
            this.saveTenantFailed(Constants.ERROR_COMMON);
            return;
        }
        if (this.landlordPropertyTenantEditFirstName.length() <= 0) {
            this.landlordPropertyTenantEditFirstNameAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyTenantEditLastNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyTenantEditDateOfBirthAlert.setVisibility(View.INVISIBLE);
//            this.landlordPropertyTenantEditSalaryRangeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyTenantEditFirstName.requestFocus();
            return;
        }
        if (this.landlordPropertyTenantEditLastName.length() <= 0) {
            this.landlordPropertyTenantEditFirstNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyTenantEditLastNameAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyTenantEditDateOfBirthAlert.setVisibility(View.INVISIBLE);
//            this.landlordPropertyTenantEditSalaryRangeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyTenantEditLastName.requestFocus();
            return;
        }
        if (this.landlordPropertyTenantEditDateOfBirth.length() <= 0) {
            this.landlordPropertyTenantEditFirstNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyTenantEditLastNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyTenantEditDateOfBirthAlert.setVisibility(View.VISIBLE);
//            this.landlordPropertyTenantEditSalaryRangeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyTenantEditDateOfBirth.requestFocus();
            return;
        }
//        if (this.landlordPropertyTenantEditSalaryRange.length() <= 0) {
//            this.landlordPropertyTenantEditFirstNameAlert.setVisibility(View.INVISIBLE);
//            this.landlordPropertyTenantEditLastNameAlert.setVisibility(View.INVISIBLE);
//            this.landlordPropertyTenantEditDateOfBirthAlert.setVisibility(View.INVISIBLE);
//            this.landlordPropertyTenantEditSalaryRangeAlert.setVisibility(View.VISIBLE);
//            this.landlordPropertyTenantEditSalaryRange.requestFocus();
//            return;
//        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.saveTenantFailed("Please relogin.");
            return;
        }
        JSONObject postData = new JSONObject();
        try {
            postData.put("gender", this.landlordPropertyTenantEditGender.getSelectedItemId() == 0 ? "MALE" : "FEMALE");
            postData.put("first_name", this.landlordPropertyTenantEditFirstName.getText().toString());
            postData.put("last_name", this.landlordPropertyTenantEditLastName.getText().toString());
            postData.put("date_of_birth", this.landlordPropertyTenantEditDateOfBirth.getText().toString());
            switch (this.landlordPropertyTenantEditSalaryRange.getSelectedItemPosition()) {
                case 1:
                    postData.put("salary_range", "5001_TO_10000");
                    break;
                case 2:
                    postData.put("salary_range", "ABOVE_10000");
                    break;
                case 0:
                default:
                    postData.put("salary_range", "1_TO_5000");
                    break;
            }
            postData.put("is_business", this.landlordPropertyTenantEditIsBusiness.getSelectedItemId() != 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Constants.URL_LANDLORD_TENANT + "/" + this.tenantId, postData, response -> {
            saveTenantSuccess();
        }, error -> saveTenantFailed(Constants.ERROR_COMMON)) {
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

    private void saveTenantSuccess() {
        this.stopLoadingSpinner();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void saveTenantFailed(String saveTenantFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveTenantFailedDialog = new AlertDialog.Builder(this);
        saveTenantFailedDialog.setCancelable(false);
        saveTenantFailedDialog.setTitle("Add Tenant Failed");
        saveTenantFailedDialog.setMessage(saveTenantFailedCause);
        saveTenantFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        saveTenantFailedDialog.create().show();
    }

    private void refreshTenantDetail() {
        if (this.tenantId == -1) {
            getTenantDetailFailed(Constants.ERROR_COMMON);
            return;
        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getTenantDetailFailed("Please relogin.");
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_TENANT + "/" + this.tenantId, null, response -> getTenantDetailSuccess(response),
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
            JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
            this.tenantName = dataFieldsJsonObject.getString("first_name");
            this.landlordPropertyTenantEditFirstName.setText(this.tenantName);
            getSupportActionBar().setTitle(this.tenantName);
            this.landlordPropertyTenantEditLastName.setText(dataFieldsJsonObject.getString("last_name"));
            this.landlordPropertyTenantEditGender.setSelection(dataFieldsJsonObject.getString("gender").equalsIgnoreCase("MALE") ? 0 : 1);
            this.landlordPropertyTenantEditIsBusiness.setSelection(dataFieldsJsonObject.getBoolean("is_business") ? 1 : 0);
            String dateOfBirth = dataFieldsJsonObject.getString("date_of_birth");
            this.landlordPropertyTenantEditDateOfBirth.setText(dateOfBirth.length() > 10 ? dateOfBirth.substring(0, 10) : dateOfBirth);
            String salaryRange = dataFieldsJsonObject.getString("salary_range");
            switch (salaryRange) {
                case "1_TO_5000":
                    this.landlordPropertyTenantEditSalaryRange.setSelection(0);
                    break;
                case "5001_TO_10000":
                    this.landlordPropertyTenantEditSalaryRange.setSelection(1);
                    break;
                case "ABOVE_10000":
                    this.landlordPropertyTenantEditSalaryRange.setSelection(2);
                    break;
                default:
                    try {
                        float tmpSalaryRange = Float.parseFloat(salaryRange);
                        if (tmpSalaryRange <= 5000) {
                            this.landlordPropertyTenantEditSalaryRange.setSelection(0);
                        } else if (tmpSalaryRange <= 10000) {
                            this.landlordPropertyTenantEditSalaryRange.setSelection(1);
                        } else {
                            this.landlordPropertyTenantEditSalaryRange.setSelection(2);
                        }
                    } catch (Exception e) {
                        this.landlordPropertyTenantEditSalaryRange.setSelection(0);
                    }
                    break;
            }
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

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.landlordPropertyTenantEditSaveButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordPropertyTenantEditSaveButton.setEnabled(true);
    }
}
