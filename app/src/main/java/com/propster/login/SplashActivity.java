package com.propster.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.propster.R;
import com.propster.content.ContentActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

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

        this.checkAppVersion();

        FirebaseApp.initializeApp(this);
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
        checkAppVersionFailedDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        checkAppVersionFailedDialog.create().show();
    }

    private void checkLoginCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.checkSessionIdFailed();
        } else {
            doCheckSessionId(sessionId);
        }
    }

    private void doCheckSessionId(String sessionId) {
        if (sessionId.length() <= 0) {
            this.checkSessionIdFailed();
            return;
        }

        JSONObject postData = new JSONObject();
        try {
            postData.put("session_id", sessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_CHECK_SESSION_ID, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                checkSessionIdSuccess(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                checkSessionIdFailed();
                error.printStackTrace();
            }
        });
        this.requestQueue.add(jsonObjectRequest);
        checkSessionIdFailed();
    }

    private void checkSessionIdSuccess(boolean isUserProfileComplete) {
        if (isUserProfileComplete) {
            this.updateFirebaseCMToken();
            Intent contentIntent = new Intent(SplashActivity.this, ContentActivity.class);
            startActivity(contentIntent);
        } else {
            Intent userProfileIntent = new Intent(SplashActivity.this, FirstTimeUserProfileActivity.class);
            startActivity(userProfileIntent);
        }
        finish();
    }

    private void checkSessionIdFailed() {
        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void updateFirebaseCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult();
                        System.out.println("fcm token = " + token);
//                        JSONObject postData = new JSONObject();
//                        try {
//                            postData.put("token", token);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_UPDATE_FCM_TOKEN, postData, new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//
//                            }
//                        });
//                        requestQueue.add(jsonObjectRequest);
                    }
                });

    }

}
