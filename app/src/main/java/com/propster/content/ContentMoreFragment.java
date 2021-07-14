package com.propster.content;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.propster.R;
import com.propster.login.LoginActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContentMoreFragment extends ContentFragment {

    private final FragmentActivity activity;
    private final ContentTabPageAdapter contentTabPageAdapter;

    private Button contentMoreUserProfileButton;
    private Button contentMoreSwitchRoleButton;
    private Button contentMoreLogoutButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    public ContentMoreFragment(FragmentActivity activity, ContentTabPageAdapter contentTabPageAdapter) {
        this.activity = activity;
        this.contentTabPageAdapter = contentTabPageAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.contentMoreUserProfileButton = view.findViewById(R.id.contentMoreUserProfileButton);
        this.contentMoreUserProfileButton.setOnClickListener(v -> {
            Intent userProfileIntent = new Intent(getContext(), UserProfileActivity.class);
            startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
        });

        this.contentMoreSwitchRoleButton = view.findViewById(R.id.contentMoreSwitchRoleButton);
        this.contentMoreSwitchRoleButton.setOnClickListener(v -> {
            this.doSwitchRole();
        });

        this.contentMoreLogoutButton = view.findViewById(R.id.contentMoreLogoutButton);
        this.contentMoreLogoutButton.setOnClickListener(v -> {
            this.doLogout();
        });

        this.backgroundView = view.findViewById(R.id.contentMoreBackground);
        this.loadingSpinner = view.findViewById(R.id.contentMoreLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this.activity);
    }

    private void doSwitchRole() {
        SharedPreferences sharedPreferences = this.activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int role = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
        AlertDialog.Builder switchRoleDialog = new AlertDialog.Builder(this.activity);
        switchRoleDialog.setCancelable(false);
        switchRoleDialog.setTitle("Switch Role");
        switchRoleDialog.setMessage("Switch role to " + (role == Constants.ROLE_LANDLORD ? "tenant" : "landlord") + "?");
        switchRoleDialog.setPositiveButton("OK", (dialog, which) -> {
            this.startLoadingSpinner();

            JSONObject postData = new JSONObject();
            try {
                postData.put("role", (role == Constants.ROLE_LANDLORD ? "TENANT" : "LANDLORD"));
                postData.put("remember_role", true);
            } catch (JSONException e) {
                switchRoleFailed(Constants.ERROR_COMMON);
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_SELECT_ROLE, postData, response -> switchRoleSuccess(), error -> switchRoleSuccess()) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if (SplashActivity.SESSION_ID.isEmpty()) {
                        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
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
            dialog.cancel();
        });
        switchRoleDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        switchRoleDialog.create().show();
    }

    private void switchRoleSuccess() {
        this.stopLoadingSpinner();
        SharedPreferences sharedPreferences = this.activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int role = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (role == Constants.ROLE_LANDLORD) {
            editor.putInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
            this.contentTabPageAdapter.setRole(Constants.ROLE_TENANT);
        } else {
            editor.putInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_LANDLORD);
            this.contentTabPageAdapter.setRole(Constants.ROLE_LANDLORD);
        }
        editor.apply();
        this.contentTabPageAdapter.reloadContentFragments();
    }

    private void switchRoleFailed(String switchRoleFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this.activity);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("Switch Role Failed");
        saveUserProfileFailedDialog.setMessage(switchRoleFailedCause);
        saveUserProfileFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        saveUserProfileFailedDialog.create().show();
    }

    private void doLogout() {
        this.startLoadingSpinner();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LOGOUT, null, response -> logoutSuccess(), error -> logoutSuccess()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (SplashActivity.SESSION_ID.isEmpty()) {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
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
        SharedPreferences sharedPreferences = this.activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.SHARED_PREFERENCES_EMAIL);
        editor.remove(Constants.SHARED_PREFERENCES_PASSWORD);
        editor.remove(Constants.SHARED_PREFERENCES_SESSION_ID);
        editor.apply();
        SplashActivity.SESSION_ID = "";
        Intent loginIntent = new Intent(this.activity, LoginActivity.class);
        startActivity(loginIntent);
        this.activity.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constants.REQUEST_CODE_SWITCH_ROLE) {
//            if (resultCode == Activity.RESULT_OK) {
//                SharedPreferences sharedPreferences = this.activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//                int role = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
//                if (role == Constants.ROLE_LANDLORD) {
//                    this.contentTabPageAdapter.setRole(Constants.ROLE_LANDLORD);
//                } else {
//                    this.contentTabPageAdapter.setRole(Constants.ROLE_TENANT);
//                }
//                this.contentTabPageAdapter.reloadContentFragments();
//            }
//        }
    }

    private void startLoadingSpinner() {
        this.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.contentMoreUserProfileButton.setEnabled(false);
        this.contentMoreSwitchRoleButton.setEnabled(false);
        this.contentMoreLogoutButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.contentMoreUserProfileButton.setEnabled(true);
        this.contentMoreSwitchRoleButton.setEnabled(true);
        this.contentMoreLogoutButton.setEnabled(true);
    }
}
