package com.propster.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.propster.R;
import com.propster.content.ContentActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FirstTimeRoleSelectionActivity extends AppCompatActivity {

    private Button roleSelectionLandlordButton;
    private Button roleSelectionTenantButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_role_selection);

        this.backgroundView = findViewById(R.id.firstTimeRoleSelectionBackground);
        this.loadingSpinner = findViewById(R.id.firstTimeRoleSelectionLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.roleSelectionLandlordButton = findViewById(R.id.firstTimeRoleSelectionLandlordButton);
        this.roleSelectionLandlordButton.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_LANDLORD);
            editor.apply();
            doSelectUserRole(Constants.ROLE_LANDLORD);
        });

        this.roleSelectionTenantButton = findViewById(R.id.firstTimeRoleSelectionTenantButton);
        this.roleSelectionTenantButton.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
            editor.apply();
            doSelectUserRole(Constants.ROLE_TENANT);
        });

        Toolbar secondaryToolbar = findViewById(R.id.firstTimeRoleSelectionToolbar);
        setSupportActionBar(secondaryToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        secondaryToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.secondaryMenuLogout) {
                this.doLogout();
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_secondary_toolbar, menu);
        return true;
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

    private void doSelectUserRole(int roleIndex) {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.selectUserRoleFailed("Please relogin.");
            return;
        }
        JSONObject postData = new JSONObject();
        try {
            postData.put("role", roleIndex == Constants.ROLE_LANDLORD ? "LANDLORD" : "TENANT");
            postData.put("remember_role", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_SELECT_ROLE, postData, response -> selectUserRoleSuccess(), error -> {
            try {
                String errorResponseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorResponseBodyJsonObject = new JSONObject(errorResponseBody);
                if (!errorResponseBodyJsonObject.has("role")) {
                    selectUserRoleFailed(Constants.ERROR_COMMON);
                    return;
                }
                selectUserRoleFailed(errorResponseBodyJsonObject.getJSONArray("role").getString(0));
            } catch (Exception e) {
                selectUserRoleFailed(Constants.ERROR_COMMON);
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

    private void selectUserRoleSuccess() {
        this.stopLoadingSpinner();
        Intent contentIntent = new Intent(FirstTimeRoleSelectionActivity.this, ContentActivity.class);
        startActivity(contentIntent);
        finish();
    }

    private void selectUserRoleFailed(String selectUserRoleFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder selectUserRoleFailedDialog = new AlertDialog.Builder(this);
        selectUserRoleFailedDialog.setCancelable(false);
        selectUserRoleFailedDialog.setTitle("Role Selection Failed");
        selectUserRoleFailedDialog.setMessage(selectUserRoleFailedCause);
        selectUserRoleFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        selectUserRoleFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.roleSelectionLandlordButton.setEnabled(false);
        this.roleSelectionTenantButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.roleSelectionLandlordButton.setEnabled(true);
        this.roleSelectionTenantButton.setEnabled(true);
    }
}
