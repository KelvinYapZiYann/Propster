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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.imageview.ShapeableImageView;
import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;
import com.propster.utils.VolleyMultipartRequest;

import org.json.JSONArray;
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

public class PropertyTenureContractsEditActivity extends AppCompatActivity {

    private EditText tenureContractsEditPropertyName;
    private EditText tenureContractsEditTenantName;
    private EditText tenureContractsEditName;
    private TextView tenureContractsEditNameAlert;
    private EditText tenureContractsEditDescription;
    private TextView tenureContractsEditDescriptionAlert;
    private EditText tenureContractsEditMonthlyRentalAmount;
    private TextView tenureContractsEditMonthlyRentalAmountAlert;
    private EditText tenureContractsEditTenureStartDate;
    private TextView tenureContractsEditTenureStartDateAlert;
    private EditText tenureContractsEditTenureEndDate;
    private TextView tenureContractsEditTenureEndDateAlert;
    private ShapeableImageView tenureContractsEditUploadedFile;
    private TextView tenureContractsEditUploadedFileName;

    private Button tenureContractsEditSaveButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int propertyId = -1;
    private String propertyName = null;
    private int tenantId = -1;
    private String tenantName = null;
    private int tenureContractId = -1;
    private String tenureContractName = null;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_tenure_contracts_edit);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.propertyId = -1;
            this.propertyName = null;
            this.tenantId = -1;
            this.tenantName = null;
            this.tenureContractId = -1;
            this.tenureContractName = null;
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
            this.tenantId = extras.getInt(Constants.INTENT_EXTRA_TENANT_ID, -1);
            this.tenantName = extras.getString(Constants.INTENT_EXTRA_TENANT_NAME, null);
            this.tenureContractId = extras.getInt(Constants.INTENT_EXTRA_TENURE_CONTRACTS_ID, -1);
            this.tenureContractName = extras.getString(Constants.INTENT_EXTRA_TENURE_CONTRACTS_NAME, null);
        }

        this.tenureContractsEditPropertyName = findViewById(R.id.tenureContractsEditPropertyName);
        this.tenureContractsEditTenantName = findViewById(R.id.tenureContractsEditTenantName);
        this.tenureContractsEditName = findViewById(R.id.tenureContractsEditName);
        this.tenureContractsEditNameAlert = findViewById(R.id.tenureContractsEditNameAlert);
        this.tenureContractsEditDescription = findViewById(R.id.tenureContractsEditDescription);
        this.tenureContractsEditDescriptionAlert = findViewById(R.id.tenureContractsEditDescriptionAlert);
        this.tenureContractsEditMonthlyRentalAmount = findViewById(R.id.tenureContractsEditMonthlyRentalAmount);
        this.tenureContractsEditMonthlyRentalAmountAlert = findViewById(R.id.tenureContractsEditMonthlyRentalAmountAlert);
        this.tenureContractsEditTenureStartDate = findViewById(R.id.tenureContractsEditTenureStartDate);
        this.tenureContractsEditTenureStartDateAlert = findViewById(R.id.tenureContractsEditTenureStartDateAlert);
        this.tenureContractsEditTenureEndDate = findViewById(R.id.tenureContractsEditTenureEndDate);
        this.tenureContractsEditTenureEndDateAlert = findViewById(R.id.tenureContractsEditTenureEndDateAlert);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Date currentDate = new Date();
        Calendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTime(currentDate);
        this.tenureContractsEditTenureStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(PropertyTenureContractsEditActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tenureContractsEditTenureStartDate.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        this.tenureContractsEditTenureEndDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(PropertyTenureContractsEditActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tenureContractsEditTenureEndDate.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        this.tenureContractsEditUploadedFileName = findViewById(R.id.tenureContractsEditUploadedFileName);
        this.tenureContractsEditUploadedFile = findViewById(R.id.tenureContractsEditUploadedFile);
        this.tenureContractsEditUploadedFile.setOnClickListener(v -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            startActivityForResult(chooserIntent, Constants.REQUEST_CODE_PROPERTY_EXPENSES_IMAGE_DOCUMENT);
        });

        this.backgroundView = findViewById(R.id.tenureContractsEditBackground);
        this.loadingSpinner = findViewById(R.id.tenureContractsEditLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.tenureContractsEditSaveButton = findViewById(R.id.tenureContractsEditSaveButton);
        this.tenureContractsEditSaveButton.setOnClickListener(v -> this.doSaveTenureContracts());

        Toolbar mainToolbar = findViewById(R.id.tenureContractsEditToolbar);
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
                Intent notificationIntent = new Intent(PropertyTenureContractsEditActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshTenureContracts();
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
                    this.tenureContractsEditUploadedFile.setImageBitmap(selectedImage);

                    Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    this.tenureContractsEditUploadedFileName.setText(picturePath);
                    this.tenureContractsEditUploadedFileName.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void refreshTenureContracts() {
        if (this.tenureContractId == -1) {
            getTenureContractsEditFailed(Constants.ERROR_COMMON);
            return;
        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getTenureContractsEditFailed("Please relogin.");
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_TENURE_CONTRACTS + "/" + this.tenureContractId, null, response -> {
            try {
                System.out.println("response = " + response.toString());
                if (!response.has("data")) {
                    getTenureContractsEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (!dataJsonObject.has("id")) {
                    getTenureContractsEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataJsonObject.has("fields")) {
                    getTenureContractsEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                if (!dataFieldsJsonObject.has("contract_name")) {
                    getTenureContractsEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("contract_description")) {
                    getTenureContractsEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("monthly_rental_amount")) {
                    getTenureContractsEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("tenure_start_date")) {
                    getTenureContractsEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataFieldsJsonObject.has("tenure_end_date")) {
                    getTenureContractsEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                this.tenureContractsEditPropertyName.setText(this.propertyName);
                this.tenureContractsEditTenantName.setText(this.tenantName);
                this.tenureContractName = dataFieldsJsonObject.getString("contract_name");
                this.tenureContractsEditName.setText(this.tenureContractName);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(this.tenureContractName);
                }
                this.tenureContractsEditDescription.setText(dataFieldsJsonObject.getString("contract_description"));
                this.tenureContractsEditMonthlyRentalAmount.setText(dataFieldsJsonObject.getString("monthly_rental_amount"));
                this.tenureContractsEditTenureStartDate.setText(dataFieldsJsonObject.getString("tenure_start_date"));
                this.tenureContractsEditTenureEndDate.setText(dataFieldsJsonObject.getString("tenure_end_date"));

                this.stopLoadingSpinner();

                if (!dataFieldsJsonObject.has("media")) {
                    return;
                }
                JSONArray dataFieldsMediaJsonArray = dataFieldsJsonObject.getJSONArray("media");
                if (dataFieldsMediaJsonArray.length() <= 0) {
                    return;
                }
                JSONObject dataFieldsMediaJsonObject = dataFieldsMediaJsonArray.getJSONObject(0);
                if (!dataFieldsMediaJsonObject.has("temporary_url")) {
                    return;
                }
                String mediaTemporaryUrl = dataFieldsMediaJsonObject.getString("temporary_url");
                displayImageFromUrl(mediaTemporaryUrl);
            } catch (JSONException e) {
                e.printStackTrace();
                getTenureContractsEditFailed(Constants.ERROR_COMMON);
            }
        }, error -> getTenureContractsEditFailed(Constants.ERROR_COMMON)) {
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

    private void getTenureContractsEditFailed(String propertyExpensesDetailFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder propertyExpensesDetailFailedDialog = new AlertDialog.Builder(this);
        propertyExpensesDetailFailedDialog.setCancelable(false);
        propertyExpensesDetailFailedDialog.setTitle("Tenure Contracts Failed");
        propertyExpensesDetailFailedDialog.setMessage(propertyExpensesDetailFailed);
        propertyExpensesDetailFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        propertyExpensesDetailFailedDialog.create().show();
    }
    
    private void doSaveTenureContracts() {
        if (this.tenureContractsEditName.length() <= 0) {
            this.tenureContractsEditNameAlert.setVisibility(View.VISIBLE);
            this.tenureContractsEditDescriptionAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditMonthlyRentalAmountAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditTenureStartDateAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditTenureEndDateAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditName.requestFocus();
            return;
        }
        if (this.tenureContractsEditDescription.length() <= 0) {
            this.tenureContractsEditNameAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditDescriptionAlert.setVisibility(View.VISIBLE);
            this.tenureContractsEditMonthlyRentalAmountAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditTenureStartDateAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditTenureEndDateAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditDescription.requestFocus();
            return;
        }
        if (this.tenureContractsEditMonthlyRentalAmount.length() <= 0) {
            this.tenureContractsEditNameAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditDescriptionAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditMonthlyRentalAmountAlert.setVisibility(View.VISIBLE);
            this.tenureContractsEditTenureStartDateAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditTenureEndDateAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditMonthlyRentalAmount.requestFocus();
            return;
        }
        if (this.tenureContractsEditTenureStartDate.length() <= 0) {
            this.tenureContractsEditNameAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditDescriptionAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditMonthlyRentalAmountAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditTenureStartDateAlert.setVisibility(View.VISIBLE);
            this.tenureContractsEditTenureEndDateAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditTenureStartDate.requestFocus();
            return;
        }
        if (this.tenureContractsEditTenureEndDate.length() <= 0) {
            this.tenureContractsEditNameAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditDescriptionAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditMonthlyRentalAmountAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditTenureStartDateAlert.setVisibility(View.INVISIBLE);
            this.tenureContractsEditTenureEndDateAlert.setVisibility(View.VISIBLE);
            this.tenureContractsEditTenureEndDate.requestFocus();
            return;
        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.editTenureContractsFailed("Please relogin.");
            return;
        }
        if (!(this.tenureContractsEditUploadedFile.getDrawable() instanceof BitmapDrawable) || this.tenureContractsEditUploadedFileName.getText().toString().equals("Empty")) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("asset_id", this.propertyId);
                postData.put("tenant_id", this.tenantId);
                postData.put("contract_name", this.tenureContractsEditName.getText().toString());
                postData.put("contract_description", this.tenureContractsEditDescription.getText().toString());
                postData.put("monthly_rental_amount", this.tenureContractsEditMonthlyRentalAmount.getText().toString());
                postData.put("monthly_rental_currency_iso", "MYR");
                postData.put("tenure_start_date", this.tenureContractsEditTenureStartDate.getText().toString());
                postData.put("tenure_end_date", this.tenureContractsEditTenureEndDate.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Constants.URL_LANDLORD_TENURE_CONTRACTS + "/" + this.tenureContractId, postData, response -> {
                editTenureContractsSuccess();
            }, error -> {
                try {
                    System.out.println("error.networkResponse.data ==> " + new String(error.networkResponse.data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editTenureContractsFailed(Constants.ERROR_COMMON);
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
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, Constants.URL_LANDLORD_TENURE_CONTRACTS + "/" + this.tenureContractId, response -> {
                editTenureContractsSuccess();
            }, error -> {
                try {
                    System.out.println("error.networkResponse.data ==> " + new String(error.networkResponse.data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editTenureContractsFailed(Constants.ERROR_COMMON);
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

                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("asset_id", Integer.toString(propertyId));
                    params.put("tenant_id", Integer.toString(tenantId));
                    params.put("contract_name", tenureContractsEditName.getText().toString());
                    params.put("contract_description", tenureContractsEditDescription.getText().toString());
                    params.put("monthly_rental_amount", tenureContractsEditMonthlyRentalAmount.getText().toString());
                    params.put("monthly_rental_currency_iso", "MYR");
                    params.put("tenure_start_date", tenureContractsEditTenureStartDate.getText().toString());
                    params.put("tenure_end_date", tenureContractsEditTenureEndDate.getText().toString());
                    return params;
                }

                @Override
                protected Map<String, FilePart> getFileData() throws AuthFailureError {
                    Map<String, FilePart> params = new HashMap<>();
                    String name = tenureContractsEditUploadedFileName.getText().toString();
                    String type = name.substring(name.lastIndexOf(".") + 1).toUpperCase();

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) tenureContractsEditUploadedFile.getDrawable();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    switch (type) {
                        case "JPEG":
                        case "JPG":
                            bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                            break;
                        case "PNG":
                            bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
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

    private void displayImageFromUrl(String mediaTemporaryUrl) {
        this.startLoadingSpinner();
        ImageRequest imageRequest = new ImageRequest(mediaTemporaryUrl, response -> {
            tenureContractsEditUploadedFile.setImageBitmap(response);
            this.stopLoadingSpinner();
        }, 0, 0, null, null, error -> {
            getTenureContractsEditFailed(Constants.ERROR_IMAGE_DOCUMENT_FILE_NOT_LOADED);
        });
        imageRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(imageRequest);
    }

    private void editTenureContractsFailed(String editTenureContactsFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder tenureContractsEditFailedDialog = new AlertDialog.Builder(this);
        tenureContractsEditFailedDialog.setCancelable(false);
        tenureContractsEditFailedDialog.setTitle("Edit Tenure Contracts Failed");
        tenureContractsEditFailedDialog.setMessage(editTenureContactsFailed);
        tenureContractsEditFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        tenureContractsEditFailedDialog.create().show();
    }

    private void editTenureContractsSuccess() {
        this.stopLoadingSpinner();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.tenureContractsEditSaveButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.tenureContractsEditSaveButton.setEnabled(true);
    }
    
}
