package com.propster.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        this.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPasswordIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
            }
        });

//        FirebaseApp.initializeApp(this);
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

//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("username", this.loginUsername.getText().toString());
//            postData.put("password", this.loginPassword.getText().toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LOGIN, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//                try {
//                    loginSuccess(response.getString("session_id"), response.getBoolean("first_time"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    loginFailed(e.getLocalizedMessage());
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                loginFailed(error.getLocalizedMessage());
//                error.printStackTrace();
//            }
//        });
//        this.requestQueue.add(jsonObjectRequest);

        loginSuccess("", false);
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

    private void loginSuccess(String sessionId, boolean firstTime) {
        this.updateFirebaseCMToken();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SHARED_PREFERENCES_SESSION_ID, sessionId);
        editor.apply();
        this.stopLoadingSpinner();
        if (firstTime) {
            Intent userProfileIntent = new Intent(LoginActivity.this, FirstTimeUserProfileActivity.class);
            startActivity(userProfileIntent);
        } else {
            Intent contentIntent = new Intent(LoginActivity.this, ContentActivity.class);
            startActivity(contentIntent);
        }
        finish();
    }

    private void loginFailed(String loginFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Login Failed");
        loginFailedDialog.setMessage(loginFailedCause);
        loginFailedDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        loginFailedDialog.create().show();
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
