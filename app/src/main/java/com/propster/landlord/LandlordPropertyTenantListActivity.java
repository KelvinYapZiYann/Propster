package com.propster.landlord;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.propster.R;

public class LandlordPropertyTenantListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_tenant_list);

        ListView propertyTenantListView = findViewById(R.id.landlordPropertyTenantList);

    }
}
