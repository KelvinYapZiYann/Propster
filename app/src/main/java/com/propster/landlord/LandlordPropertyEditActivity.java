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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LandlordPropertyEditActivity extends AppCompatActivity {

    private EditText landlordPropertyEditName;
    private TextView landlordPropertyEditNameAlert;
    private EditText landlordPropertyEditUnitName;
    private TextView landlordPropertyEditUnitNameAlert;
    private EditText landlordPropertyEditAddressLine1;
    private TextView landlordPropertyEditAddressLine1Alert;
    private EditText landlordPropertyEditCity;
    private TextView landlordPropertyEditCityAlert;
    private EditText landlordPropertyEditPostcode;
    private TextView landlordPropertyEditPostcodeAlert;
    private EditText landlordPropertyEditState;
    private TextView landlordPropertyEditStateAlert;
    private EditText landlordPropertyEditCountry;
    private TextView landlordPropertyEditCountryAlert;
    private EditText landlordPropertyEditNumberOfRooms;
    private TextView landlordPropertyEditNumberOfRoomsAlert;
    private EditText landlordPropertyEditNumberOfBathrooms;
    private TextView landlordPropertyEditNumberOfBathroomsAlert;
    private EditText landlordPropertyEditSize;
    private TextView landlordPropertyEditSizeAlert;
    private Spinner landlordPropertyEditType;
    private Spinner landlordPropertyEditOwnershipType;
    private EditText landlordPropertyEditPurchaseValue;
    private TextView landlordPropertyEditPurchaseValueAlert;
    private EditText landlordPropertyEditCurrentValue;
    private TextView landlordPropertyEditCurrentValueAlert;
    private EditText landlordPropertyEditPurchaseDate;
    private TextView landlordPropertyEditPurchaseDateAlert;
    private Spinner landlordPropertyEditIsActive;
    private EditText landlordPropertyEditInterestRate;
    private TextView landlordPropertyEditInterestRateAlert;
    private EditText landlordPropertyEditOutstandingAmount;
    private TextView landlordPropertyEditOutstandingAmountAlert;
    private EditText landlordPropertyEditTotalYear;
    private TextView landlordPropertyEditTotalYearAlert;

    private Button landlordPropertyEditSaveButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int propertyId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_edit);

        Bundle extras = getIntent().getExtras();
        String propertyName;
        if(extras == null) {
            this.propertyId = -1;
            propertyName = null;
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
        }

        this.landlordPropertyEditName = findViewById(R.id.landlordPropertyEditName);
        this.landlordPropertyEditNameAlert = findViewById(R.id.landlordPropertyEditNameAlert);
        this.landlordPropertyEditUnitName = findViewById(R.id.landlordPropertyEditUnitName);
        this.landlordPropertyEditUnitNameAlert = findViewById(R.id.landlordPropertyEditUnitNameAlert);
        this.landlordPropertyEditAddressLine1 = findViewById(R.id.landlordPropertyEditAddressLine1);
        this.landlordPropertyEditAddressLine1Alert = findViewById(R.id.landlordPropertyEditAddressLine1Alert);
        this.landlordPropertyEditCity = findViewById(R.id.landlordPropertyEditCity);
        this.landlordPropertyEditCityAlert = findViewById(R.id.landlordPropertyEditCityAlert);
        this.landlordPropertyEditPostcode = findViewById(R.id.landlordPropertyEditPostcode);
        this.landlordPropertyEditPostcodeAlert = findViewById(R.id.landlordPropertyEditPostcodeAlert);
        this.landlordPropertyEditState = findViewById(R.id.landlordPropertyEditState);
        this.landlordPropertyEditStateAlert = findViewById(R.id.landlordPropertyEditStateAlert);
        this.landlordPropertyEditCountry = findViewById(R.id.landlordPropertyEditCountry);
        this.landlordPropertyEditCountryAlert = findViewById(R.id.landlordPropertyEditCountryAlert);
        this.landlordPropertyEditNumberOfRooms = findViewById(R.id.landlordPropertyEditNumberOfRooms);
        this.landlordPropertyEditNumberOfRoomsAlert = findViewById(R.id.landlordPropertyEditNumberOfRoomsAlert);
        this.landlordPropertyEditNumberOfBathrooms = findViewById(R.id.landlordPropertyEditNumberOfBathrooms);
        this.landlordPropertyEditNumberOfBathroomsAlert = findViewById(R.id.landlordPropertyEditNumberOfBathroomsAlert);
        this.landlordPropertyEditSize = findViewById(R.id.landlordPropertyEditSize);
        this.landlordPropertyEditSizeAlert = findViewById(R.id.landlordPropertyEditSizeAlert);
        this.landlordPropertyEditType = findViewById(R.id.landlordPropertyEditType);
        this.landlordPropertyEditOwnershipType = findViewById(R.id.landlordPropertyEditOwnershipType);
        this.landlordPropertyEditPurchaseValue = findViewById(R.id.landlordPropertyEditPurchaseValue);
        this.landlordPropertyEditPurchaseValueAlert = findViewById(R.id.landlordPropertyEditPurchaseValueAlert);
        this.landlordPropertyEditCurrentValue = findViewById(R.id.landlordPropertyEditCurrentValue);
        this.landlordPropertyEditCurrentValueAlert = findViewById(R.id.landlordPropertyEditCurrentValueAlert);
        this.landlordPropertyEditPurchaseDate = findViewById(R.id.landlordPropertyEditPurchaseDate);
        this.landlordPropertyEditPurchaseDateAlert = findViewById(R.id.landlordPropertyEditPurchaseDateAlert);
        this.landlordPropertyEditIsActive = findViewById(R.id.landlordPropertyEditIsActive);
        this.landlordPropertyEditInterestRate = findViewById(R.id.landlordPropertyEditInterestRate);
        this.landlordPropertyEditInterestRateAlert = findViewById(R.id.landlordPropertyEditInterestRateAlert);
        this.landlordPropertyEditOutstandingAmount = findViewById(R.id.landlordPropertyEditOutstandingAmount);
        this.landlordPropertyEditOutstandingAmountAlert = findViewById(R.id.landlordPropertyEditOutstandingAmountAlert);
        this.landlordPropertyEditTotalYear = findViewById(R.id.landlordPropertyEditTotalYear);
        this.landlordPropertyEditTotalYearAlert = findViewById(R.id.landlordPropertyEditTotalYearAlert);

        this.backgroundView = findViewById(R.id.landlordPropertyEditBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyEditLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        ArrayAdapter<CharSequence> propertyTypeArrayAdapter = ArrayAdapter.createFromResource(this, R.array.property_type, R.layout.spinner_item);
        propertyTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordPropertyEditType.setAdapter(propertyTypeArrayAdapter);

        ArrayAdapter<CharSequence> ownershipTypeArrayAdapter = ArrayAdapter.createFromResource(this, R.array.ownership_type, R.layout.spinner_item);
        ownershipTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordPropertyEditOwnershipType.setAdapter(ownershipTypeArrayAdapter);

        ArrayAdapter<CharSequence> isActiveArrayAdapter = ArrayAdapter.createFromResource(this, R.array.loan_is_active, R.layout.spinner_item);
        isActiveArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.landlordPropertyEditIsActive.setAdapter(isActiveArrayAdapter);

        this.landlordPropertyEditSaveButton = findViewById(R.id.landlordPropertyEditSaveButton);
        this.landlordPropertyEditSaveButton.setOnClickListener(v -> doSaveProperty());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Calendar currentCalendar = new GregorianCalendar();
        this.landlordPropertyEditPurchaseDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(LandlordPropertyEditActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                landlordPropertyEditPurchaseDate.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        Toolbar mainToolbar = findViewById(R.id.landlordPropertyEditToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            if (propertyName != null) {
                getSupportActionBar().setTitle(propertyName);
            } else {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.mainMenuUser) {
//                Intent userProfileIntent = new Intent(LandlordPropertyEditActivity.this, UserProfileActivity.class);
//                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//            } else
            if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(LandlordPropertyEditActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshPropertyInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    private void doSaveProperty() {
        if (this.propertyId == -1) {
            editPropertyFailed(Constants.ERROR_COMMON);
            return;
        }
        if (this.landlordPropertyEditName.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditName.requestFocus();
            return;
        }
        if (this.landlordPropertyEditUnitName.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitName.requestFocus();
            return;
        }
        if (this.landlordPropertyEditAddressLine1.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1.requestFocus();
            return;
        }
        if (this.landlordPropertyEditCity.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCity.requestFocus();
            return;
        }
        if (this.landlordPropertyEditPostcode.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcode.requestFocus();
            return;
        }
        if (this.landlordPropertyEditState.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditState.requestFocus();
            return;
        }
        if (this.landlordPropertyEditCountry.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountry.requestFocus();
            return;
        }
        if (this.landlordPropertyEditNumberOfRooms.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRooms.requestFocus();
            return;
        }
        if (this.landlordPropertyEditNumberOfBathrooms.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathrooms.requestFocus();
            return;
        }
        if (this.landlordPropertyEditSize.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSize.requestFocus();
            return;
        }
        if (this.landlordPropertyEditPurchaseValue.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValue.requestFocus();
            return;
        }
        if (this.landlordPropertyEditCurrentValue.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValue.requestFocus();
            return;
        }
        if (this.landlordPropertyEditPurchaseDate.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDate.requestFocus();
            return;
        }
        if (this.landlordPropertyEditInterestRate.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRate.requestFocus();
            return;
        }
        if (this.landlordPropertyEditOutstandingAmount.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmount.requestFocus();
            return;
        }
        if (this.landlordPropertyEditTotalYear.length() <= 0) {
            this.landlordPropertyEditNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCityAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditStateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCountryAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfRoomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditNumberOfBathroomsAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditSizeAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditCurrentValueAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditPurchaseDateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditInterestRateAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditOutstandingAmountAlert.setVisibility(View.INVISIBLE);
            this.landlordPropertyEditTotalYearAlert.setVisibility(View.VISIBLE);
            this.landlordPropertyEditTotalYear.requestFocus();
            return;
        }
        this.startLoadingSpinner();

        JSONObject postData = new JSONObject();
        try {
            postData.put("asset_nickname", this.landlordPropertyEditName.getText().toString());
            postData.put("number_of_rooms", Integer.parseInt(this.landlordPropertyEditNumberOfRooms.getText().toString()));
            postData.put("number_of_bathrooms", Integer.parseInt(this.landlordPropertyEditNumberOfBathrooms.getText().toString()));
            postData.put("asset_size", this.landlordPropertyEditSize.getText().toString());
            postData.put("asset_type", this.landlordPropertyEditType.getSelectedItemId() == 0 ? "RESIDENTIAL" : "COMMERCIAL");
            postData.put("asset_ownership_type", this.landlordPropertyEditOwnershipType.getSelectedItemId() == 0 ? "FREEHOLD" : "LEASEHOLD");
            postData.put("asset_unit_no", this.landlordPropertyEditUnitName.getText().toString());
            postData.put("asset_address_line", this.landlordPropertyEditAddressLine1.getText().toString());
            postData.put("asset_city", this.landlordPropertyEditCity.getText().toString());
            postData.put("asset_state", this.landlordPropertyEditState.getText().toString());
            postData.put("asset_postal_code", this.landlordPropertyEditPostcode.getText().toString());
            postData.put("asset_country", "MY");
//            postData.put("asset_country", this.landlordPropertyEditCountry.getText().toString());
            postData.put("asset_purchased_value", Integer.parseInt(this.landlordPropertyEditPurchaseValue.getText().toString()));
            postData.put("asset_current_value", Integer.parseInt(this.landlordPropertyEditCurrentValue.getText().toString()));
            String purchaseDate = this.landlordPropertyEditPurchaseDate.getText().toString();
            postData.put("purchased_date", purchaseDate.length() > 10 ? purchaseDate.substring(0, 10) : purchaseDate);
            postData.put("loan_is_active", this.landlordPropertyEditIsActive.getSelectedItemId() == 0);
            postData.put("loan_interest_rate", this.landlordPropertyEditInterestRate.getText().toString());
            postData.put("loan_outstanding_amount", Integer.parseInt(this.landlordPropertyEditOutstandingAmount.getText().toString()));
            postData.put("loan_total_year", Integer.parseInt(this.landlordPropertyEditTotalYear.getText().toString()));
        } catch (JSONException e) {
            editPropertyFailed(Constants.ERROR_COMMON);
        }
        System.out.println("postData = " + postData.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Constants.URL_LANDLORD_PROPERTY + "/" + this.propertyId, postData, response -> editPropertySuccess(), error -> {
            try {
                String errorResponseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorResponseBodyJsonObject = new JSONObject(errorResponseBody);
                if (errorResponseBodyJsonObject.has("message")) {
                    String errorMessage = errorResponseBodyJsonObject.getString("message");
                    if (errorMessage.equals("This action is unauthorized.")) {
                        editPropertyFailed(Constants.ERROR_COMMON);
                    } else {
                        editPropertyFailed(errorMessage);
                    }
                } else {
                    editPropertyFailed(Constants.ERROR_COMMON);
                }
            } catch (JSONException e) {
                editPropertyFailed(Constants.ERROR_COMMON);
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

    private void editPropertySuccess() {
        this.stopLoadingSpinner();
        finish();
    }

    private void editPropertyFailed(String addPropertyFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("Asset Edit Failed");
        saveUserProfileFailedDialog.setMessage(addPropertyFailedCause);
        saveUserProfileFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        saveUserProfileFailedDialog.create().show();
    }

    private void refreshPropertyInfo() {
        if (this.propertyId == -1) {
            getPropertyInfoFailed(Constants.ERROR_COMMON);
            return;
        }
        this.startLoadingSpinner();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY + "/" + this.propertyId, null, response -> {
            try {
                if (!response.has("data")) {
                    getPropertyInfoFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (!dataJsonObject.has("id")) {
                    getPropertyInfoFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (dataJsonObject.getInt("id") != this.propertyId) {
                    getPropertyInfoFailed(Constants.ERROR_USER_PROPERTY_DETAIL_ID_NOT_MATCHED);
                    return;
                }
                JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                this.landlordPropertyEditName.setText(dataFieldsJsonObject.getString("asset_nickname"));
                JSONObject dataFieldsLocationJsonObject = dataFieldsJsonObject.getJSONObject("location_details");
                this.landlordPropertyEditUnitName.setText(dataFieldsLocationJsonObject.getString("asset_unit_no"));
                this.landlordPropertyEditAddressLine1.setText(dataFieldsLocationJsonObject.getString("asset_address_line"));
                this.landlordPropertyEditCity.setText(dataFieldsLocationJsonObject.getString("asset_city"));
                this.landlordPropertyEditState.setText(dataFieldsLocationJsonObject.getString("asset_state"));
                this.landlordPropertyEditPostcode.setText(dataFieldsLocationJsonObject.getString("asset_postal_code"));
                this.landlordPropertyEditCountry.setText(dataFieldsLocationJsonObject.getString("asset_country").equalsIgnoreCase("MY") ? "Malaysia" : dataFieldsLocationJsonObject.getString("asset_country"));
                this.landlordPropertyEditNumberOfRooms.setText(Integer.toString(dataFieldsJsonObject.getInt("number_of_rooms")));
                this.landlordPropertyEditNumberOfBathrooms.setText(Integer.toString(dataFieldsJsonObject.getInt("number_of_bathrooms")));
                this.landlordPropertyEditSize.setText(dataFieldsJsonObject.getString("asset_size"));
                if (dataFieldsJsonObject.getString("asset_type").equalsIgnoreCase("RESIDENTIAL")) {
                    this.landlordPropertyEditType.setSelection(0);
                } else {
                    this.landlordPropertyEditType.setSelection(1);
                }
                if (dataFieldsJsonObject.getString("asset_ownership_type").equalsIgnoreCase("FREEHOLD")) {
                    this.landlordPropertyEditOwnershipType.setSelection(0);
                } else {
                    this.landlordPropertyEditOwnershipType.setSelection(1);
                }
                JSONObject dataFieldsFinancialJsonObject = dataFieldsJsonObject.getJSONObject("financial_details");
                this.landlordPropertyEditPurchaseValue.setText(dataFieldsFinancialJsonObject.getString("asset_purchased_value"));
                this.landlordPropertyEditCurrentValue.setText(dataFieldsFinancialJsonObject.getString("asset_current_value"));
                String purchaseDate = dataFieldsFinancialJsonObject.getString("purchased_date");
                this.landlordPropertyEditPurchaseDate.setText(purchaseDate.length() > 10 ? purchaseDate.substring(0, 10) : purchaseDate);
                this.landlordPropertyEditIsActive.setSelection(dataFieldsFinancialJsonObject.getInt("loan_is_active") == 1 ? 0 : 1);
                this.landlordPropertyEditInterestRate.setText(dataFieldsFinancialJsonObject.getString("loan_interest_rate"));
                this.landlordPropertyEditOutstandingAmount.setText(dataFieldsFinancialJsonObject.getString("loan_outstanding_amount"));
                this.landlordPropertyEditTotalYear.setText(dataFieldsFinancialJsonObject.getString("loan_total_year"));
                this.getPropertyInfoSuccess();
            } catch (JSONException e) {
                e.printStackTrace();
                getPropertyInfoFailed(Constants.ERROR_USER_PROPERTY_DETAIL_ID_NOT_MATCHED);
            }
        }, error -> {
            try {
                String errorResponseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorResponseBodyJsonObject = new JSONObject(errorResponseBody);
                if (!errorResponseBodyJsonObject.has("message")) {
                    getPropertyInfoFailed(Constants.ERROR_COMMON);
                    return;
                }
                getPropertyInfoFailed(errorResponseBodyJsonObject.getString("message"));
            } catch (Exception e) {
                getPropertyInfoFailed(Constants.ERROR_COMMON);
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

    private void getPropertyInfoSuccess() {
        this.stopLoadingSpinner();
    }

    private void getPropertyInfoFailed(String landlordGetPropertyDetailFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Asset Info Failed");
        loginFailedDialog.setMessage(landlordGetPropertyDetailFailed);
        loginFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        loginFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.landlordPropertyEditSaveButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordPropertyEditSaveButton.setEnabled(true);
    }


}
