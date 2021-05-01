package com.propster.landlord;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.propster.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LandlordPropertyListActivity extends AppCompatActivity {

    private TextView landlordManageFirstTimeLoginLabel;
    private LandlordPropertyListAdapter propertyListAdapter;

    private Button landlordManageAddPropertyButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private int tmp = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_list);

        boolean firstTime;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            firstTime = false;
        } else {
            firstTime = extras.getBoolean(Constants.INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN);
        }

        this.backgroundView = findViewById(R.id.landlordPropertyListBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyListLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.landlordManageFirstTimeLoginLabel = findViewById(R.id.landlordManageFirstTimeLoginLabel);
        ListView landlordManagePropertyList = findViewById(R.id.landlordManagePropertyList);
        ArrayList<LandlordPropertyListItem> propertyListItemArrayList = new ArrayList<>();
        this.propertyListAdapter = new LandlordPropertyListAdapter(this, propertyListItemArrayList);
        landlordManagePropertyList.setAdapter(this.propertyListAdapter);
        landlordManagePropertyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent propertyTenantListIntent = new Intent(LandlordPropertyListActivity.this, LandlordPropertyTenantListActivity.class);
//                propertyTenantListIntent.putExtra(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST, "");
                startActivity(propertyTenantListIntent);
            }
        });

        this.landlordManageAddPropertyButton = findViewById(R.id.landlordManageAddPropertyButton);
        this.landlordManageAddPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent landlordAddPropertyIntent = new Intent(LandlordPropertyListActivity.this, LandlordAddPropertyActivity.class);
//                startActivityForResult(landlordAddPropertyIntent, Constants.REQUEST_CODE_LANDLORD_ADD_PROPERTY);
                addPropertyItemIntoList("something new", tmp++, 2, 4, tmp++, 30);
                landlordManageFirstTimeLoginLabel.setVisibility(View.GONE);
            }
        });

        this.refreshPropertyListView();
        if (this.propertyListAdapter.getCount() <= 0) {
            if (firstTime) {
                this.landlordManageFirstTimeLoginLabel.setVisibility(View.VISIBLE);
            } else {
                this.landlordManageFirstTimeLoginLabel.setVisibility(View.GONE);
            }
        }
        Toolbar mainToolbar = findViewById(R.id.landlordManagePropertyListToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.mainMenuUser) {
                    Intent userProfileIntent = new Intent(LandlordPropertyListActivity.this, UserProfileActivity.class);
                    startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
                } else if (item.getItemId() == R.id.mainMenuNotification) {
                    Intent notificationIntent = new Intent(LandlordPropertyListActivity.this, NotificationActivity.class);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_LANDLORD_ADD_PROPERTY) {
            if (resultCode == Activity.RESULT_OK) {
                this.landlordManageFirstTimeLoginLabel.setVisibility(View.GONE);
                this.refreshPropertyListView();
            }
        }
    }

    private void refreshPropertyListView() {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.landlordManageGetPropertyListFailed("Please relogin.");
            return;
        }

//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("session_id", sessionId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LANDLORD_PROPERTY_LIST, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//                try {
//                    landlordManageGetPropertyListSuccess(response.getJSONArray("property_list"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    landlordManageGetPropertyListFailed(e.getLocalizedMessage());
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                landlordManageGetPropertyListFailed(error.getLocalizedMessage());
//                error.printStackTrace();
//            }
//        });
//        this.requestQueue.add(jsonObjectRequest);

        JSONArray propertyListJasonArray = new JSONArray();
        this.landlordManageGetPropertyListSuccess(propertyListJasonArray);

    }

    private void landlordManageGetPropertyListSuccess(JSONArray propertyListJasonArray) {
        this.refreshPropertyList();
        this.stopLoadingSpinner();
        try {
            JSONObject propertyItemJsonObject;
            for (int i = 0; i < propertyListJasonArray.length(); i++) {
                propertyItemJsonObject = propertyListJasonArray.getJSONObject(i);
                this.addPropertyItemIntoList(propertyItemJsonObject.getString("property_name"), propertyItemJsonObject.getInt("property_id"),
                        propertyItemJsonObject.getInt("tenant_count"), propertyItemJsonObject.getInt("total_tenant_count"),
                        (float) propertyItemJsonObject.getDouble("payment"), propertyItemJsonObject.getInt("age"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void landlordManageGetPropertyListFailed(String landlordGetPropertyListFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Property List Failed");
        loginFailedDialog.setMessage(landlordGetPropertyListFailed);
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

    private void addPropertyItemIntoList(String propertyName, int propertyId, int tenantCount, int totalTenantCount, float payment, int age) {
        if (this.propertyListAdapter == null) {
            return;
        }
        this.propertyListAdapter.add(new LandlordPropertyListItem(propertyName, propertyId, tenantCount, totalTenantCount, payment, age));
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.landlordManageAddPropertyButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordManageAddPropertyButton.setEnabled(true);
    }

}
