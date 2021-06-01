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

public class PropertyExpensesEditActivity extends AppCompatActivity {

    private EditText propertyExpensesEditDescription;
    private TextView propertyExpensesEditDescriptionAlert;
    private EditText propertyExpensesEditPropertyName;
    private TextView propertyExpensesEditPropertyNameAlert;
    private EditText propertyExpensesEditType;
    private TextView propertyExpensesEditTypeAlert;
    private EditText propertyExpensesEditVendor;
    private TextView propertyExpensesEditVendorAlert;
    private EditText propertyExpensesEditAmount;
    private TextView propertyExpensesEditAmountAlert;
    private EditText propertyExpensesEditDateOfExpense;
    private TextView propertyExpensesEditDateOfExpenseAlert;
    private ShapeableImageView propertyExpensesEditUploadedFile;
    private TextView propertyExpensesEditUploadedFileName;

    private Button propertyExpensesEditSaveButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int propertyId = -1;
    private String propertyName = null;
    private int expenseId = -1;
    private String expenseName = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_expenses_edit);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.propertyId = -1;
            this.propertyName = null;
            this.expenseId = -1;
            this.expenseName = null;
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
            this.expenseId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_ID, -1);
            this.expenseName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_NAME, null);
        }

        this.propertyExpensesEditDescription = findViewById(R.id.propertyExpensesEditDescription);
        this.propertyExpensesEditDescriptionAlert = findViewById(R.id.propertyExpensesEditDescriptionAlert);
        this.propertyExpensesEditPropertyName = findViewById(R.id.propertyExpensesEditPropertyName);
        this.propertyExpensesEditPropertyNameAlert = findViewById(R.id.propertyExpensesEditPropertyNameAlert);
        this.propertyExpensesEditType = findViewById(R.id.propertyExpensesEditType);
        this.propertyExpensesEditTypeAlert = findViewById(R.id.propertyExpensesEditTypeAlert);
        this.propertyExpensesEditVendor = findViewById(R.id.propertyExpensesEditVendor);
        this.propertyExpensesEditVendorAlert = findViewById(R.id.propertyExpensesEditVendorAlert);
        this.propertyExpensesEditAmount = findViewById(R.id.propertyExpensesEditAmount);
        this.propertyExpensesEditAmountAlert = findViewById(R.id.propertyExpensesEditAmountAlert);
        this.propertyExpensesEditDateOfExpense = findViewById(R.id.propertyExpensesEditDateOfExpense);
        this.propertyExpensesEditDateOfExpenseAlert = findViewById(R.id.propertyExpensesEditDateOfExpenseAlert);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Date currentDate = new Date();
        Calendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTime(currentDate);
        this.propertyExpensesEditDateOfExpense.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(PropertyExpensesEditActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                propertyExpensesEditDateOfExpense.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        this.propertyExpensesEditUploadedFileName = findViewById(R.id.propertyExpensesEditUploadedFileName);
        this.propertyExpensesEditUploadedFile = findViewById(R.id.propertyExpensesEditUploadedFile);
        this.propertyExpensesEditUploadedFile.setOnClickListener(v -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            startActivityForResult(chooserIntent, Constants.REQUEST_CODE_PROPERTY_EXPENSES_IMAGE_DOCUMENT);
        });

        this.backgroundView = findViewById(R.id.propertyExpensesEditBackground);
        this.loadingSpinner = findViewById(R.id.propertyExpensesEditLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.propertyExpensesEditSaveButton = findViewById(R.id.propertyExpensesEditSaveButton);
        this.propertyExpensesEditSaveButton.setOnClickListener(v -> {
            this.doSavePropertyExpenses();
        });

        Toolbar mainToolbar = findViewById(R.id.propertyExpensesEditToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(this.expenseName);
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.mainMenuUser) {
//                Intent userProfileIntent = new Intent(PropertyExpensesEditActivity.this, UserProfileActivity.class);
//                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//            } else
            if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(PropertyExpensesEditActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshPropertyExpenses();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_PROPERTY_EXPENSES_IMAGE_DOCUMENT) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    if (data == null) {
                        return;
                    }
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    this.propertyExpensesEditUploadedFile.setImageBitmap(selectedImage);

                    Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    this.propertyExpensesEditUploadedFileName.setText(picturePath);
                    this.propertyExpensesEditUploadedFileName.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    private void doSavePropertyExpenses() {
        if (this.expenseId == -1) {
            this.savePropertyExpensesFailed(Constants.ERROR_COMMON);
            return;
        }
        if (this.propertyId == -1) {
            this.savePropertyExpensesFailed(Constants.ERROR_COMMON);
            return;
        }
        if (this.propertyName == null) {
            this.savePropertyExpensesFailed(Constants.ERROR_COMMON);
            return;
        }
        if (this.propertyExpensesEditDescription.length() <= 0) {
            this.propertyExpensesEditDescriptionAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesEditPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditTypeAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditVendorAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditAmountAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditDateOfExpenseAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditDescription.requestFocus();
            return;
        }
        if (this.propertyExpensesEditPropertyName.length() <= 0) {
            this.propertyExpensesEditDescriptionAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditPropertyNameAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesEditTypeAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditVendorAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditAmountAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditDateOfExpenseAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditPropertyName.requestFocus();
            return;
        }
        if (this.propertyExpensesEditType.length() <= 0) {
            this.propertyExpensesEditDescriptionAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditTypeAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesEditVendorAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditAmountAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditDateOfExpenseAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditType.requestFocus();
            return;
        }
        if (this.propertyExpensesEditVendor.length() <= 0) {
            this.propertyExpensesEditDescriptionAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditTypeAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditVendorAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesEditAmountAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditDateOfExpenseAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditVendor.requestFocus();
            return;
        }
        if (this.propertyExpensesEditDateOfExpense.length() <= 0) {
            this.propertyExpensesEditDescriptionAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditTypeAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditVendorAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditAmountAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesEditDateOfExpenseAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditDateOfExpense.requestFocus();
            return;
        }
        if (this.propertyExpensesEditDateOfExpense.length() <= 0) {
            this.propertyExpensesEditDescriptionAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditTypeAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditVendorAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditAmountAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesEditDateOfExpenseAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesEditDateOfExpense.requestFocus();
            return;
        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.savePropertyExpensesFailed("Please relogin.");
            return;
        }
        if (!(this.propertyExpensesEditUploadedFile.getDrawable() instanceof BitmapDrawable) || this.propertyExpensesEditUploadedFileName.getText().toString().equals("Empty")) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("asset_id", this.propertyId);
                postData.put("payment_description", this.propertyExpensesEditDescription.getText().toString());
                postData.put("vendor", this.propertyExpensesEditVendor.getText().toString());
                postData.put("amount", this.propertyExpensesEditAmount.getText().toString());
                postData.put("currency_iso", "MYR");
                postData.put("date_of_expense", this.propertyExpensesEditDateOfExpense.getText().toString());
                postData.put("expense_type", this.propertyExpensesEditType.getText().toString());
                postData.put("is_recurring", 1);
//            postData.put("file", );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Constants.URL_LANDLORD_PROPERTY_EXPENSES + "/" + this.expenseId, postData, response -> {
                savePropertyExpensesSuccess();
            }, error -> savePropertyExpensesFailed(Constants.ERROR_COMMON)) {
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
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, Constants.URL_LANDLORD_PROPERTY_EXPENSES + "/" + this.expenseId, response -> {
                savePropertyExpensesSuccess();
            }, error -> {
                try {
                    System.out.println("URL_LANDLORD_PROPERTY_EXPENSES PUT image error.networkResponse.data ==> " + new String(error.networkResponse.data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                savePropertyExpensesFailed(Constants.ERROR_COMMON);
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
                    params.put("payment_description", propertyExpensesEditDescription.getText().toString());
                    params.put("vendor", propertyExpensesEditVendor.getText().toString());
                    params.put("amount", propertyExpensesEditAmount.getText().toString());
                    params.put("currency_iso", "MYR");
                    params.put("date_of_expense", propertyExpensesEditDateOfExpense.getText().toString());
                    params.put("expense_type", propertyExpensesEditType.getText().toString());
                    params.put("is_recurring", Integer.toString(1));
                    return params;
                }

                @Override
                protected Map<String, FilePart> getFileData() throws AuthFailureError {
                    Map<String, FilePart> params = new HashMap<>();
                    String name = propertyExpensesEditUploadedFileName.getText().toString();
                    String type = name.substring(name.lastIndexOf(".") + 1).toUpperCase();

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) propertyExpensesEditUploadedFile.getDrawable();
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

    private void savePropertyExpensesFailed(String savePropertyExpensesFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder savePropertyExpensesFailedDialog = new AlertDialog.Builder(this);
        savePropertyExpensesFailedDialog.setCancelable(false);
        savePropertyExpensesFailedDialog.setTitle("Save Property Expenses Failed");
        savePropertyExpensesFailedDialog.setMessage(savePropertyExpensesFailed);
        savePropertyExpensesFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        savePropertyExpensesFailedDialog.create().show();
    }

    private void  savePropertyExpensesSuccess() {
        this.stopLoadingSpinner();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void refreshPropertyExpenses() {
        if (this.expenseId == -1) {
            getPropertyExpensesEditFailed(Constants.ERROR_COMMON);
            return;
        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getPropertyExpensesEditFailed("Please relogin.");
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PROPERTY_EXPENSES + "/" + this.expenseId, null, response -> {
            try {
                if (!response.has("data")) {
                    getPropertyExpensesEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (!dataJsonObject.has("id")) {
                    getPropertyExpensesEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                if (!dataJsonObject.has("fields")) {
                    getPropertyExpensesEditFailed(Constants.ERROR_COMMON);
                    return;
                }
                JSONObject dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                this.propertyExpensesEditDescription.setText(dataFieldsJsonObject.getString("payment_description"));
                this.propertyExpensesEditType.setText(dataFieldsJsonObject.getString("expense_type"));
                this.propertyExpensesEditVendor.setText(dataFieldsJsonObject.getString("vendor"));
                this.propertyExpensesEditAmount.setText(dataFieldsJsonObject.getString("amount"));
                this.propertyExpensesEditDateOfExpense.setText(dataFieldsJsonObject.getString("date_of_expense"));
                this.propertyExpensesEditPropertyName.setText(this.propertyName);
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
                getPropertyExpensesEditFailed(Constants.ERROR_COMMON);
            }
        }, error -> getPropertyExpensesEditFailed(Constants.ERROR_COMMON)) {
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

    private void displayImageFromUrl(String mediaTemporaryUrl) {
        this.startLoadingSpinner();
        ImageRequest imageRequest = new ImageRequest(mediaTemporaryUrl, response -> {
            propertyExpensesEditUploadedFile.setImageBitmap(response);
            this.stopLoadingSpinner();
        }, 0, 0, null, null, error -> {
            getPropertyExpensesEditFailed(Constants.ERROR_IMAGE_DOCUMENT_FILE_NOT_LOADED);
        });
        imageRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(imageRequest);
    }

    private void getPropertyExpensesEditFailed(String propertyExpensesEditFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder propertyExpensesEditFailedDialog = new AlertDialog.Builder(this);
        propertyExpensesEditFailedDialog.setCancelable(false);
        propertyExpensesEditFailedDialog.setTitle("Property Expenses Detail Failed");
        propertyExpensesEditFailedDialog.setMessage(propertyExpensesEditFailed);
        propertyExpensesEditFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        propertyExpensesEditFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.propertyExpensesEditSaveButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.propertyExpensesEditSaveButton.setEnabled(true);
    }
}
