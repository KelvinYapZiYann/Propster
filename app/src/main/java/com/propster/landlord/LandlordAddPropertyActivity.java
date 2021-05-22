package com.propster.landlord;

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

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LandlordAddPropertyActivity extends AppCompatActivity {

    private EditText landlordAddPropertyName;
    private TextView landlordAddPropertyNameAlert;
    private EditText landlordAddPropertyUnitName;
    private TextView landlordAddPropertyUnitNameAlert;
//    private EditText landlordAddPropertyFloor;
    private EditText landlordAddPropertyAddressLine1;
    private TextView landlordAddPropertyAddressLine1Alert;
//    private EditText landlordAddPropertyAddressLine2;
    private EditText landlordAddPropertyCity;
    private TextView landlordAddPropertyCityAlert;
    private EditText landlordAddPropertyPostcode;
    private TextView landlordAddPropertyPostcodeAlert;
    private EditText landlordAddPropertyState;
    private TextView landlordAddPropertyStateAlert;
    private EditText landlordAddPropertyCountry;
    private TextView landlordAddPropertyCountryAlert;
    private EditText landlordAddPropertyNumberOfRooms;
    private TextView landlordAddPropertyNumberOfRoomsAlert;
    private EditText landlordAddPropertyNumberOfBathrooms;
    private TextView landlordAddPropertyNumberOfBathroomsAlert;
    private Spinner landlordAddPropertyType;
    private Spinner landlordAddPropertyOwnershipType;
    private EditText landlordAddPropertyPurchaseValue;
    private TextView landlordAddPropertyPurchaseValueAlert;
    private EditText landlordAddPropertyCurrentValue;
    private TextView landlordAddPropertyCurrentValueAlert;
    private EditText landlordAddPropertyPurchaseDate;
    private TextView landlordAddPropertyPurchaseDateAlert;
    private Spinner landlordAddPropertyIsActive;
    private EditText landlordAddPropertyInterestRate;
    private TextView landlordAddPropertyInterestRateAlert;
    private EditText landlordAddPropertyOutstandingAmount;
    private TextView landlordAddPropertyOutstandingAmountAlert;
    private EditText landlordAddPropertyTotalYear;
    private TextView landlordAddPropertyTotalYearAlert;

    private Button landlordAddPropertyButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_add_property);

        this.landlordAddPropertyName = findViewById(R.id.landlordAddPropertyName);
        this.landlordAddPropertyNameAlert = findViewById(R.id.landlordAddPropertyNameAlert);
        this.landlordAddPropertyUnitName = findViewById(R.id.landlordAddPropertyUnitName);
        this.landlordAddPropertyUnitNameAlert = findViewById(R.id.landlordAddPropertyUnitNameAlert);
//        this.landlordAddPropertyFloor = findViewById(R.id.landlordAddPropertyFloor);
        this.landlordAddPropertyAddressLine1 = findViewById(R.id.landlordAddPropertyAddressLine1);
        this.landlordAddPropertyAddressLine1Alert = findViewById(R.id.landlordAddPropertyAddressLine1Alert);
