package com.propster.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.propster.R;

public class FirstTimeRoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_role_selection);

        Button roleSelectionLandlordButton = findViewById(R.id.firstTimeRoleSelectionLandlordButton);
        roleSelectionLandlordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent firstTimeLandlordIntent = new Intent(FirstTimeRoleSelectionActivity.this, );
                startActivity(firstTimeLandlordIntent);
            }
        });

        Button roleSelectionTenantButton = findViewById(R.id.firstTimeRoleSelectionTenantButton);
        roleSelectionTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent firstTimeTenantIntent = new Intent(FirstTimeRoleSelectionActivity.this, );
                startActivity(firstTimeTenantIntent);
            }
        });
    }
}
