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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText forgotPasswordEmail;
    private TextView forgotPasswordAlert;

    private Button forgotPasswordButton;
    private Button forgotPasswordBackButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        this.forgotPasswordEmail = findViewById(R.id.forgotPasswordEmail);
        this.forgotPasswordAlert = findViewById(R.id.forgotPasswordEmailAlert);

        this.backgroundView = findViewById(R.id.forgotPasswordBackground);
        this.loadingSpinner = findViewById(R.id.forgotPasswordLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        this.forgotPasswordButton.setOnClickListener(v -> doForgotPassword());

        this.forgotPasswordBackButton = findViewById(R.id.forgotPasswordBackButton);
        this.forgotPasswordBackButton.setOnClickListener(v -> finish());
    }

    private void doForgotPassword() {
        if (this.forgotPasswordEmail.length() <= 0) {
            this.forgotPasswordAlert.setVisibility(View.VISIBLE);
            this.forgotPasswordEmail.requestFocus();
            return;
        }

        this.forgotPasswordAlert.setVisibility(View.INVISIBLE);
        this.startLoadingSpinner();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", this.forgotPasswordEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_FORGOT_PASSWORD, postData, response -> forgotPasswordSuccess(), error -> {
            try {
                String errorResponseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorResponseBodyJsonObject = new JSONObject(errorResponseBody);
                System.out.println("errorResponseBodyJsonObject = " + errorResponseBodyJsonObject.toString());
                if (!errorResponseBodyJsonObject.has("message")) {
                    forgotPasswordFailed(Constants.ERROR_COMMON);
                    return;
                }
                String errorMessage = errorResponseBodyJsonObject.getString("message");
                if (errorMessage.equals("The given data was invalid.")) {
                    if (errorResponseBodyJsonObject.has("errors")) {
                        StringBuilder sb = new StringBuilder();
                        if (errorResponseBodyJsonObject.has("email")) {
                            sb.append("The email is not yet registered.");
                        }
                        if (sb.length() > 0) {
                            forgotPasswordFailed(sb.toString());
                        } else {
                            forgotPasswordFailed(errorMessage);
                        }
                    } else {
                        forgotPasswordFailed(errorMessage);
                    }
                } else {
                    forgotPasswordFailed(errorMessage);
                }
            } catch (Exception e) {
                forgotPasswordFailed(Constants.ERROR_COMMON);
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

    private void forgotPasswordSuccess() {
        this.stopLoadingSpinner();
        AlertDialog.Builder registerSuccessDialog  = new AlertDialog.Builder(this);
        registerSuccessDialog.setCancelable(false);
        registerSuccessDialog.setTitle("Forgot Password");
        registerSuccessDialog.setMessage("Password change has been sent to the email.");
        registerSuccessDialog.setPositiveButton("OK", (dialog, which) -> finish());
        registerSuccessDialog.create().show();
    }

    private void forgotPasswordFailed(String forgotPasswordFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder forgotPasswordFailedDialog = new AlertDialog.Builder(this);
        forgotPasswordFailedDialog.setCancelable(false);
        forgotPasswordFailedDialog.setTitle("Forgot Password Failed");
        forgotPasswordFailedDialog.setMessage(forgotPasswordFailedCause);
        forgotPasswordFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        forgotPasswordFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.forgotPasswordButton.setEnabled(false);
        this.forgotPasswordBackButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.forgotPasswordButton.setEnabled(true);
        this.forgotPasswordBackButton.setEnabled(true);
    }

}
