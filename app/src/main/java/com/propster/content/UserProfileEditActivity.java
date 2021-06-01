package com.propster.content;

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
import com.google.android.material.imageview.ShapeableImageView;
import com.propster.R;
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

public class UserProfileEditActivity extends AppCompatActivity {

    private ShapeableImageView userProfileEditImage;

    private EditText userProfileEditEmail;
    private TextView userProfileEditEmailAlert;
    private EditText userProfileEditPhoneNumber;
    private TextView userProfileEditPhoneNumberAlert;
    private Spinner userProfileEditGender;
    private EditText userProfileEditFirstName;
    private TextView userProfileEditFirstNameAlert;
    private EditText userProfileEditLastName;
    private TextView userProfileEditLastNameAlert;
    private EditText userProfileEditDateOfBirth;
    private TextView userProfileEditDateOfBirthAlert;
    private Spinner userProfileEditIsBusiness;

    private Button userProfileEditSaveButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int userId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        this.userProfileEditImage = findViewById(R.id.userProfileEditImage);

        this.userProfileEditEmail = findViewById(R.id.userProfileEditEmail);
        this.userProfileEditEmailAlert = findViewById(R.id.userProfileEditEmailAlert);
        this.userProfileEditPhoneNumber = findViewById(R.id.userProfileEditPhoneNumber);
        this.userProfileEditPhoneNumberAlert = findViewById(R.id.userProfileEditPhoneNumberAlert);
        this.userProfileEditFirstName = findViewById(R.id.userProfileEditFirstName);
        this.userProfileEditFirstNameAlert = findViewById(R.id.userProfileEditFirstNameAlert);
        this.userProfileEditLastName = findViewById(R.id.userProfileEditLastName);
        this.userProfileEditLastNameAlert = findViewById(R.id.userProfileEditLastNameAlert);
        this.userProfileEditGender = findViewById(R.id.userProfileEditGender);
        this.userProfileEditDateOfBirth = findViewById(R.id.userProfileEditDateOfBirth);
        this.userProfileEditDateOfBirthAlert = findViewById(R.id.userProfileEditDateOfBirthAlert);
        this.userProfileEditIsBusiness = findViewById(R.id.userProfileEditIsBusiness);

        this.backgroundView = findViewById(R.id.userProfileEditBackground);
        this.loadingSpinner = findViewById(R.id.userProfileEditLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        ArrayAdapter<CharSequence> genderArrayAdapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.spinner_item);
        genderArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.userProfileEditGender.setAdapter(genderArrayAdapter);

