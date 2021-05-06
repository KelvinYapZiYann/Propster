package com.propster.content;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.imageview.ShapeableImageView;
import com.propster.R;
import com.propster.login.FirstTimeRoleSelectionActivity;
import com.propster.login.LoginActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private ShapeableImageView userProfileImage;

    private EditText userProfileEmail;
    private TextView userProfileEmailAlert;
    private EditText userProfilePhoneNumber;
    private TextView userProfilePhoneNumberAlert;
    private Spinner userProfileTitle;
    private EditText userProfileFirstName;
    private TextView userProfileFirstNameAlert;
    private EditText userProfileLastName;
    private TextView userProfileLastNameAlert;
    private EditText userProfileDateOfBirth;
    private TextView userProfileDateOfBirthAlert;
    private Spinner userProfileIsBusiness;

    private Button userProfileSaveButton;
    private Button userProfileSwitchRoleButton;
    private Button userProfileLogoutButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        this.userProfileImage = findViewById(R.id.userProfileImage);

        this.userProfileEmail = findViewById(R.id.userProfileEmail);
        this.userProfileEmailAlert = findViewById(R.id.userProfileEmailAlert);
        this.userProfilePhoneNumber = findViewById(R.id.userProfilePhoneNumber);
        this.userProfilePhoneNumberAlert = findViewById(R.id.userProfilePhoneNumberAlert);
        this.userProfileTitle = findViewById(R.id.userProfileTitle);
        this.userProfileFirstName = findViewById(R.id.userProfileFirstName);
        this.userProfileFirstNameAlert = findViewById(R.id.userProfileFirstNameAlert);
        this.userProfileLastName = findViewById(R.id.userProfileLastName);
        this.userProfileLastNameAlert = findViewById(R.id.userProfileLastNameAlert);
        this.userProfileDateOfBirth = findViewById(R.id.userProfileDateOfBirth);
        this.userProfileDateOfBirthAlert = findViewById(R.id.userProfileDateOfBirthAlert);
        this.userProfileIsBusiness = findViewById(R.id.userProfileIsBusiness);

        this.backgroundView = findViewById(R.id.userProfileBackground);
        this.loadingSpinner = findViewById(R.id.userProfileLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        ArrayAdapter<CharSequence> titleArrayAdapter = ArrayAdapter.createFromResource(this, R.array.title, R.layout.spinner_item);
        titleArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.userProfileTitle.setAdapter(titleArrayAdapter);

        ArrayAdapter<CharSequence> isBusinessArrayAdapter = ArrayAdapter.createFromResource(this, R.array.is_business, R.layout.spinner_item);
        isBusinessArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.userProfileIsBusiness.setAdapter(isBusinessArrayAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        Calendar currentCalendar = new GregorianCalendar();
        this.userProfileDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(UserProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        currentCalendar.set(Calendar.YEAR, year);
                        currentCalendar.set(Calendar.MONTH, month);
                        currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        userProfileDateOfBirth.setText(sdf.format(currentCalendar.getTime()));
                    }
                }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        this.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doChooseImage();
            }
        });

        this.userProfileSaveButton = findViewById(R.id.userProfileSaveButton);
        this.userProfileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSaveUserProfile();
            }
        });

        this.userProfileSwitchRoleButton = findViewById(R.id.userProfileSwitchRoleButton);
        this.userProfileSwitchRoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSwitchRole();
            }
        });

        this.userProfileLogoutButton = findViewById(R.id.userProfileLogoutButton);
        this.userProfileLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogout();
            }
        });

        Toolbar userProfileToolbar = findViewById(R.id.userProfileToolbar);
        setSupportActionBar(userProfileToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        userProfileToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.userProfileMenuNotification) {
                    Intent notificationIntent = new Intent(UserProfileActivity.this, NotificationActivity.class);
                    startActivity(notificationIntent);
                }
                return false;
            }
        });
        userProfileToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int role = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
        if (role == Constants.ROLE_TENANT) {
            this.userProfileSwitchRoleButton.setText(R.string.switch_role_to_landlord);
        } else {
            this.userProfileSwitchRoleButton.setText(R.string.switch_role_to_tenant);
        }
    }

    private void doSwitchRole() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int role = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
        if (role == Constants.ROLE_LANDLORD) {
            editor.putInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
        } else {
            editor.putInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_LANDLORD);
        }
        editor.apply();
        setResult(Activity.RESULT_OK);
        finish();
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_profile_toolbar, menu);
        return true;
    }

    private void doSaveUserProfile() {
        if (this.userProfileEmail.length() <= 0) {
            this.userProfileEmailAlert.setVisibility(View.VISIBLE);
            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfileEmail.requestFocus();
            return;
        }
        if (this.userProfilePhoneNumber.length() <= 0) {
            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfilePhoneNumberAlert.setVisibility(View.VISIBLE);
            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfilePhoneNumber.requestFocus();
            return;
        }
        if (this.userProfileFirstName.length() <= 0) {
            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileFirstNameAlert.setVisibility(View.VISIBLE);
            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfileFirstName.requestFocus();
            return;
        }
        if (this.userProfileLastName.length() <= 0) {
            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileLastNameAlert.setVisibility(View.VISIBLE);
            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfileLastName.requestFocus();
            return;
        }
        if (this.userProfileDateOfBirth.length() <= 0) {
            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileDateOfBirthAlert.setVisibility(View.VISIBLE);
            this.userProfileLastName.requestFocus();
            return;
        }

        this.startLoadingSpinner();

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
        saveUserProfileSuccess();
    }

    private void doLogout() {
        this.startLoadingSpinner();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LOGOUT, null, response -> logoutSuccess(), error -> logoutSuccess()) {
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

    private void logoutSuccess() {
        this.stopLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.SHARED_PREFERENCES_EMAIL);
        editor.remove(Constants.SHARED_PREFERENCES_PASSWORD);
        editor.remove(Constants.SHARED_PREFERENCES_SESSION_ID);
        editor.apply();
        SplashActivity.SESSION_ID = "";
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void saveUserProfileSuccess() {
        this.stopLoadingSpinner();
        Intent roleSelectionIntent = new Intent(this, FirstTimeRoleSelectionActivity.class);
        startActivity(roleSelectionIntent);
        finish();
    }

    private void saveUserProfileFailed(String saveUserProfileFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("User Profile Failed");
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
        this.userProfileSaveButton.setEnabled(false);
        this.userProfileSwitchRoleButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.userProfileSaveButton.setEnabled(true);
        this.userProfileSwitchRoleButton.setEnabled(true);
    }
}
