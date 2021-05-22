package com.propster.landlord;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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

public class LandlordPropertyAddTenantActivity extends AppCompatActivity {

//    private EditText landlordAddTenantName;
//    private TextView landlordAddTenantNameAlert;
//    private Spinner landlordAddTenantTitle;
    private Spinner landlordAddTenantGender;
    private EditText landlordAddTenantFirstName;
    private TextView landlordAddTenantFirstNameAlert;
    private EditText landlordAddTenantLastName;
    private TextView landlordAddTenantLastNameAlert;
    private EditText landlordAddTenantDateOfBirth;
    private TextView landlordAddTenantDateOfBirthAlert;
    private EditText landlordAddTenantSalaryRange;
    private TextView landlordAddTenantSalaryRangeAlert;
    private Spinner landlordAddTenantIsBusiness;

    private Button landlordAddTenantButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int propertyId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_add_tenant);

//        this.landlordAddTenantName = findViewById(R.id.landlordPropertyAddTenantName);
//        this.landlordAddTenantNameAlert = findViewById(R.id.landlordPropertyAddTenantNameAlert);
//        this.landlordAddTenantTitle = findViewById(R.id.landlordPropertyAddTenantTitle);
        this.landlordAddTenantGender = findViewById(R.id.landlordPropertyAddTenantGender);
        this.landlordAddTenantFirstName = findViewById(R.id.landlordPropertyAddTenantFirstName);
        this.landlordAddTenantFirstNameAlert = findViewById(R.id.landlordPropertyAddTenantFirstNameAlert);
        this.landlordAddTenantLastName = findViewById(R.id.landlordPropertyAddTenantLastName);
        this.landlordAddTenantLastNameAlert = findViewById(R.id.landlordPropertyAddTenantLastNameAlert);
        this.landlordAddTenantDateOfBirth = findViewById(R.id.landlordPropertyAddTenantDateOfBirth);
        this.landlordAddTenantDateOfBirthAlert = findViewById(R.id.landlordPropertyAddTenantDateOfBirthAlert);
        this.landlordAddTenantSalaryRange = findViewById(R.id.landlordPropertyAddTenantSalaryRange);
        this.landlordAddTenantSalaryRangeAlert = findViewById(R.id.landlordPropertyAddTenantSalaryRangeAlert);
        this.landlordAddTenantIsBusiness = findViewById(R.id.landlordPropertyAddTenantIsBusiness);

        this.backgroundView = findViewById(R.id.landlordPropertyAddTenantBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyAddTenantLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.propertyId = -1;
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_PROPERTY_ID, -1);
        }


//        ArrayAdapter<CharSequence> titleArrayAdapter = ArrayAdapter.createFromResource(this, R.array.title, R.layout.spinner_item);
//        titleArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
//        this.landlordAddTenantTitle.setAdapter(titleArrayAdapter);

        ArrayAdapter<CharSequence> genderArrayAdapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_item);
        genderArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordAddTenantGender.setAdapter(genderArrayAdapter);

        ArrayAdapter<CharSequence> isBusinessArrayAdapter = ArrayAdapter.createFromResource(this, R.array.is_business, R.layout.spinner_item);
        isBusinessArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordAddTenantIsBusiness.setAdapter(isBusinessArrayAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Date currentDate = new Date();
        Calendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTime(currentDate);
        this.landlordAddTenantDateOfBirth.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(LandlordPropertyAddTenantActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                landlordAddTenantDateOfBirth.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        this.landlordAddTenantButton = findViewById(R.id.landlordPropertyAddTenantButton);
        this.landlordAddTenantButton.setOnClickListener(v -> doAddTenant());

        Toolbar mainToolbar = findViewById(R.id.landlordPropertyAddTenantToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.mainMenuUser) {
                Intent userProfileIntent = new Intent(LandlordPropertyAddTenantActivity.this, UserProfileActivity.class);
                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            } else if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(LandlordPropertyAddTenantActivity.this, NotificationActivity.class);
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

    private void doAddTenant() {
//        if (this.landlordAddTenantName.length() <= 0) {
//            this.landlordAddTenantNameAlert.setVisibility(View.VISIBLE);
//            this.landlordAddTenantFirstNameAlert.setVisibility(View.INVISIBLE);
//            this.landlordAddTenantLastNameAlert.setVisibility(View.INVISIBLE);
//            this.landlordAddTenantDateOfBirthAlert.setVisibility(View.INVISIBLE);
//            this.landlordAddTenantSalaryRangeAlert.setVisibility(View.INVISIBLE);
//            this.landlordAddTenantName.requestFocus();
//            return;
//        }
        if (this.landlordAddTenantFirstName.length() <= 0) {
//            this.landlordAddTenantNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantFirstNameAlert.setVisibility(View.VISIBLE);
            this.landlordAddTenantLastNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantSalaryRangeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantFirstName.requestFocus();
            return;
        }
        if (this.landlordAddTenantLastName.length() <= 0) {
//            this.landlordAddTenantNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantFirstNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantLastNameAlert.setVisibility(View.VISIBLE);
            this.landlordAddTenantDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantSalaryRangeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantLastName.requestFocus();
            return;
        }
        if (this.landlordAddTenantDateOfBirth.length() <= 0) {
//            this.landlordAddTenantNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantFirstNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantLastNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantDateOfBirthAlert.setVisibility(View.VISIBLE);
            this.landlordAddTenantSalaryRangeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantDateOfBirth.requestFocus();
            return;
        }
        if (this.landlordAddTenantSalaryRange.length() <= 0) {
//            this.landlordAddTenantNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantFirstNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantLastNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantSalaryRangeAlert.setVisibility(View.VISIBLE);
            this.landlordAddTenantSalaryRange.requestFocus();
            return;
        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.propertyAddTenantFailed("Please relogin.");
            return;
        }
        JSONObject postData = new JSONObject();
        try {
            postData.put("gender", this.landlordAddTenantGender.getSelectedItemId() == 0 ? "MALE" : "FEMALE");
            postData.put("first_name", this.landlordAddTenantFirstName.getText().toString());
            postData.put("last_name", this.landlordAddTenantLastName.getText().toString());
            postData.put("date_of_birth", this.landlordAddTenantDateOfBirth.getText().toString());
//            postData.put("salary_range", this.landlordAddTenantSalaryRange.getText().toString() + "_TO_" + (Integer.parseInt(this.landlordAddTenantSalaryRange.getText().toString()) + 1000));
            postData.put("salary_range", "1_TO_5000");
//            postData.put("is_business", this.landlordAddTenantIsBusiness.getSelectedItemId() != 0);
            postData.put("is_business", true);
            postData.put("asset_id", this.propertyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LANDLORD_PROPERTY_ADD_TENANT, postData, response -> {
            propertyAddTenantSuccess();
        }, error -> {
            try {
                System.out.println("error.networkResponse.data = " + new String(error.networkResponse.data));
            } catch (Exception e) {
                e.printStackTrace();
            }
            propertyAddTenantFailed(Constants.ERROR_COMMON);
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

    private void propertyAddTenantSuccess() {
        this.stopLoadingSpinner();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void propertyAddTenantFailed(String saveUserProfileFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("Add Tenant Failed");
        saveUserProfileFailedDialog.setMessage(saveUserProfileFailedCause);
        saveUserProfileFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        saveUserProfileFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.landlordAddTenantButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordAddTenantButton.setEnabled(true);
    }

}
