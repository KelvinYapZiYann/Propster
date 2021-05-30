package com.propster.tenant;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.content.UserProfileActivity;
import com.propster.landlord.LandlordPropertyAddTenantActivity;
import com.propster.landlord.LandlordPropertyListActivity;
import com.propster.landlord.LandlordPropertyListAdapter;
import com.propster.landlord.LandlordPropertyListItem;
import com.propster.landlord.LandlordPropertyTenantListActivity;
import com.propster.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TenantPropertyListActivity extends AppCompatActivity {

    private TextView tenantManageFirstTimeLoginLabel;
    private TenantPropertyListAdapter propertyListAdapter;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private Button tenantManageAddPropertyButton;

    private RequestQueue requestQueue;

    private int tmp = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_property_list);

        boolean firstTime;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            firstTime = false;
        } else {
            firstTime = extras.getBoolean(Constants.INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN);
        }

        this.backgroundView = findViewById(R.id.tenantPropertyListBackground);
        this.loadingSpinner = findViewById(R.id.tenantPropertyListLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.tenantManageFirstTimeLoginLabel = findViewById(R.id.tenantManageFirstTimeLoginLabel);
        ListView tenantManagePropertyList = findViewById(R.id.tenantManagePropertyList);
        ArrayList<TenantPropertyListItem> propertyListItemArrayList = new ArrayList<>();
        this.propertyListAdapter = new TenantPropertyListAdapter(this, propertyListItemArrayList);
        tenantManagePropertyList.setAdapter(this.propertyListAdapter);
        tenantManagePropertyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent tenantPropertyDetailIntent = new Intent(TenantPropertyListActivity.this, TenantPropertyDetailActivity.class);
//                propertyTenantListIntent.putExtra(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST, "");
                startActivity(tenantPropertyDetailIntent);
            }
        });

        this.tenantManageAddPropertyButton = findViewById(R.id.tenantManageAddPropertyButton);
        this.tenantManageAddPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder tenantAddPropertyDialog = new AlertDialog.Builder(TenantPropertyListActivity.this);
                tenantAddPropertyDialog.setTitle("Add Tenant");
                tenantAddPropertyDialog.setItems(R.array.add_property_type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                        } else {
                            addPropertyItemIntoList("something new", tmp++, tmp++, 30);
//                Intent landlordAddPropertyIntent = new Intent(LandlordPropertyListActivity.this, LandlordAddPropertyActivity.class);
//                startActivityForResult(landlordAddPropertyIntent, Constants.REQUEST_CODE_LANDLORD_ADD_PROPERTY);
                        }
                    }
                });
                tenantAddPropertyDialog.create().show();
            }
        });
        this.refreshPropertyListView();
        if (this.propertyListAdapter.getCount() <= 0) {
            if (firstTime) {
                this.tenantManageFirstTimeLoginLabel.setVisibility(View.VISIBLE);
            } else {
                this.tenantManageFirstTimeLoginLabel.setVisibility(View.GONE);
            }
        }

        Toolbar mainToolbar = findViewById(R.id.tenantPropertyListToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getItemId() == R.id.mainMenuUser) {
//                    Intent userProfileIntent = new Intent(TenantPropertyListActivity.this, UserProfileActivity.class);
//                    startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//                } else
                if (item.getItemId() == R.id.mainMenuNotification) {
                    Intent notificationIntent = new Intent(TenantPropertyListActivity.this, NotificationActivity.class);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_TENANT_ADD_PROPERTY) {
            if (resultCode == Activity.RESULT_OK) {
                this.tenantManageFirstTimeLoginLabel.setVisibility(View.GONE);
                this.refreshPropertyListView();
            }
        }
    }

    private void refreshPropertyListView() {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.tenantManageGetPropertyListFailed("Please relogin.");
            return;
        }

//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("session_id", sessionId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_TENANT_PROPERTY_LIST, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//                try {
//                    tenantManageGetPropertyListSuccess(response.getJSONArray("property_list"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    tenantManageGetPropertyListFailed(e.getLocalizedMessage());
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                tenantManageGetPropertyListFailed(error.getLocalizedMessage());
//                error.printStackTrace();
//            }
//        });
//        this.requestQueue.add(jsonObjectRequest);

        JSONArray propertyListJasonArray = new JSONArray();
        this.tenantManageGetPropertyListSuccess(propertyListJasonArray);

    }

    private void tenantManageGetPropertyListSuccess(JSONArray propertyListJasonArray) {
        this.refreshPropertyList();
        this.stopLoadingSpinner();
        try {
            JSONObject propertyItemJsonObject;
            for (int i = 0; i < propertyListJasonArray.length(); i++) {
                propertyItemJsonObject = propertyListJasonArray.getJSONObject(i);
                this.addPropertyItemIntoList(propertyItemJsonObject.getString("property_name"), propertyItemJsonObject.getInt("property_id"),
                        (float) propertyItemJsonObject.getDouble("payment"), propertyItemJsonObject.getInt("age"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tenantManageGetPropertyListFailed(String tenantGetPropertyListFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Property List Failed");
        loginFailedDialog.setMessage(tenantGetPropertyListFailed);
        loginFailedDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        loginFailedDialog.create().show();
    }

    private void refreshPropertyList() {
        if (this.propertyListAdapter == null) {
            return;
        }
        this.propertyListAdapter.clear();
    }

    private void addPropertyItemIntoList(String propertyName, int propertyId, float payment, int age) {
        if (this.propertyListAdapter == null) {
            return;
        }
        this.propertyListAdapter.add(new TenantPropertyListItem(propertyName, propertyId, payment, age));
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.tenantManageAddPropertyButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.tenantManageAddPropertyButton.setEnabled(true);
    }
}
