package com.propster.landlord;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.content.UserProfileActivity;
import com.propster.login.FirstTimeRoleSelectionActivity;
import com.propster.login.FirstTimeUserProfileActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class LandlordPropertyAddTenantActivity extends AppCompatActivity {

    private EditText landlordAddTenantName;
    private TextView landlordAddTenantNameAlert;
    private Spinner landlordAddTenantTitle;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_add_tenant);

        this.landlordAddTenantName = findViewById(R.id.landlordPropertyAddTenantName);
        this.landlordAddTenantNameAlert = findViewById(R.id.landlordPropertyAddTenantNameAlert);
        this.landlordAddTenantTitle = findViewById(R.id.landlordPropertyAddTenantTitle);
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

        ArrayAdapter<CharSequence> titleArrayAdapter = ArrayAdapter.createFromResource(this, R.array.title, R.layout.spinner_item);
        titleArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordAddTenantTitle.setAdapter(titleArrayAdapter);

        ArrayAdapter<CharSequence> isBusinessArrayAdapter = ArrayAdapter.createFromResource(this, R.array.is_business, R.layout.spinner_item);
        isBusinessArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordAddTenantIsBusiness.setAdapter(isBusinessArrayAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        Date currentDate = new Date();
        Calendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTime(currentDate);
        this.landlordAddTenantDateOfBirth.setText(sdf.format(currentDate));
        this.landlordAddTenantDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(LandlordPropertyAddTenantActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        currentCalendar.set(Calendar.YEAR, year);
                        currentCalendar.set(Calendar.MONTH, month);
                        currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        landlordAddTenantDateOfBirth.setText(sdf.format(currentCalendar.getTime()));
                    }
                }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        this.landlordAddTenantButton = findViewById(R.id.landlordPropertyAddTenantButton);
        this.landlordAddTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAddTenant();
            }
        });

        Toolbar mainToolbar = findViewById(R.id.landlordPropertyAddTenantToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.mainMenuUser) {
                    Intent userProfileIntent = new Intent(LandlordPropertyAddTenantActivity.this, UserProfileActivity.class);
                    startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
                } else if (item.getItemId() == R.id.mainMenuNotification) {
                    Intent notificationIntent = new Intent(LandlordPropertyAddTenantActivity.this, NotificationActivity.class);
                    startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
                }
                return false;
            }
        });
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    private void doAddTenant() {
        if (this.landlordAddTenantName.length() <= 0) {
            this.landlordAddTenantNameAlert.setVisibility(View.VISIBLE);
            this.landlordAddTenantFirstNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantLastNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantSalaryRangeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantName.requestFocus();
            return;
        }
        if (this.landlordAddTenantFirstName.length() <= 0) {
            this.landlordAddTenantNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantFirstNameAlert.setVisibility(View.VISIBLE);
            this.landlordAddTenantLastNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantSalaryRangeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantFirstName.requestFocus();
            return;
        }
        if (this.landlordAddTenantLastName.length() <= 0) {
            this.landlordAddTenantNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantFirstNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantLastNameAlert.setVisibility(View.VISIBLE);
            this.landlordAddTenantDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantSalaryRangeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantLastName.requestFocus();
            return;
        }
        if (this.landlordAddTenantDateOfBirth.length() <= 0) {
            this.landlordAddTenantNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantFirstNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantLastNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantDateOfBirthAlert.setVisibility(View.VISIBLE);
            this.landlordAddTenantSalaryRangeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantDateOfBirth.requestFocus();
            return;
        }
        if (this.landlordAddTenantSalaryRange.length() <= 0) {
            this.landlordAddTenantNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantFirstNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantLastNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.landlordAddTenantSalaryRangeAlert.setVisibility(View.VISIBLE);
            this.landlordAddTenantSalaryRange.requestFocus();
            return;
        }
        this.startLoadingSpinner();

//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("name", this.landlordAddTenantName.getText().toString());
//            postData.put("title", this.landlordAddTenantTitle.getSelectedItemId());
//            postData.put("first_name", this.landlordAddTenantFirstName.getText().toString());
//            postData.put("last_name", this.landlordAddTenantLastName.getText().toString());
//            postData.put("date_of_birth", this.landlordAddTenantDateOfBirth.getText().toString());
//            postData.put("salary_range", this.landlordAddTenantSalaryRange.getText().toString());
//            postData.put("is_business", this.landlordAddTenantIsBusiness.getSelectedItemId());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LANDLORD_PROPERTY_ADD_TENANT, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//                propertyAddTenantSuccess();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                propertyAddTenantFailed(error.getLocalizedMessage());
//                error.printStackTrace();
//            }
//        });
//        this.requestQueue.add(jsonObjectRequest);
        propertyAddTenantSuccess();
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
        saveUserProfileFailedDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
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