//        this.landlordAddPropertyAddressLine2 = findViewById(R.id.landlordAddPropertyAddressLine2);
        this.landlordAddPropertyCity = findViewById(R.id.landlordAddPropertyCity);
        this.landlordAddPropertyCityAlert = findViewById(R.id.landlordAddPropertyCityAlert);
        this.landlordAddPropertyPostcode = findViewById(R.id.landlordAddPropertyPostcode);
        this.landlordAddPropertyPostcodeAlert = findViewById(R.id.landlordAddPropertyPostcodeAlert);
        this.landlordAddPropertyState = findViewById(R.id.landlordAddPropertyState);
        this.landlordAddPropertyStateAlert = findViewById(R.id.landlordAddPropertyStateAlert);
        this.landlordAddPropertyCountry = findViewById(R.id.landlordAddPropertyCountry);
        this.landlordAddPropertyCountryAlert = findViewById(R.id.landlordAddPropertyCountryAlert);
        this.landlordAddPropertyNumberOfRooms = findViewById(R.id.landlordAddPropertyNumberOfRooms);
        this.landlordAddPropertyNumberOfRoomsAlert = findViewById(R.id.landlordAddPropertyNumberOfRoomsAlert);
        this.landlordAddPropertyNumberOfBathrooms = findViewById(R.id.landlordAddPropertyNumberOfBathrooms);
        this.landlordAddPropertyNumberOfBathroomsAlert = findViewById(R.id.landlordAddPropertyNumberOfBathroomsAlert);
        this.landlordAddPropertyType = findViewById(R.id.landlordAddPropertyType);
        this.landlordAddPropertyOwnershipType = findViewById(R.id.landlordAddPropertyOwnershipType);
        this.landlordAddPropertyPurchaseValue = findViewById(R.id.landlordAddPropertyPurchaseValue);
        this.landlordAddPropertyPurchaseValueAlert = findViewById(R.id.landlordAddPropertyPurchaseValueAlert);
        this.landlordAddPropertyCurrentValue = findViewById(R.id.landlordAddPropertyCurrentValue);
        this.landlordAddPropertyCurrentValueAlert = findViewById(R.id.landlordAddPropertyCurrentValueAlert);
        this.landlordAddPropertyPurchaseDate = findViewById(R.id.landlordAddPropertyPurchaseDate);
        this.landlordAddPropertyPurchaseDateAlert = findViewById(R.id.landlordAddPropertyPurchaseDateAlert);
        this.landlordAddPropertyIsActive = findViewById(R.id.landlordAddPropertyIsActive);
        this.landlordAddPropertyInterestRate = findViewById(R.id.landlordAddPropertyInterestRate);
        this.landlordAddPropertyInterestRateAlert = findViewById(R.id.landlordAddPropertyInterestRateAlert);
        this.landlordAddPropertyOutstandingAmount = findViewById(R.id.landlordAddPropertyOutstandingAmount);
        this.landlordAddPropertyOutstandingAmountAlert = findViewById(R.id.landlordAddPropertyOutstandingAmountAlert);
        this.landlordAddPropertyTotalYear = findViewById(R.id.landlordAddPropertyTotalYear);
        this.landlordAddPropertyTotalYearAlert = findViewById(R.id.landlordAddPropertyTotalYearAlert);

        this.backgroundView = findViewById(R.id.landlordAddPropertyBackground);
        this.loadingSpinner = findViewById(R.id.landlordAddPropertyLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        ArrayAdapter<CharSequence> propertyTypeArrayAdapter = ArrayAdapter.createFromResource(this, R.array.property_type, R.layout.spinner_item);
        propertyTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordAddPropertyType.setAdapter(propertyTypeArrayAdapter);

        ArrayAdapter<CharSequence> ownershipTypeArrayAdapter = ArrayAdapter.createFromResource(this, R.array.ownership_type, R.layout.spinner_item);
        ownershipTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordAddPropertyOwnershipType.setAdapter(ownershipTypeArrayAdapter);

        ArrayAdapter<CharSequence> isActiveArrayAdapter = ArrayAdapter.createFromResource(this, R.array.loan_is_active, R.layout.spinner_item);
        isActiveArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordAddPropertyIsActive.setAdapter(isActiveArrayAdapter);

        this.landlordAddPropertyButton = findViewById(R.id.landlordAddPropertyButton);
        this.landlordAddPropertyButton.setOnClickListener(v -> doAddProperty());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Calendar currentCalendar = new GregorianCalendar();
        this.landlordAddPropertyPurchaseDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(LandlordAddPropertyActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                landlordAddPropertyPurchaseDate.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        Toolbar mainToolbar = findViewById(R.id.landlordAddPropertyToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.mainMenuUser) {
                Intent userProfileIntent = new Intent(LandlordAddPropertyActivity.this, UserProfileActivity.class);
                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            } else if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(LandlordAddPropertyActivity.this, NotificationActivity.class);
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

    private void doAddProperty() {
        if (this.landlordAddPropertyName.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyName.requestFocus();
            return;
        }
        if (this.landlordAddPropertyUnitName.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitName.requestFocus();
            return;
        }
        if (this.landlordAddPropertyAddressLine1.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1.requestFocus();
            return;
        }
        if (this.landlordAddPropertyCity.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCity.requestFocus();
            return;
        }
        if (this.landlordAddPropertyPostcode.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcode.requestFocus();
            return;
        }
        if (this.landlordAddPropertyState.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyState.requestFocus();
            return;
        }
        if (this.landlordAddPropertyCountry.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountry.requestFocus();
            return;
        }
        if (this.landlordAddPropertyNumberOfRooms.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRooms.requestFocus();
            return;
        }
        if (this.landlordAddPropertyNumberOfBathrooms.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathrooms.requestFocus();
            return;
        }
        if (this.landlordAddPropertyPurchaseValue.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValue.requestFocus();
            return;
        }
        if (this.landlordAddPropertyCurrentValue.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValue.requestFocus();
            return;
        }
        if (this.landlordAddPropertyPurchaseDate.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDate.requestFocus();
            return;
        }
        if (this.landlordAddPropertyInterestRate.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRate.requestFocus();
            return;
        }
        if (this.landlordAddPropertyOutstandingAmount.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmount.requestFocus();
            return;
        }
        if (this.landlordAddPropertyTotalYear.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyTotalYearAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyTotalYear.requestFocus();
            return;
        }
        this.startLoadingSpinner();

        JSONObject postData = new JSONObject();
        try {
            postData.put("asset_nickname", this.landlordAddPropertyName.getText().toString());
            postData.put("number_of_rooms", Integer.parseInt(this.landlordAddPropertyNumberOfRooms.getText().toString()));
            postData.put("number_of_bathrooms", Integer.parseInt(this.landlordAddPropertyNumberOfBathrooms.getText().toString()));
            postData.put("asset_type", this.landlordAddPropertyType.getSelectedItemId() == 0 ? "RESIDENTIAL" : "COMMERCIAL");
            postData.put("asset_ownership_type", this.landlordAddPropertyOwnershipType.getSelectedItemId() == 0 ? "FREEHOLD" : "LEASEHOLD");
            postData.put("asset_unit_no", this.landlordAddPropertyUnitName.getText().toString());
            postData.put("asset_address_line", this.landlordAddPropertyAddressLine1.getText().toString());
            postData.put("asset_city", this.landlordAddPropertyCity.getText().toString());
            postData.put("asset_state", this.landlordAddPropertyState.getText().toString());
            postData.put("asset_postal_code", this.landlordAddPropertyPostcode.getText().toString());
            postData.put("asset_country", this.landlordAddPropertyCountry.getText().toString());
            postData.put("asset_purchased_value", Integer.parseInt(this.landlordAddPropertyPurchaseValue.getText().toString()));
            postData.put("asset_current_value", Integer.parseInt(this.landlordAddPropertyCurrentValue.getText().toString()));
            postData.put("purchased_date", this.landlordAddPropertyPurchaseDate.getText().toString());
            postData.put("loan_is_active", this.landlordAddPropertyIsActive.getSelectedItemId() == 0);
            postData.put("loan_interest_rate", Integer.parseInt(this.landlordAddPropertyInterestRate.getText().toString()));
            postData.put("loan_outstanding_amount", Integer.parseInt(this.landlordAddPropertyOutstandingAmount.getText().toString()));
            postData.put("loan_total_year", Integer.parseInt(this.landlordAddPropertyTotalYear.getText().toString()));
        } catch (JSONException e) {
            addPropertyFailed(Constants.ERROR_COMMON);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LANDLORD_ADD_PROPERTY, postData, response -> addPropertySuccess(), error -> {
            try {
                String errorResponseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorResponseBodyJsonObject = new JSONObject(errorResponseBody);
                if (errorResponseBodyJsonObject.has("message")) {
                    String errorMessage = errorResponseBodyJsonObject.getString("message");
                    if (errorMessage.equals("This action is unauthorized.")) {
                        addPropertyFailed(Constants.ERROR_USER_ADD_PROPERTY_LIMIT_FAILED);
                    } else {
                        addPropertyFailed(errorMessage);
                    }
                } else {
                    addPropertyFailed(Constants.ERROR_COMMON);
                }
            } catch (JSONException e) {
                addPropertyFailed(Constants.ERROR_COMMON);
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

    private void addPropertySuccess() {
        this.stopLoadingSpinner();
//        setResult(Activity.RESULT_OK);
        finish();
    }

    private void addPropertyFailed(String addPropertyFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("Add Property Failed");
        saveUserProfileFailedDialog.setMessage(addPropertyFailedCause);
        saveUserProfileFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        saveUserProfileFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.landlordAddPropertyButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordAddPropertyButton.setEnabled(true);
    }
}
