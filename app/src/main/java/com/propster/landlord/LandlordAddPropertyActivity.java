package com.propster.landlord;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.propster.content.ContentActivity;
import com.propster.content.NotificationActivity;
import com.propster.content.UserProfileActivity;
import com.propster.login.FirstTimeRoleSelectionActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class LandlordAddPropertyActivity extends AppCompatActivity {

    private EditText landlordAddPropertyName;
    private TextView landlordAddPropertyNameAlert;
    private EditText landlordAddPropertyUnitName;
    private TextView landlordAddPropertyUnitNameAlert;
    private EditText landlordAddPropertyFloor;
    private EditText landlordAddPropertyAddressLine1;
    private TextView landlordAddPropertyAddressLine1Alert;
    private EditText landlordAddPropertyAddressLine2;
    private EditText landlordAddPropertyCity;
    private TextView landlordAddPropertyCityAlert;
    private EditText landlordAddPropertyPostcode;
    private TextView landlordAddPropertyPostcodeAlert;
    private EditText landlordAddPropertyState;
    private TextView landlordAddPropertyStateAlert;
    private EditText landlordAddPropertyCountry;
    private TextView landlordAddPropertyCountryAlert;

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
        this.landlordAddPropertyFloor = findViewById(R.id.landlordAddPropertyFloor);
        this.landlordAddPropertyAddressLine1 = findViewById(R.id.landlordAddPropertyAddressLine1);
        this.landlordAddPropertyAddressLine1Alert = findViewById(R.id.landlordAddPropertyAddressLine1Alert);
        this.landlordAddPropertyAddressLine2 = findViewById(R.id.landlordAddPropertyAddressLine2);
        this.landlordAddPropertyCity = findViewById(R.id.landlordAddPropertyCity);
        this.landlordAddPropertyCityAlert = findViewById(R.id.landlordAddPropertyCityAlert);
        this.landlordAddPropertyPostcode = findViewById(R.id.landlordAddPropertyPostcode);
        this.landlordAddPropertyPostcodeAlert = findViewById(R.id.landlordAddPropertyPostcodeAlert);
        this.landlordAddPropertyState = findViewById(R.id.landlordAddPropertyState);
        this.landlordAddPropertyStateAlert = findViewById(R.id.landlordAddPropertyStateAlert);
        this.landlordAddPropertyCountry = findViewById(R.id.landlordAddPropertyCountry);
        this.landlordAddPropertyCountryAlert = findViewById(R.id.landlordAddPropertyCountryAlert);

        this.backgroundView = findViewById(R.id.landlordAddPropertyBackground);
        this.loadingSpinner = findViewById(R.id.landlordAddPropertyLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.landlordAddPropertyButton = findViewById(R.id.landlordAddPropertyButton);
        this.landlordAddPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAddProperty();
            }
        });

        Toolbar mainToolbar = findViewById(R.id.landlordAddPropertyToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.mainMenuUser) {
                    Intent userProfileIntent = new Intent(LandlordAddPropertyActivity.this, UserProfileActivity.class);
                    startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
                } else if (item.getItemId() == R.id.mainMenuNotification) {
                    Intent notificationIntent = new Intent(LandlordAddPropertyActivity.this, NotificationActivity.class);
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

    private void doAddProperty() {
        if (this.landlordAddPropertyName.length() <= 0) {
            this.landlordAddPropertyNameAlert.setVisibility(View.VISIBLE);
            this.landlordAddPropertyUnitNameAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyAddressLine1Alert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCityAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyPostcodeAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyStateAlert.setVisibility(View.INVISIBLE);
            this.landlordAddPropertyCountryAlert.setVisibility(View.INVISIBLE);
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
            this.landlordAddPropertyCountry.requestFocus();
            return;
        }
        this.startLoadingSpinner();

//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("name", this.landlordAddPropertyName.getText().toString());
//            postData.put("unit", this.landlordAddPropertyUnitName.getText().toString());
//            postData.put("floor", this.landlordAddPropertyFloor.getText().toString());
//            postData.put("address_line_1", this.landlordAddPropertyAddressLine1.getText().toString());
//            postData.put("address_line_2", this.landlordAddPropertyAddressLine2.getText().toString());
//            postData.put("city", this.landlordAddPropertyCity.getText().toString());
//            postData.put("postcode", this.landlordAddPropertyPostcode.getText().toString());
//            postData.put("state", this.landlordAddPropertyState.getText().toString());
//            postData.put("country", this.landlordAddPropertyCountry.getText().toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LANDLORD_ADD_PROPERTY, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//                addPropertySuccess();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                addPropertyFailed(error.getLocalizedMessage());
//                error.printStackTrace();
//            }
//        });
//        this.requestQueue.add(jsonObjectRequest);
        addPropertySuccess();

    }

    private void addPropertySuccess() {
        this.stopLoadingSpinner();
//        Intent data = new Intent();
//        data.putExtra(Constants.INTENT_EXTRA_LANDLORD_ADD_PROPERTY, false);
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void addPropertyFailed(String addPropertyFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("Add Property Failed");
        saveUserProfileFailedDialog.setMessage(addPropertyFailedCause);
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
        this.landlordAddPropertyButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordAddPropertyButton.setEnabled(true);
    }
}