        ArrayAdapter<CharSequence> isBusinessArrayAdapter = ArrayAdapter.createFromResource(this, R.array.is_business, R.layout.spinner_item);
        isBusinessArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.userProfileEditIsBusiness.setAdapter(isBusinessArrayAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Date currentDate = new Date();
        Calendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTime(currentDate);
        this.userProfileEditDateOfBirth.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(UserProfileEditActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                userProfileEditDateOfBirth.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

//        this.userProfileEditImage.setOnClickListener(view -> doChooseImage());

        this.userProfileEditSaveButton = findViewById(R.id.userProfileEditSaveButton);
        this.userProfileEditSaveButton.setOnClickListener(v -> this.doSaveUserProfile());

        Toolbar userProfileToolbar = findViewById(R.id.userProfileEditToolbar);
        setSupportActionBar(userProfileToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        userProfileToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.userProfileMenuNotification) {
                Intent notificationIntent = new Intent(UserProfileEditActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
            }
            return false;
        });
        userProfileToolbar.setNavigationOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });

        this.refreshUserProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_profile_toolbar, menu);
        return true;
    }

    private void refreshUserProfile() {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getUserProfileFailed("Please relogin.");
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_USER, null, response -> getUserProfileSuccess(response),
                error -> getUserProfileFailed(Constants.ERROR_COMMON)) {
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

    private void doSaveUserProfile() {
        if (this.userId == -1) {
            saveUserProfileFailed(Constants.ERROR_COMMON);
            return;
        }
//        if (this.userProfileEditEmail.length() <= 0) {
//            this.userProfileEditEmailAlert.setVisibility(View.VISIBLE);
//            this.userProfileEditPhoneNumberAlert.setVisibility(View.INVISIBLE);
//            this.userProfileEditFirstNameAlert.setVisibility(View.INVISIBLE);
//            this.userProfileEditLastNameAlert.setVisibility(View.INVISIBLE);
//            this.userProfileEditDateOfBirthAlert.setVisibility(View.INVISIBLE);
//            this.userProfileEditEmail.requestFocus();
//            return;
//        }
        if (this.userProfileEditPhoneNumber.length() <= 0) {
            this.userProfileEditEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditPhoneNumberAlert.setVisibility(View.VISIBLE);
            this.userProfileEditFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditPhoneNumber.requestFocus();
            return;
        }
        if (this.userProfileEditFirstName.length() <= 0) {
            this.userProfileEditEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditPhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditFirstNameAlert.setVisibility(View.VISIBLE);
            this.userProfileEditLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditFirstName.requestFocus();
            return;
        }
        if (this.userProfileEditLastName.length() <= 0) {
            this.userProfileEditEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditPhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditLastNameAlert.setVisibility(View.VISIBLE);
            this.userProfileEditDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditLastName.requestFocus();
            return;
        }
        if (this.userProfileEditDateOfBirth.length() <= 0) {
            this.userProfileEditEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditPhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileEditDateOfBirthAlert.setVisibility(View.VISIBLE);
            this.userProfileEditLastName.requestFocus();
            return;
        }

        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.saveUserProfileFailed("Please relogin.");
            return;
        }
        JSONObject postData = new JSONObject();
        try {
//            postData.put("email", this.userProfileEditEmail.getText().toString());
            postData.put("phone_country_code", "60");
            postData.put("phone_number", this.userProfileEditPhoneNumber.getText().toString());
            postData.put("gender", this.userProfileEditGender.getSelectedItemPosition() == 0 ? "MALE" : "FEMALE");
            postData.put("first_name", this.userProfileEditFirstName.getText().toString());
            postData.put("last_name", this.userProfileEditLastName.getText().toString());
            postData.put("date_of_birth", this.userProfileEditDateOfBirth.getText().toString());
            postData.put("is_business", this.userProfileEditIsBusiness.getSelectedItemId() != 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Constants.URL_USER + "/" + this.userId, postData, response -> saveUserProfileSuccess(), error -> {
            try {
                System.out.println("error = " + new String(error.networkResponse.data));
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveUserProfileFailed(Constants.ERROR_COMMON);
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

    private void saveUserProfileSuccess() {
        this.stopLoadingSpinner();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void saveUserProfileFailed(String getTenantDetailFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("Save User Profile Failed");
        saveUserProfileFailedDialog.setMessage(getTenantDetailFailedCause);
        saveUserProfileFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        saveUserProfileFailedDialog.create().show();
    }

    private void doChooseImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, Constants.REQUEST_CODE_FIRST_LOGIN_USER_PROFILE_IMAGE);
    }

    private void getUserProfileSuccess(JSONObject response) {
        try {
            if (!response.has("data")) {
                getUserProfileFailed(Constants.ERROR_COMMON);
                return;
            }
            JSONObject dataJsonObject = response.getJSONObject("data");
            if (!dataJsonObject.has("id")) {
                getUserProfileFailed(Constants.ERROR_COMMON);
            }
            this.userId = dataJsonObject.getInt("id");
            JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
            this.userProfileEditEmail.setText(dataFieldsJsonObject.getString("email"));
            this.userProfileEditPhoneNumber.setText(dataFieldsJsonObject.getString("phone_number"));
            this.userProfileEditFirstName.setText(dataFieldsJsonObject.getString("first_name"));
            this.userProfileEditLastName.setText(dataFieldsJsonObject.getString("last_name"));
            this.userProfileEditGender.setSelection(dataFieldsJsonObject.getString("gender").equalsIgnoreCase("male") ? 0 : 1);
            this.userProfileEditIsBusiness.setSelection(dataFieldsJsonObject.getBoolean("is_business") ? 1 : 0);
            String dateOfBirth = dataFieldsJsonObject.getString("date_of_birth");
            this.userProfileEditDateOfBirth.setText(dateOfBirth.length() > 10 ? dateOfBirth.substring(0, 10) : dateOfBirth);
            getSupportActionBar().setTitle(dataFieldsJsonObject.getString("full_name"));
            this.stopLoadingSpinner();
        } catch (JSONException e) {
            e.printStackTrace();
            this.getUserProfileFailed(Constants.ERROR_COMMON);
        }
    }

    private void getUserProfileFailed(String getTenantDetailFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("User Profile Failed");
        saveUserProfileFailedDialog.setMessage(getTenantDetailFailedCause);
        saveUserProfileFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        saveUserProfileFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.userProfileEditSaveButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.userProfileEditSaveButton.setEnabled(true);
    }

}
