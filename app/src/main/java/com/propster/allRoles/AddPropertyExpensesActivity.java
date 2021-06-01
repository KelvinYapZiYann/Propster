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

public class AddPropertyExpensesActivity extends AppCompatActivity {

    private EditText propertyExpensesAddDescription;
    private TextView propertyExpensesAddDescriptionAlert;
    private EditText propertyExpensesAddPropertyName;
    private TextView propertyExpensesAddPropertyNameAlert;
    private EditText propertyExpensesAddType;
    private TextView propertyExpensesAddTypeAlert;
    private EditText propertyExpensesAddVendor;
    private TextView propertyExpensesAddVendorAlert;
    private EditText propertyExpensesAddAmount;
    private TextView propertyExpensesAddAmountAlert;
    private EditText propertyExpensesAddDateOfExpense;
    private TextView propertyExpensesAddDateOfExpenseAlert;
    private ShapeableImageView propertyExpensesAddUploadedFile;
    private TextView propertyExpensesAddUploadedFileName;

    private Button propertyExpensesAddExpensesButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int propertyId = -1;
    private String propertyName = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_expenses_add);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.propertyId = -1;
            this.propertyName = null;
        } else {
            this.propertyId = extras.getInt(Constants.INTENT_EXTRA_PROPERTY_ID, -1);
            this.propertyName = extras.getString(Constants.INTENT_EXTRA_PROPERTY_NAME, null);
        }

        this.propertyExpensesAddDescription = findViewById(R.id.propertyExpensesAddDescription);
        this.propertyExpensesAddDescriptionAlert = findViewById(R.id.propertyExpensesAddDescriptionAlert);
        this.propertyExpensesAddPropertyName = findViewById(R.id.propertyExpensesAddPropertyName);
        this.propertyExpensesAddPropertyNameAlert = findViewById(R.id.propertyExpensesAddPropertyNameAlert);
        this.propertyExpensesAddType = findViewById(R.id.propertyExpensesAddType);
        this.propertyExpensesAddTypeAlert = findViewById(R.id.propertyExpensesAddTypeAlert);
        this.propertyExpensesAddVendor = findViewById(R.id.propertyExpensesAddVendor);
        this.propertyExpensesAddVendorAlert = findViewById(R.id.propertyExpensesAddVendorAlert);
        this.propertyExpensesAddAmount = findViewById(R.id.propertyExpensesAddAmount);
        this.propertyExpensesAddAmountAlert = findViewById(R.id.propertyExpensesAddAmountAlert);
        this.propertyExpensesAddDateOfExpense = findViewById(R.id.propertyExpensesAddDateOfExpense);
        this.propertyExpensesAddDateOfExpenseAlert = findViewById(R.id.propertyExpensesAddDateOfExpenseAlert);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Date currentDate = new Date();
        Calendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTime(currentDate);
        this.propertyExpensesAddDateOfExpense.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddPropertyExpensesActivity.this, (view, year, month, dayOfMonth) -> {
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                propertyExpensesAddDateOfExpense.setText(sdf.format(currentCalendar.getTime()));
            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        this.propertyExpensesAddUploadedFile = findViewById(R.id.propertyExpensesAddUploadedFile);
        this.propertyExpensesAddUploadedFile.setOnClickListener(v -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

//            Intent pickIntent = new Intent(Intent.ACTION_PICK);
//            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            startActivityForResult(chooserIntent, Constants.REQUEST_CODE_PROPERTY_EXPENSES_IMAGE_DOCUMENT);
        });
        this.propertyExpensesAddUploadedFileName = findViewById(R.id.propertyExpensesAddUploadedFileName);

        this.backgroundView = findViewById(R.id.propertyExpensesAddBackground);
        this.loadingSpinner = findViewById(R.id.propertyExpensesAddLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.propertyExpensesAddExpensesButton = findViewById(R.id.propertyExpensesAddExpensesButton);
        this.propertyExpensesAddExpensesButton.setOnClickListener(v -> {
            this.doAddPropertyExpenses();
        });

        Toolbar mainToolbar = findViewById(R.id.propertyExpensesAddToolbar);
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
                Intent notificationIntent = new Intent(AddPropertyExpensesActivity.this, NotificationActivity.class);
                startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshPropertyExpensesPropertyName();
    }

    private void refreshPropertyExpensesPropertyName() {
        if (this.propertyName == null) {
            return;
        }
        this.propertyExpensesAddPropertyName.setText(this.propertyName);
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
        if (requestCode == Constants.REQUEST_CODE_PROPERTY_EXPENSES_IMAGE_DOCUMENT) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    if (data == null) {
                        return;
                    }
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    this.propertyExpensesAddUploadedFile.setImageBitmap(selectedImage);

                    Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    this.propertyExpensesAddUploadedFileName.setText(picturePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doAddPropertyExpenses() {
        if (this.propertyId == -1) {
            this.addPropertyExpensesFailed(Constants.ERROR_COMMON);
            return;
        }
        if (this.propertyName == null) {
            this.addPropertyExpensesFailed(Constants.ERROR_COMMON);
            return;
        }
        if (this.propertyExpensesAddDescription.length() <= 0) {
            this.propertyExpensesAddDescriptionAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddTypeAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddVendorAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddAmountAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddDateOfExpenseAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddDescription.requestFocus();
            return;
        }
        if (this.propertyExpensesAddPropertyName.length() <= 0) {
            this.propertyExpensesAddDescriptionAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddPropertyNameAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesAddTypeAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddVendorAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddAmountAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddDateOfExpenseAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddPropertyName.requestFocus();
            return;
        }
        if (this.propertyExpensesAddType.length() <= 0) {
            this.propertyExpensesAddDescriptionAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddTypeAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesAddVendorAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddAmountAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddDateOfExpenseAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddType.requestFocus();
            return;
        }
        if (this.propertyExpensesAddVendor.length() <= 0) {
            this.propertyExpensesAddDescriptionAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddTypeAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddVendorAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesAddAmountAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddDateOfExpenseAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddVendor.requestFocus();
            return;
        }
        if (this.propertyExpensesAddDateOfExpense.length() <= 0) {
            this.propertyExpensesAddDescriptionAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddTypeAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddVendorAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddAmountAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesAddDateOfExpenseAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddDateOfExpense.requestFocus();
            return;
        }
        if (this.propertyExpensesAddDateOfExpense.length() <= 0) {
            this.propertyExpensesAddDescriptionAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddPropertyNameAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddTypeAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddVendorAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddAmountAlert.setVisibility(View.INVISIBLE);
            this.propertyExpensesAddDateOfExpenseAlert.setVisibility(View.VISIBLE);
            this.propertyExpensesAddDateOfExpense.requestFocus();
            return;
        }
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.addPropertyExpensesFailed("Please relogin.");
            return;
        }
        if (this.propertyExpensesAddUploadedFileName.getText().equals("Empty") || !(this.propertyExpensesAddUploadedFile.getDrawable() instanceof BitmapDrawable)) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("asset_id", this.propertyId);
                postData.put("payment_description", this.propertyExpensesAddDescription.getText().toString());
                postData.put("vendor", this.propertyExpensesAddVendor.getText().toString());
                postData.put("amount", this.propertyExpensesAddAmount.getText().toString());
                postData.put("currency_iso", "MYR");
                postData.put("date_of_expense", this.propertyExpensesAddDateOfExpense.getText().toString());
                postData.put("expense_type", this.propertyExpensesAddType.getText().toString());
                postData.put("is_recurring", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LANDLORD_PROPERTY_EXPENSES, postData, response -> {
                addPropertyExpensesSuccess();
            }, error -> {
                try {
                    System.out.println("error.networkResponse.data ==> " + new String(error.networkResponse.data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addPropertyExpensesFailed(Constants.ERROR_COMMON);
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
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.URL_LANDLORD_PROPERTY_EXPENSES, response -> {
                addPropertyExpensesSuccess();
            }, error -> {
                try {
                    System.out.println("error.networkResponse.data ==> " + new String(error.networkResponse.data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addPropertyExpensesFailed(Constants.ERROR_COMMON);
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
                    params.put("payment_description", propertyExpensesAddDescription.getText().toString());
                    params.put("vendor", propertyExpensesAddVendor.getText().toString());
                    params.put("amount", propertyExpensesAddAmount.getText().toString());
                    params.put("currency_iso", "MYR");
                    params.put("date_of_expense", propertyExpensesAddDateOfExpense.getText().toString());
                    params.put("expense_type", propertyExpensesAddType.getText().toString());
                    params.put("is_recurring", Integer.toString(1));
                    return params;
                }

                @Override
                protected Map<String, FilePart> getFileData() throws AuthFailureError {
                    Map<String, FilePart> params = new HashMap<>();
                    String name = propertyExpensesAddUploadedFileName.getText().toString();
                    String type = name.substring(name.lastIndexOf(".") + 1).toUpperCase();

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) propertyExpensesAddUploadedFile.getDrawable();
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

    private void addPropertyExpensesFailed(String addPropertyExpensesFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder addPropertyExpensesFailedDialog = new AlertDialog.Builder(this);
        addPropertyExpensesFailedDialog.setCancelable(false);
        addPropertyExpensesFailedDialog.setTitle("Add Property Expenses Failed");
        addPropertyExpensesFailedDialog.setMessage(addPropertyExpensesFailed);
        addPropertyExpensesFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        addPropertyExpensesFailedDialog.create().show();
    }

    private void addPropertyExpensesSuccess() {
        this.stopLoadingSpinner();
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.propertyExpensesAddExpensesButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.propertyExpensesAddExpensesButton.setEnabled(true);
    }
}
