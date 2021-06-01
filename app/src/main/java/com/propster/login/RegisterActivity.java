package com.propster.login;

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
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmail;
    private TextView registerEmailAlert;
    private EditText registerPhoneNumber;
    private TextView registerPhoneNumberAlert;
    private EditText registerPassword;
    private TextView registerPasswordAlert;
    private EditText registerConfirmedPassword;
    private TextView registerConfirmedPasswordAlert;

    private Button registerButton;
    private Button backButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.registerEmail = findViewById(R.id.registerEmail);
        this.registerEmailAlert = findViewById(R.id.registerEmailAlert);
        this.registerPhoneNumber = findViewById(R.id.registerPhoneNumber);
        this.registerPhoneNumberAlert = findViewById(R.id.registerPhoneNumberAlert);
        this.registerPassword = findViewById(R.id.registerPassword);
        this.registerPasswordAlert = findViewById(R.id.registerPasswordAlert);
        this.registerConfirmedPassword = findViewById(R.id.registerConfirmedPassword);
        this.registerConfirmedPasswordAlert = findViewById(R.id.registerConfirmedPasswordAlert);

        this.backgroundView = findViewById(R.id.registerBackground);
        this.loadingSpinner = findViewById(R.id.registerLoadingSpinner);

        this.registerButton = findViewById(R.id.registerButton);
        this.registerButton.setOnClickListener(v -> doRegister());

        this.backButton = findViewById(R.id.registerBackButton);
        this.backButton.setOnClickListener(v -> finish());

        this.requestQueue = Volley.newRequestQueue(this);

    }

    private void doRegister() {
        if (this.registerEmail.length() <= 0) {
            this.registerEmailAlert.setVisibility(View.VISIBLE);
            this.registerPhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.registerPasswordAlert.setVisibility(View.INVISIBLE);
            this.registerConfirmedPasswordAlert.setVisibility(View.INVISIBLE);
            this.registerEmail.requestFocus();
            return;
        }
        if (this.registerPhoneNumber.length() <= 0) {
            this.registerEmailAlert.setVisibility(View.INVISIBLE);
            this.registerPhoneNumberAlert.setVisibility(View.VISIBLE);
            this.registerPasswordAlert.setVisibility(View.INVISIBLE);
            this.registerConfirmedPasswordAlert.setVisibility(View.INVISIBLE);
            this.registerPhoneNumber.requestFocus();
            return;
        }
        if (this.registerPassword.length() <= 0) {
            this.registerEmailAlert.setVisibility(View.INVISIBLE);
            this.registerPhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.registerPasswordAlert.setVisibility(View.VISIBLE);
            this.registerConfirmedPasswordAlert.setVisibility(View.INVISIBLE);
            this.registerPassword.requestFocus();
            return;
        }
        if (this.registerConfirmedPassword.length() <= 0) {
            this.registerEmailAlert.setVisibility(View.INVISIBLE);
            this.registerPhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.registerPasswordAlert.setVisibility(View.INVISIBLE);
            this.registerConfirmedPasswordAlert.setVisibility(View.VISIBLE);
            this.registerConfirmedPassword.requestFocus();
            return;
        }
        if (!this.registerPassword.getEditableText().toString().equals(this.registerConfirmedPassword.getEditableText().toString())) {
            this.registerEmailAlert.setVisibility(View.INVISIBLE);
            this.registerPhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.registerPasswordAlert.setVisibility(View.INVISIBLE);
            this.registerConfirmedPasswordAlert.setVisibility(View.VISIBLE);
            this.registerConfirmedPassword.requestFocus();
            return;
        }

        this.registerEmailAlert.setVisibility(View.INVISIBLE);
        this.registerPhoneNumberAlert.setVisibility(View.INVISIBLE);
        this.registerPasswordAlert.setVisibility(View.INVISIBLE);
        this.registerConfirmedPasswordAlert.setVisibility(View.INVISIBLE);

        this.startLoadingSpinner();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", this.registerEmail.getText().toString());
            postData.put("mobile_number", this.registerPhoneNumber.getText().toString());
            postData.put("password", this.registerPassword.getText().toString());
            postData.put("password_confirmation", this.registerConfirmedPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_REGISTER, postData, response -> registerSuccess(), error -> {
            try {
                String errorResponseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorResponseBodyJsonObject = new JSONObject(errorResponseBody);
                if (!errorResponseBodyJsonObject.has("message")) {
                    registerFailed(Constants.ERROR_COMMON);
                    return;
                }
                String errorMessage = errorResponseBodyJsonObject.getString("message");
                if (errorMessage.equals("The given data was invalid.")) {
                    if (errorResponseBodyJsonObject.has("errors")) {
                        JSONObject errorsJsonObject = errorResponseBodyJsonObject.getJSONObject("errors");
                        StringBuilder sb = new StringBuilder();
                        if (errorsJsonObject.has("email")) {
                            sb.append(errorsJsonObject.getJSONArray("email").getString(0)).append("\n");
                        }
                        if (errorsJsonObject.has("password")) {
                            sb.append(errorsJsonObject.getJSONArray("password").getString(0)).append("\n");
                        }
                        if (errorsJsonObject.has("password_confirmation")) {
                            sb.append(errorsJsonObject.getJSONArray("password_confirmation").getString(0)).append("\n");
                        }
                        if (errorsJsonObject.has("mobile_number")) {
                            sb.append(errorsJsonObject.getJSONArray("mobile_number").getString(0)).append("\n");
                        }
                        if (sb.length() > 0) {
                            registerFailed(sb.toString());
                        } else {
                            registerFailed(errorMessage);
                        }
                    } else {
                        registerFailed(errorMessage);
                    }
                } else {
                    registerFailed(errorMessage);
                }
            } catch (Exception e) {
                registerFailed(Constants.ERROR_COMMON);
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

    private void registerSuccess() {
        stopLoadingSpinner();
        AlertDialog.Builder registerSuccessDialog  = new AlertDialog.Builder(this);
        registerSuccessDialog.setCancelable(false);
        registerSuccessDialog.setTitle("Register Successful");
        registerSuccessDialog.setMessage("Check your email to verify the account before logging in.");
        registerSuccessDialog.setPositiveButton("OK", (dialog, which) -> finish());
        registerSuccessDialog.create().show();
    }

    private void registerFailed(String registerFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder registerFailedDialog = new AlertDialog.Builder(this);
        registerFailedDialog.setCancelable(false);
        registerFailedDialog.setTitle("Register Failed");
        registerFailedDialog.setMessage(registerFailedCause);
        registerFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        registerFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.registerButton.setEnabled(false);
        this.backButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.registerButton.setEnabled(true);
        this.backButton.setEnabled(true);
    }

}
