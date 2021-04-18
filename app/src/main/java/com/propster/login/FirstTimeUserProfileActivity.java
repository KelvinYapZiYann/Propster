package com.propster.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.propster.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirstTimeUserProfileActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FIRST_LOGIN_USER_PROFILE_IMAGE = 1000;
    public static final String[] TITLE = {"Mr", "Mrs/Ms"};
    public static final String[] IS_BUSINESS = {"Personal", "Company"};

    private ImageView userProfileImage;

    private EditText userProfileEmail;
    private EditText userProfileNumber;
    private Spinner userProfileTitle;
    private EditText userProfileFirstName;
    private EditText userProfileLastName;
    private EditText userProfileDateOfBirth;
    private Spinner userProfileIsBusiness;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_user_profile);

        this.userProfileImage = findViewById(R.id.firstTimeUserProfileImage);

        this.userProfileEmail = findViewById(R.id.firstTimeUserProfileEmail);
        this.userProfileNumber = findViewById(R.id.firstTimeUserProfilePhoneNumber);
        this.userProfileTitle = findViewById(R.id.firstTimeUserProfileTitle);
        this.userProfileFirstName = findViewById(R.id.firstTimeUserProfileFirstName);
        this.userProfileLastName = findViewById(R.id.firstTimeUserProfileLastName);
        this.userProfileDateOfBirth = findViewById(R.id.firstTimeUserProfileDateOfBirth);
        this.userProfileIsBusiness = findViewById(R.id.firstTimeUserProfileIsBusiness);

        ArrayAdapter<String> titleArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TITLE);
        titleArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.userProfileTitle.setAdapter(titleArrayAdapter);

        ArrayAdapter<String> isBusinessArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, IS_BUSINESS);
        isBusinessArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.userProfileIsBusiness.setAdapter(isBusinessArrayAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.UK);
        this.userProfileDateOfBirth.setText(sdf.format( new Date()));

        Button userProfileChooseImageButton = findViewById(R.id.firstTimeUserProfileImageButton);
        userProfileChooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doChooseImage();
            }
        });

        Button userProfileSaveButton = findViewById(R.id.firstTimeUserProfileSaveButton);
        userProfileSaveButton.setOnClickListener(new View.OnClickListener() {
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

        startActivityForResult(chooserIntent, REQUEST_CODE_FIRST_LOGIN_USER_PROFILE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FIRST_LOGIN_USER_PROFILE_IMAGE) {
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

    }
}
