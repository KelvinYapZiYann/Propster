package com.propster.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class LoginActivity extends AppCompatActivity {

    private EditText loginUsername;
    private TextView loginUsernameAlert;
    private EditText loginPassword;
    private TextView loginPasswordAlert;
    private Button loginButton;
    private Button registerButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.loginUsername = findViewById(R.id.loginUsername);
        this.loginUsernameAlert = findViewById(R.id.loginUsernameAlert);
        this.loginPassword = findViewById(R.id.loginPassword);
        this.loginPasswordAlert = findViewById(R.id.loginPasswordAlert);
        this.loginButton = findViewById(R.id.loginButton);
        this.registerButton = findViewById(R.id.loginRegisterButton);
        TextView forgotPasswordButton = findViewById(R.id.loginForgotPassword);

        this.backgroundView = findViewById(R.id.loginBackground);
        this.loadingSpinner = findViewById(R.id.loginLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.loginButton.setOnClickListener(v -> doLogin());

        this.registerButton.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });

        forgotPasswordButton.setOnClickListener(v -> {
            Intent forgotPasswordIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(forgotPasswordIntent);
        });
    }

    private void doLogin() {
        if (this.loginUsername.length() <= 0) {
            this.loginUsernameAlert.setVisibility(View.VISIBLE);
            this.loginPasswordAlert.setVisibility(View.INVISIBLE);
            this.loginUsername.requestFocus();
            return;
        }
        if (this.loginPassword.length() <= 0) {
            this.loginUsernameAlert.setVisibility(View.INVISIBLE);
            this.loginPasswordAlert.setVisibility(View.VISIBLE);
            this.loginPassword.requestFocus();
            return;
        }
        this.loginUsernameAlert.setVisibility(View.INVISIBLE);
        this.loginPasswordAlert.setVisibility(View.INVISIBLE);

        this.startLoadingSpinner();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", this.loginUsername.getText().toString());
            postData.put("password", this.loginPassword.getText().toString());
        } catch (JSONException e) {
            loginFailed(Constants.ERROR_COMMON);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LOGIN, postData, response -> {
            try {
                if (!response.has("access_token")) {
                    loginFailed(Constants.ERROR_COMMON);
                    return;
                }
                loginSuccess(response.getString("access_token"));
            } catch (JSONException e) {
                loginFailed(Constants.ERROR_COMMON);
            }
        }, error -> {
            try {
                String errorResponseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorResponseBodyJsonObject = new JSONObject(errorResponseBody);
                if (!errorResponseBodyJsonObject.has("message")) {
                    loginFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!errorResponseBodyJsonObject.has("errors")) {
                    loginFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject errorJsonObject = errorResponseBodyJsonObject.getJSONObject("errors");
                if (!errorJsonObject.has("email")) {
                    loginFailed(Constants.ERROR_COMMON);
                    return;
                }
                loginFailed(Constants.ERROR_LOGIN_FAILED_CREDENTIALS);
            } catch (Exception e) {
                loginFailed(Constants.ERROR_COMMON);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerParams = new HashMap<>();
                headerParams.put("Accept", "application/json");
                headerParams.put("Content-Type", "application/json");
                return headerParams;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(jsonObjectRequest);
    }

//    private void doUpdateFirebaseCMToken() {
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        return;
//                    }
//                    String token = task.getResult();
//                    System.out.println("fcm token = " + token);
////                        JSONObject postData = new JSONObject();
////                        try {
////                            postData.put("token", token);
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_UPDATE_FCM_TOKEN, postData, new Response.Listener<JSONObject>() {
////                            @Override
////                            public void onResponse(JSONObject response) {
////
////                            }
////                        }, new Response.ErrorListener() {
////                            @Override
////                            public void onErrorResponse(VolleyError error) {
////
////                            }
////                        });
////                        requestQueue.add(jsonObjectRequest);
//                });
//    }

    private void doCheckUserMiddlewareVerification() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_CHECK_MIDDLEWARE_VERIFICATION, null, response -> {
            Intent contentIntent = new Intent(LoginActivity.this, ContentActivity.class);
            startActivity(contentIntent);
            finish();
        }, error -> {
            try {
                String errorResponseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorResponseBodyJsonObject = new JSONObject(errorResponseBody);
                if (!errorResponseBodyJsonObject.has("message")) {
                    loginFailed(Constants.ERROR_COMMON);
                    return;
                }
                String errorMessage = errorResponseBodyJsonObject.getString("message");
                switch (errorMessage) {
                    case "Unauthenticated.":
                        loginFailed(Constants.ERROR_LOGIN_FAILED_CREDENTIALS);
                        break;
                    case "user email not verified.":
                        loginFailedWithResendEmailVerification();
                        break;
                    case "user profile is not filled.":
                        Intent userProfileIntent = new Intent(LoginActivity.this, FirstTimeUserProfileActivity.class);
                        startActivity(userProfileIntent);
                        finish();
                        break;
                    case "user role not selected.":
                        Intent roleSelectionIntent = new Intent(LoginActivity.this, FirstTimeRoleSelectionActivity.class);
                        startActivity(roleSelectionIntent);
                        finish();
                        break;
                    default:
                        loginFailed(Constants.ERROR_COMMON);
                        break;
                }
            } catch (Exception e) {
                loginFailed(Constants.ERROR_COMMON);
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

    private void loginSuccess(String sessionId) {
//        this.doUpdateFirebaseCMToken();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SHARED_PREFERENCES_EMAIL, this.loginUsername.getText().toString());
        editor.putString(Constants.SHARED_PREFERENCES_PASSWORD, this.loginPassword.getText().toString());
        editor.putString(Constants.SHARED_PREFERENCES_SESSION_ID, Constants.SESSION_ID_PREFIX + sessionId);
        editor.apply();
        SplashActivity.SESSION_ID = Constants.SESSION_ID_PREFIX + sessionId;
        this.doCheckUserMiddlewareVerification();
    }

    private void loginFailed(String loginFailedCause) {
        this.stopLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.SHARED_PREFERENCES_EMAIL);
        editor.remove(Constants.SHARED_PREFERENCES_PASSWORD);
        editor.remove(Constants.SHARED_PREFERENCES_SESSION_ID);
        editor.apply();
        SplashActivity.SESSION_ID = "";
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Login Failed");
        loginFailedDialog.setMessage(loginFailedCause);
        loginFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        loginFailedDialog.create().show();
    }

    private void loginFailedWithResendEmailVerification() {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Login Failed");
        loginFailedDialog.setMessage(Constants.ERROR_LOGIN_FAILED_NOT_VERIFIED);
        loginFailedDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        loginFailedDialog.setNeutralButton("Resend Email Verification", (dialog, which) -> {
            dialog.cancel();
            doResendEmailVerification();
        });
        loginFailedDialog.create().show();
    }

    private void doResendEmailVerification() {
        this.startLoadingSpinner();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_RESEND_EMAIL_VERIFICATION, null,
                response -> resendEmailVerificationSuccess(),
                error -> resendEmailVerificationFailed()) {
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

    private void resendEmailVerificationSuccess() {
        this.stopLoadingSpinner();
        AlertDialog.Builder resendVerificationEmailDialog = new AlertDialog.Builder(LoginActivity.this);
        resendVerificationEmailDialog.setCancelable(false);
        resendVerificationEmailDialog.setTitle("Resend Email Verification");
        resendVerificationEmailDialog.setMessage(Constants.RESEND_EMAIL_VERIFICATION_SUCCESSFUL);
        resendVerificationEmailDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        resendVerificationEmailDialog.create().show();
    }

    private void resendEmailVerificationFailed() {
        this.stopLoadingSpinner();
        AlertDialog.Builder resendVerificationEmailDialog = new AlertDialog.Builder(LoginActivity.this);
        resendVerificationEmailDialog.setCancelable(false);
        resendVerificationEmailDialog.setTitle("Resend Email Verification Failed");
        resendVerificationEmailDialog.setMessage(Constants.ERROR_RESEND_EMAIL_VERIFICATION_FAILED);
        resendVerificationEmailDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        resendVerificationEmailDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.loginButton.setEnabled(false);
        this.registerButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.loginButton.setEnabled(true);
        this.registerButton.setEnabled(true);
    }

}
