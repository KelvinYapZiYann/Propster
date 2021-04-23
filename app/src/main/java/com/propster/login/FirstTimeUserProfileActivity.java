package com.propster.login;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.propster.R;
import com.propster.utils.Constants;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class FirstTimeUserProfileActivity extends AppCompatActivity {

    public static final String[] TITLE = {"Mr", "Mrs/Ms"};
    public static final String[] IS_BUSINESS = {"Personal", "Company"};

    private ImageButton userProfileImage;

    private EditText userProfileEmail;
    private TextView userProfileEmailAlert;
    private EditText userProfilePhoneNumber;
    private TextView userProfilePhoneNumberAlert;
    private Spinner userProfileTitle;
    private EditText userProfileFirstName;
    private TextView userProfileFirstNameAlert;
    private EditText userProfileLastName;
    private TextView userProfileLastNameAlert;
    private EditText userProfileDateOfBirth;
    private TextView userProfileDateOfBirthAlert;
    private Spinner userProfileIsBusiness;

    private Button userProfileSaveButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_user_profile);

        this.userProfileImage = findViewById(R.id.firstTimeUserProfileImage);

        this.userProfileEmail = findViewById(R.id.firstTimeUserProfileEmail);
        this.userProfileEmailAlert = findViewById(R.id.firstTimeUserProfileEmailAlert);
        this.userProfilePhoneNumber = findViewById(R.id.firstTimeUserProfilePhoneNumber);
        this.userProfilePhoneNumberAlert = findViewById(R.id.firstTimeUserProfilePhoneNumberAlert);
        this.userProfileTitle = findViewById(R.id.firstTimeUserProfileTitle);
        this.userProfileFirstName = findViewById(R.id.firstTimeUserProfileFirstName);
        this.userProfileFirstNameAlert = findViewById(R.id.firstTimeUserProfileFirstNameAlert);
        this.userProfileLastName = findViewById(R.id.firstTimeUserProfileLastName);
        this.userProfileLastNameAlert = findViewById(R.id.firstTimeUserProfileLastNameAlert);
        this.userProfileDateOfBirth = findViewById(R.id.firstTimeUserProfileDateOfBirth);
        this.userProfileDateOfBirthAlert = findViewById(R.id.firstTimeUserProfileDateOfBirthAlert);
        this.userProfileIsBusiness = findViewById(R.id.firstTimeUserProfileIsBusiness);

        this.backgroundView = findViewById(R.id.firstTimeUserProfileBackground);
        this.loadingSpinner = findViewById(R.id.firstTimeUserProfileLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        ArrayAdapter<String> titleArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, TITLE);
        titleArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.userProfileTitle.setAdapter(titleArrayAdapter);

        ArrayAdapter<String> isBusinessArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, IS_BUSINESS);
        isBusinessArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        this.userProfileIsBusiness.setAdapter(isBusinessArrayAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        Date currentDate = new Date();
        Calendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTime(currentDate);
        this.userProfileDateOfBirth.setText(sdf.format(currentDate));
        this.userProfileDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(FirstTimeUserProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        currentCalendar.set(Calendar.YEAR, year);
                        currentCalendar.set(Calendar.MONTH, month);
                        currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        userProfileDateOfBirth.setText(sdf.format(currentCalendar.getTime()));
                    }
                }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        this.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doChooseImage();
            }
        });

        this.userProfileSaveButton = findViewById(R.id.firstTimeUserProfileSaveButton);
        this.userProfileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSaveUserProfile();
            }
        });

//        Button userProfileBackButton = findViewById(R.id.firstTimeUserProfileBackButton);
//        userProfileBackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

    }

    private void doChooseImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, Constants.REQUEST_CODE_FIRST_LOGIN_USER_PROFILE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_FIRST_LOGIN_USER_PROFILE_IMAGE) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                this.userProfileImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void doSaveUserProfile() {
        if (this.userProfileEmail.length() <= 0) {
            this.userProfileEmailAlert.setVisibility(View.VISIBLE);
            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfileEmail.requestFocus();
            return;
        }
        if (this.userProfilePhoneNumber.length() <= 0) {
            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfilePhoneNumberAlert.setVisibility(View.VISIBLE);
            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfilePhoneNumber.requestFocus();
            return;
        }
        if (this.userProfileFirstName.length() <= 0) {
            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileFirstNameAlert.setVisibility(View.VISIBLE);
            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfileFirstName.requestFocus();
            return;
        }
        if (this.userProfileLastName.length() <= 0) {
            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileLastNameAlert.setVisibility(View.VISIBLE);
            this.userProfileDateOfBirthAlert.setVisibility(View.INVISIBLE);
            this.userProfileLastName.requestFocus();
            return;
        }
        if (this.userProfileDateOfBirth.length() <= 0) {
            this.userProfileEmailAlert.setVisibility(View.INVISIBLE);
            this.userProfilePhoneNumberAlert.setVisibility(View.INVISIBLE);
            this.userProfileFirstNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileLastNameAlert.setVisibility(View.INVISIBLE);
            this.userProfileDateOfBirthAlert.setVisibility(View.VISIBLE);
            this.userProfileLastName.requestFocus();
            return;
        }

        this.startLoadingSpinner();

//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("email", this.userProfileEmail.getText().toString());
//            postData.put("phone_number", this.userProfilePhoneNumber.getText().toString());
//            postData.put("title", (String) this.userProfileTitle.getSelectedItem());
//            postData.put("first_name", this.userProfileFirstName.getText().toString());
//            postData.put("last_name", this.userProfileLastName.getText().toString());
//            postData.put("date_of_birth", this.userProfileDateOfBirth.getText().toString());
//            postData.put("is_business", this.userProfileIsBusiness.getSelectedItemId() != 0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_SAVE_USER_PROFILE, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//                saveUserProfileSuccess();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                saveUserProfileFailed(error.getLocalizedMessage());
//                error.printStackTrace();
//            }
//        });
//        this.requestQueue.add(jsonObjectRequest);
        saveUserProfileSuccess();
    }

    private void saveUserProfileSuccess() {
        this.stopLoadingSpinner();
        Intent roleSelectionIntent = new Intent(this, FirstTimeRoleSelectionActivity.class);
        startActivity(roleSelectionIntent);
        finish();
    }

    private void saveUserProfileFailed(String saveUserProfileFailedCause) {
        this.stopLoadingSpinner();
        AlertDialog.Builder saveUserProfileFailedDialog = new AlertDialog.Builder(this);
        saveUserProfileFailedDialog.setCancelable(false);
        saveUserProfileFailedDialog.setTitle("User Profile Failed");
        saveUserProfileFailedDialog.setMessage(saveUserProfileFailedCause);
        saveUserProfileFailedDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveUserProfileFailedDialog.create().show();
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.userProfileSaveButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.userProfileSaveButton.setEnabled(true);
    }



}
