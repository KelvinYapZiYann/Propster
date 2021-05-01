package com.propster.landlord;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.content.UserProfileActivity;
import com.propster.utils.Constants;

public class LandlordPropertyDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_detail);

        Toolbar mainToolbar = findViewById(R.id.landlordPropertyDetailToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.mainMenuUser) {
                    Intent userProfileIntent = new Intent(LandlordPropertyDetailActivity.this, UserProfileActivity.class);
                    startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
                } else if (item.getItemId() == R.id.mainMenuNotification) {
                    Intent notificationIntent = new Intent(LandlordPropertyDetailActivity.this, NotificationActivity.class);
                    startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
                }
                return false;
            }
        });
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }
}
