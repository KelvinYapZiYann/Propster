package com.propster.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.propster.R;
import com.propster.content.ContentActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    public static String SESSION_ID = "";

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Animation splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash);
        ImageView splashLogo = findViewById(R.id.splashLogo);
        TextView splashTitle = findViewById(R.id.splashTitle);
        splashLogo.setAnimation(splashAnimation);
        splashTitle.setAnimation(splashAnimation);

        this.requestQueue = Volley.newRequestQueue(this);

        this.checkInternetConnection();

        FirebaseApp.initializeApp(this);
    }

    private void checkInternetConnection() {
        boolean internetConnection = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            internetConnection = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        }
        if (internetConnection) {
            this.checkAppVersion();
        } else {
            AlertDialog.Builder checkAppVersionFailedDialog = new AlertDialog.Builder(this);
            checkAppVersionFailedDialog.setCancelable(false);
            checkAppVersionFailedDialog.setTitle("No Internet Connection");
            checkAppVersionFailedDialog.setMessage("Please make sure the device is connected to internet.");
            checkAppVersionFailedDialog.setPositiveButton("Try again", (dialog, which) -> {
                dialog.cancel();
                this.checkInternetConnection();
            });
            checkAppVersionFailedDialog.create().show();
        }
    }

    private void checkAppVersion() {
//        JSONObject postData = new JSONObject();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_CHECK_SESSION_ID, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//                try {
//                    if (getPackageManager().getPackageInfo(getPackageName(), 0).versionName.equals("")) {
//                        checkAppVersionSuccess();
//                    } else {
//                        checkAppVersionFailed();
//                    }
//                } catch (Exception e) {
//                    checkAppVersionFailed();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                checkAppVersionFailed();
//                error.printStackTrace();
//            }
//        });
//        this.requestQueue.add(jsonObjectRequest);
        checkAppVersionSuccess();
    }

    private void checkAppVersionSuccess() {
        this.checkLoginCredentials();
    }

    private void checkAppVersionFailed() {
        AlertDialog.Builder checkAppVersionFailedDialog = new AlertDialog.Builder(this);
        checkAppVersionFailedDialog.setCancelable(false);
        checkAppVersionFailedDialog.setTitle("Outdated App Version");
        checkAppVersionFailedDialog.setMessage("Please update to the latest version to access to this app.");
        checkAppVersionFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        checkAppVersionFailedDialog.create().show();
    }

    private void checkLoginCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.SHARED_PREFERENCES_EMAIL, null);
        String password = sharedPreferences.getString(Constants.SHARED_PREFERENCES_PASSWORD, null);
        if (email == null || password == null) {
            this.loginFailed();
        } else {
            this.doLogin(email, password);
        }
    }

    private void doLogin(String email, String password) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
            postData.put("password", password);
        } catch (JSONException e) {
            loginFailed();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LOGIN, postData, response -> {
            try {
                if (!response.has("access_token")) {
                    loginFailed();
                    return;
                }
                /* todo */
//                loginFailed();
                loginSuccess(response.getString("access_token"));
            } catch (Exception e) {
                loginFailed();
            }
        }, error -> loginFailed()) {
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

    private void doCheckUserMiddlewareVerification() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_CHECK_MIDDLEWARE_VERIFICATION, null, response -> {
            Intent contentIntent = new Intent(SplashActivity.this, ContentActivity.class);
            startActivity(contentIntent);
            finish();
        }, error -> {
            try {
                String errorResponseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorResponseBodyJsonObject = new JSONObject(errorResponseBody);
                if (!errorResponseBodyJsonObject.has("message")) {
                    loginFailed();
                    return;
                }
                String errorMessage = errorResponseBodyJsonObject.getString("message");
                switch (errorMessage) {
                    case "user profile is not filled.":
                        Intent userProfileIntent = new Intent(SplashActivity.this, FirstTimeUserProfileActivity.class);
                        startActivity(userProfileIntent);
                        finish();
                        break;
                    case "user role not selected.":
                        Intent roleSelectionIntent = new Intent(SplashActivity.this, FirstTimeRoleSelectionActivity.class);
                        startActivity(roleSelectionIntent);
                        finish();
                        break;
                    default:
                        loginFailed();
                        break;
                }
            } catch (Exception e) {
                loginFailed();
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
        editor.putString(Constants.SHARED_PREFERENCES_SESSION_ID, Constants.SESSION_ID_PREFIX + sessionId);
        editor.apply();
        SESSION_ID = Constants.SESSION_ID_PREFIX + sessionId;
        this.doCheckUserMiddlewareVerification();
    }

    private void loginFailed() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.SHARED_PREFERENCES_EMAIL);
        editor.remove(Constants.SHARED_PREFERENCES_PASSWORD);
        editor.remove(Constants.SHARED_PREFERENCES_SESSION_ID);
        editor.apply();
        SESSION_ID = "";
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

//    private void updateFirebaseCMToken() {
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            return;
//                        }
//                        String token = task.getResult();
//                        System.out.println("fcm token = " + token);
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
//                    }
//                });
//
//    }

}
