package com.propster.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.propster.R;
import com.propster.login.LoginActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private ShapeableImageView userProfileImage;

    private EditText userProfileEmail;
//    private TextView userProfileEmailAlert;
    private EditText userProfilePhoneNumber;
//    private TextView userProfilePhoneNumberAlert;
    private EditText userProfileGender;
    private EditText userProfileFirstName;
//    private TextView userProfileFirstNameAlert;
    private EditText userProfileLastName;
//    private TextView userProfileLastNameAlert;
    private EditText userProfileDateOfBirth;
//    private TextView userProfileDateOfBirthAlert;
    private EditText userProfileIsBusiness;

    private Button userProfileEditButton;
//    private FabOption userProfileSwitchRoleButton;
//    private FabOption userProfileLogoutButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        this.userProfileImage = findViewById(R.id.userProfileImage);

        this.userProfileEmail = findViewById(R.id.userProfileEmail);
        this.userProfilePhoneNumber = findViewById(R.id.userProfilePhoneNumber);
        this.userProfileFirstName = findViewById(R.id.userProfileFirstName);
        this.userProfileLastName = findViewById(R.id.userProfileLastName);
        this.userProfileGender = findViewById(R.id.userProfileGender);
        this.userProfileDateOfBirth = findViewById(R.id.userProfileDateOfBirth);
        this.userProfileIsBusiness = findViewById(R.id.userProfileIsBusiness);

        this.backgroundView = findViewById(R.id.userProfileBackground);
        this.loadingSpinner = findViewById(R.id.userProfileLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
//        Calendar currentCalendar = new GregorianCalendar();
//        this.userProfileDateOfBirth.setOnClickListener(v -> {
//            DatePickerDialog datePickerDialog = new DatePickerDialog(UserProfileActivity.this, (view, year, month, dayOfMonth) -> {
//                currentCalendar.set(Calendar.YEAR, year);
//                currentCalendar.set(Calendar.MONTH, month);
//                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                userProfileDateOfBirth.setText(sdf.format(currentCalendar.getTime()));
//            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
//            datePickerDialog.show();
//        });

//        this.userProfileImage.setOnClickListener(view -> doChooseImage());

        this.userProfileEditButton = findViewById(R.id.userProfileEditButton);
        this.userProfileEditButton.setOnClickListener(view -> doEditUserProfile());

//        this.userProfileSwitchRoleButton = findViewById(R.id.userProfileSwitchRoleButton);
//        this.userProfileSwitchRoleButton.setOnClickListener(v -> doSwitchRole());
//
//        this.userProfileLogoutButton = findViewById(R.id.userProfileLogoutButton);
//        this.userProfileLogoutButton.setOnClickListener(v -> doLogout());

        Toolbar userProfileToolbar = findViewById(R.id.userProfileToolbar);
        setSupportActionBar(userProfileToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        userProfileToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.userProfileMenuNotification) {
                Intent notificationIntent = new Intent(UserProfileActivity.this, NotificationActivity.class);
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
            JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
            this.userProfileEmail.setText(dataFieldsJsonObject.getString("email"));
            this.userProfilePhoneNumber.setText(dataFieldsJsonObject.getString("phone_number"));
            this.userProfileFirstName.setText(dataFieldsJsonObject.getString("first_name"));
            this.userProfileLastName.setText(dataFieldsJsonObject.getString("last_name"));
            this.userProfileGender.setText(dataFieldsJsonObject.getString("gender"));
            this.userProfileIsBusiness.setText(dataFieldsJsonObject.getBoolean("is_business") ? "COMMERCIAL" : "RESIDENTIAL");
            String dateOfBirth = dataFieldsJsonObject.getString("date_of_birth");
            this.userProfileDateOfBirth.setText(dateOfBirth.length() > 10 ? dateOfBirth.substring(0, 10) : dateOfBirth);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_FIRST_LOGIN_USER_PROFILE_IMAGE) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                this.userProfileImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == Constants.REQUEST_CODE_USER_PROFILE_DETAIL) {
            this.refreshUserProfile();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_profile_toolbar, menu);
        return true;
    }

    private void doEditUserProfile() {
        Intent userProfileEditIntent = new Intent(this, UserProfileEditActivity.class);
        startActivityForResult(userProfileEditIntent, Constants.REQUEST_CODE_USER_PROFILE_DETAIL);
//        if (this.userProfileEmail.length() <= 0) {
//            this.userProfileEmailAlert.setVisibility(View.VISIBLE);
//            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
//            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
//            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
//            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
//            this.userProfileEmail.requestFocus();
//            return;
//        }
//        if (this.userProfilePhoneNumber.length() <= 0) {
//            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
//            this.userProfilePhoneNumberAlert.setVisibility(View.VISIBLE);
//            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
//            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
//            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
//            this.userProfilePhoneNumber.requestFocus();
//            return;
//        }
//        if (this.userProfileFirstName.length() <= 0) {
//            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
//            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
//            this.userProfileFirstNameAlert.setVisibility(View.VISIBLE);
//            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
//            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
//            this.userProfileFirstName.requestFocus();
//            return;
//        }
//        if (this.userProfileLastName.length() <= 0) {
//            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
//            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
//            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
//            this.userProfileLastNameAlert.setVisibility(View.VISIBLE);
//            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
//            this.userProfileLastName.requestFocus();
//            return;
//        }
//        if (this.userProfileDateOfBirth.length() <= 0) {
//            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
//            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
//            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
//            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
//            this.userProfileDateOfBirthAlert.setVisibility(View.VISIBLE);
//            this.userProfileLastName.requestFocus();
//            return;
//        }
//
//        this.startLoadingSpinner();

//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("email", this.userProfileEmail.getText().toString());
//            postData.put("phone_number", this.userProfilePhoneNumber.getText().toString());
//            postData.put("title", (String) this.userProfileTitle.getSelectedItem());
//            postData.put("first_name", this.userProfileFirstName.getText().toString());
//            postData.put("last_name", this.userProfileLastName.getText().toString());
//            postData.put("date_of_birth", this.userProfileDateOfBirth.getText().toString());
//            postData.put("is_business", this.userProfileIsBusiness.getSelectedItemId() != 0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_SAVE_USER_PROFILE, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//                saveUserProfileSuccess();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                saveUserProfileFailed(error.getLocalizedMessage());
//                error.printStackTrace();
//            }
//        });
//        this.requestQueue.add(jsonObjectRequest);
//        saveUserProfileSuccess();
    }

//    private void saveUserProfileSuccess() {
//        this.stopLoadingSpinner();
//        Intent roleSelectionIntent = new Intent(this, FirstTimeRoleSelectionActivity.class);
//        startActivity(roleSelectionIntent);
//        finish();
//    }
//
//    private void saveUserProfileFailed(String saveUserProfileFailedCause) {
//        this.stopLoadingSpinner();
//        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
//        saveUserProfileFailedDialog.setCancelable(false);
//        saveUserProfileFailedDialog.setTitle("User Profile Failed");
//        saveUserProfileFailedDialog.setMessage(saveUserProfileFailedCause);
//        saveUserProfileFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
//        saveUserProfileFailedDialog.create().show();
//    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.userProfileEditButton.setEnabled(false);
//        this.userProfileSwitchRoleButton.setEnabled(false);
//        this.userProfileLogoutButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.userProfileEditButton.setEnabled(true);
//        this.userProfileSwitchRoleButton.setEnabled(true);
//        this.userProfileLogoutButton.setEnabled(true);
    }
}
