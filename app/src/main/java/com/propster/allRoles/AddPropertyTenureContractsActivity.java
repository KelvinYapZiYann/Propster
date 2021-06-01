package com.propster.allRoles;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.propster.content.NotificationActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;
import com.propster.utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddPropertyTenureContractsActivity extends AppCompatActivity {

    private EditText addTenureContractsPropertyName;
    private EditText addTenureContractsTenantName;
    private EditText addTenureContractsName;
    private TextView addTenureContractsNameAlert;
    private EditText addTenureContractsDescription;
    private TextView addTenureContractsDescriptionAlert;
    private EditText addTenureContractsMonthlyRentalAmount;
    private TextView addTenureContractsMonthlyRentalAmountAlert;
    private EditText addTenureContractsTenureStartDate;
    private TextView addTenureContractsTenureStartDateAlert;
    private EditText addTenureContractsTenureEndDate;
    private TextView addTenureContractsTenureEndDateAlert;
    private ShapeableImageView addTenureContractsUploadedFile;
    private TextView addTenureContractsUploadedFileName;

    private Button addTenureContractsAddContractButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int propertyId = -1;
    private String propertyName = null;
    private int tenantId = -1;
    private String tenantName = null;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_tenure_contracts_add);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.propertyId = -1;
            this.propertyName = null;
            this.tenantId = -1;
            this.tenantName = null;
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
            this.tenantId = extras.getInt(Constants.INTENT_EXTRA_TENANT_ID, -1);
            this.tenantName = extras.getString(Constants.INTENT_EXTRA_TENANT_NAME, null);
        }

        this.addTenureContractsPropertyName = findViewById(R.id.addTenureContractsPropertyName);
        this.addTenureContractsTenantName = findViewById(R.id.addTenureContractsTenantName);
        this.addTenureContractsName = findViewById(R.id.addTenureContractsName);
        this.addTenureContractsNameAlert = findViewById(R.id.addTenureContractsNameAlert);
        this.addTenureContractsDescription = findViewById(R.id.addTenureContractsDescription);
        this.addTenureContractsDescriptionAlert = findViewById(R.id.addTenureContractsDescriptionAlert);
        this.addTenureContractsMonthlyRentalAmount = findViewById(R.id.addTenureContractsMonthlyRentalAmount);
        this.addTenureContractsMonthlyRentalAmountAlert = findViewById(R.id.addTenureContractsMonthlyRentalAmountAlert);
        this.addTenureContractsTenureStartDate = findViewById(R.id.addTenureContractsTenureStartDate);
        this.addTenureContractsTenureStartDateAlert = findViewById(R.id.addTenureContractsTenureStartDateAlert);
        this.addTenureContractsTenureEndDate = findViewById(R.id.addTenureContractsTenureEndDate);
        this.addTenureContractsTenureEndDateAlert = findViewById(R.id.addTenureContractsTenureEndDateAlert);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Date currentDate = new Date();
        Calendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTime(currentDate);
        this.addTenureContractsTenureStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddPropertyTenureContractsActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                addTenureContractsTenureStartDate.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        this.addTenureContractsTenureEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddPropertyTenureContractsActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                addTenureContractsTenureEndDate.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        this.addTenureContractsUploadedFile = findViewById(R.id.addTenureContractsUploadedFile);
        this.addTenureContractsUploadedFile.setOnClickListener(v -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            startActivityForResult(chooserIntent, Constants.REQUEST_CODE_PROPERTY_EXPENSES_IMAGE_DOCUMENT);
        });
        this.addTenureContractsUploadedFileName = findViewById(R.id.addTenureContractsUploadedFileName);

        this.backgroundView = findViewById(R.id.addTenureContractsBackground);
        this.loadingSpinner = findViewById(R.id.addTenureContractsLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.addTenureContractsAddContractButton = findViewById(R.id.addTenureContractsAddContractButton);
        this.addTenureContractsAddContractButton.setOnClickListener(v -> this.doAddTenureContracts());

        Toolbar mainToolbar = findViewById(R.id.addTenureContractsToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.mainMenuUser) {
//                Intent userProfileIntent = new Intent(AddPropertyExpensesActivity.this, UserProfileActivity.class);
//                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//            } else
            if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(AddPropertyTenureContractsActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshTenureContractsName();

    }

    private void refreshTenureContractsName() {
        if (this.propertyName == null) {
            return;
        }
        this.addTenureContractsPropertyName.setText(this.propertyName);
        if (this.tenantName == null) {
            return;
        }
        this.addTenureContractsTenantName.setText(this.tenantName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_TENURE_CONTRACTS_IMAGE_DOCUMENT) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    if (data == null) {
                        return;
                    }
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    this.addTenureContractsUploadedFile.setImageBitmap(selectedImage);

                    Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    this.addTenureContractsUploadedFileName.setText(picturePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doAddTenureContracts() {
        if (this.propertyId == -1) {
            this.addTenureContractsFailed(Constants.ERROR_COMMON);
            return;
        }
        if (this.tenantId == -1) {
            this.addTenureContractsFailed(Constants.ERROR_COMMON);
            return;
        }
        if (this.addTenureContractsName.length() <= 0) {
            this.addTenureContractsNameAlert.setVisibility(View.VISIBLE);
            this.addTenureContractsDescriptionAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsMonthlyRentalAmountAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsTenureStartDateAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsTenureEndDateAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsName.requestFocus();
            return;
        }
        if (this.addTenureContractsDescription.length() <= 0) {
            this.addTenureContractsNameAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsDescriptionAlert.setVisibility(View.VISIBLE);
            this.addTenureContractsMonthlyRentalAmountAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsTenureStartDateAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsTenureEndDateAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsDescription.requestFocus();
            return;
        }
        if (this.addTenureContractsMonthlyRentalAmount.length() <= 0) {
            this.addTenureContractsNameAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsDescriptionAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsMonthlyRentalAmountAlert.setVisibility(View.VISIBLE);
            this.addTenureContractsTenureStartDateAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsTenureEndDateAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsMonthlyRentalAmount.requestFocus();
            return;
        }
        if (this.addTenureContractsTenureStartDate.length() <= 0) {
            this.addTenureContractsNameAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsDescriptionAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsMonthlyRentalAmountAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsTenureStartDateAlert.setVisibility(View.VISIBLE);
            this.addTenureContractsTenureEndDateAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsTenureStartDate.requestFocus();
            return;
        }
        if (this.addTenureContractsTenureEndDate.length() <= 0) {
            this.addTenureContractsNameAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsDescriptionAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsMonthlyRentalAmountAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsTenureStartDateAlert.setVisibility(View.INVISIBLE);
            this.addTenureContractsTenureEndDateAlert.setVisibility(View.VISIBLE);
            this.addTenureContractsTenureEndDate.requestFocus();
            return;
        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.addTenureContractsFailed("Please relogin.");
            return;
        }
        if (this.addTenureContractsUploadedFileName.getText().equals("Empty") || !(this.addTenureContractsUploadedFile.getDrawable() instanceof BitmapDrawable)) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("asset_id", this.propertyId);
                postData.put("tenant_id", this.tenantId);
                postData.put("contract_name", this.addTenureContractsName.getText().toString());
                postData.put("contract_description", this.addTenureContractsDescription.getText().toString());
                postData.put("monthly_rental_amount", this.addTenureContractsMonthlyRentalAmount.getText().toString());
                postData.put("monthly_rental_currency_iso", "MYR");
                postData.put("tenure_start_date", this.addTenureContractsTenureStartDate.getText().toString());
                postData.put("tenure_end_date", this.addTenureContractsTenureEndDate.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LANDLORD_TENURE_CONTRACTS, postData, response -> {
                addTenureContractsSuccess();
            }, error -> {
                try {
                    System.out.println("error.networkResponse.data ==> " + new String(error.networkResponse.data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addTenureContractsFailed(Constants.ERROR_COMMON);
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
        } else {
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.URL_LANDLORD_TENURE_CONTRACTS, response -> {
                addTenureContractsSuccess();
            }, error -> {
                try {
                    System.out.println("error.networkResponse.data ==> " + new String(error.networkResponse.data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addTenureContractsFailed(Constants.ERROR_COMMON);
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if (SplashActivity.SESSION_ID.isEmpty()) {
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        SplashActivity.SESSION_ID = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, "");
                    }
                    Map<String, String> headerParams = new HashMap<>();
                    headerParams.put("Accept", "application/json");
//                    headerParams.put("Content-Type", "application/json");
//                    headerParams.put("X-Requested-With", "XMLHttpRequest");
                    headerParams.put("Authorization", SplashActivity.SESSION_ID);
                    return headerParams;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("asset_id", Integer.toString(propertyId));
                    params.put("tenant_id", Integer.toString(tenantId));
                    params.put("contract_name", addTenureContractsName.getText().toString());
                    params.put("contract_description", addTenureContractsDescription.getText().toString());
                    params.put("monthly_rental_amount", addTenureContractsMonthlyRentalAmount.getText().toString());
                    params.put("monthly_rental_currency_iso", "MYR");
                    params.put("tenure_start_date", addTenureContractsTenureStartDate.getText().toString());
                    params.put("tenure_end_date", addTenureContractsTenureEndDate.getText().toString());
                    return params;
                }

                @Override
                protected Map<String, FilePart> getFileData() throws AuthFailureError {
                    Map<String, FilePart> params = new HashMap<>();
                    String name = addTenureContractsUploadedFileName.getText().toString();
                    String type = name.substring(name.lastIndexOf(".") + 1).toUpperCase();

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) addTenureContractsUploadedFile.getDrawable();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    switch (type) {
                        case "JPEG":
                        case "JPG":
                            bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            break;
                        case "PNG":
                            bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            break;
                        default:
                            throw new AuthFailureError();
                    }
                    params.put("file", new FilePart(name, byteArrayOutputStream.toByteArray(), type));
                    return params;

                }
            };
            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.requestQueue.add(volleyMultipartRequest);
        }
    }

    private void addTenureContractsFailed(String addTenureContactsFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder addTenureContractsFailedDialog = new AlertDialog.Builder(this);
        addTenureContractsFailedDialog.setCancelable(false);
        addTenureContractsFailedDialog.setTitle("Add Tenure Contracts Failed");
        addTenureContractsFailedDialog.setMessage(addTenureContactsFailed);
        addTenureContractsFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        addTenureContractsFailedDialog.create().show();
    }

    private void addTenureContractsSuccess() {
        this.stopLoadingSpinner();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.addTenureContractsAddContractButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.addTenureContractsAddContractButton.setEnabled(true);
    }
}
