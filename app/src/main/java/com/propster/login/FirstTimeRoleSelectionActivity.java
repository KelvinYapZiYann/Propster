package com.propster.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.propster.R;
import com.propster.content.ContentActivity;
import com.propster.utils.Constants;

public class FirstTimeRoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_role_selection);

        Button roleSelectionLandlordButton = findViewById(R.id.firstTimeRoleSelectionLandlordButton);
        roleSelectionLandlordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_LANDLORD);
                editor.apply();
//                Intent firstTimeLandlordIntent = new Intent(FirstTimeRoleSelectionActivity.this, FirstTimeLandlordActivity.class);
//                startActivity(firstTimeLandlordIntent);
                Intent contentIntent = new Intent(FirstTimeRoleSelectionActivity.this, ContentActivity.class);
                contentIntent.putExtra(Constants.INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN, true);
                startActivity(contentIntent);
                finish();
            }
        });

        Button roleSelectionTenantButton = findViewById(R.id.firstTimeRoleSelectionTenantButton);
        roleSelectionTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
                editor.apply();
//                Intent firstTimeTenantIntent = new Intent(FirstTimeRoleSelectionActivity.this, );
//                startActivity(firstTimeTenantIntent);
                Intent contentIntent = new Intent(FirstTimeRoleSelectionActivity.this, ContentActivity.class);
                startActivity(contentIntent);
                finish();
            }
        });
    }
}
