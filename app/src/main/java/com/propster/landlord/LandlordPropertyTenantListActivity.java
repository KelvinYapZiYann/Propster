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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.content.UserProfileActivity;
import com.propster.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LandlordPropertyTenantListActivity extends AppCompatActivity {

    private LandlordPropertyTenantListAdapter propertyTenantListAdapter;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private Button landlordManagePropertyAddTenantButton;
    private Button landlordManagePropertyDetailButton;
    private Button landlordManagePropertyExpensesButton;
    private Button landlordManagePropertyPaymentRecordsButton;
    private Button landlordManagePropertyTenureContractButton;
    private Button landlordManagePropertyRemovePropertyButton;

    private IntentIntegrator qrScanIntentIntegrator;

    private RequestQueue requestQueue;

    private int tmp = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_tenant_list);

        String tenantListAllTenants;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            tenantListAllTenants = null;
        } else {
            tenantListAllTenants = extras.getString(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_ALL_TENANTS, null);
        }

        this.backgroundView = findViewById(R.id.landlordPropertyTenantListBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyTenantListLoadingSpinner);

        this.qrScanIntentIntegrator = new IntentIntegrator(this);
        this.qrScanIntentIntegrator.setRequestCode(Constants.REQUEST_CODE_LANDLORD_PROPERTY_ADD_TENANT);
        this.qrScanIntentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        this.qrScanIntentIntegrator.setPrompt("Scan Tenant's QR Code");
        this.qrScanIntentIntegrator.setOrientationLocked(false);

        this.requestQueue = Volley.newRequestQueue(this);

        ArrayList<LandlordPropertyTenantListItem> tenantListItemList = new ArrayList<>();
        this.propertyTenantListAdapter = new LandlordPropertyTenantListAdapter(this, tenantListItemList);
        ListView propertyTenantListView = findViewById(R.id.landlordPropertyTenantList);
        propertyTenantListView.setAdapter(this.propertyTenantListAdapter);
        propertyTenantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent propertyTenantListDetail = new Intent(LandlordPropertyTenantListActivity.this, LandlordPropertyTenantDetailActivity.class);
                startActivity(propertyTenantListDetail);
            }
        });
        this.landlordManagePropertyAddTenantButton = findViewById(R.id.landlordManagePropertyAddTenantButton);
        this.landlordManagePropertyAddTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder propertyAddTenantDialog = new AlertDialog.Builder(LandlordPropertyTenantListActivity.this);
                propertyAddTenantDialog.setTitle("Add Tenant");
                propertyAddTenantDialog.setItems(R.array.add_tenant_type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            qrScanIntentIntegrator.initiateScan();
                        } else {
                            addTenantItemIntoList("new tenant", "2020/2/4", 200, tmp++);
//                Intent landlordPropertyAddTenantIntent = new Intent(LandlordPropertyTenantListActivity.this, LandlordPropertyAddTenantActivity.class);
//                startActivityForResult(landlordPropertyAddTenantIntent, Constants.REQUEST_CODE_LANDLORD_PROPERTY_ADD_TENANT);
                        }
                    }
                });
                propertyAddTenantDialog.create().show();
            }
        });
        this.landlordManagePropertyDetailButton = findViewById(R.id.landlordManagePropertyAddTenantDetailButton);
        this.landlordManagePropertyDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent propertyDetailIntent = new Intent(LandlordPropertyTenantListActivity.this, LandlordPropertyDetailActivity.class);
                startActivity(propertyDetailIntent);
            }
        });
        this.landlordManagePropertyExpensesButton = findViewById(R.id.landlordManagePropertyAddTenantExpensesButton);
        this.landlordManagePropertyExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        this.landlordManagePropertyPaymentRecordsButton = findViewById(R.id.landlordManagePropertyAddTenantPaymentRecordsButton);
        this.landlordManagePropertyPaymentRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        this.landlordManagePropertyTenureContractButton = findViewById(R.id.landlordManagePropertyAddTenantTenureContractButton);
        this.landlordManagePropertyTenureContractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        this.landlordManagePropertyRemovePropertyButton = findViewById(R.id.landlordManagePropertyAddTenanRemovePropertyButton);
        this.landlordManagePropertyRemovePropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (tenantListAllTenants != null && tenantListAllTenants.equals(Constants.INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_ALL_TENANTS)) {
            this.landlordManagePropertyAddTenantButton.setVisibility(View.GONE);
            this.landlordManagePropertyDetailButton.setVisibility(View.GONE);
            this.landlordManagePropertyExpensesButton.setVisibility(View.GONE);
            this.landlordManagePropertyPaymentRecordsButton.setVisibility(View.GONE);
            this.landlordManagePropertyTenureContractButton.setVisibility(View.GONE);
            this.landlordManagePropertyRemovePropertyButton.setVisibility(View.GONE);
        } else {
            this.landlordManagePropertyAddTenantButton.setVisibility(View.VISIBLE);
            this.landlordManagePropertyDetailButton.setVisibility(View.VISIBLE);
            this.landlordManagePropertyExpensesButton.setVisibility(View.VISIBLE);
            this.landlordManagePropertyPaymentRecordsButton.setVisibility(View.VISIBLE);
            this.landlordManagePropertyTenureContractButton.setVisibility(View.VISIBLE);
            this.landlordManagePropertyRemovePropertyButton.setVisibility(View.VISIBLE);
        }

        Toolbar mainToolbar = findViewById(R.id.landlordManagePropertyTenantListToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.mainMenuUser) {
                    Intent userProfileIntent = new Intent(LandlordPropertyTenantListActivity.this, UserProfileActivity.class);
                    startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
                } else if (item.getItemId() == R.id.mainMenuNotification) {
                    Intent notificationIntent = new Intent(LandlordPropertyTenantListActivity.this, NotificationActivity.class);
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

        this.refreshTenantListView();

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
        if (requestCode == Constants.REQUEST_CODE_LANDLORD_ADD_PROPERTY) {
            if (resultCode == Activity.RESULT_OK) {
                this.refreshTenantListView();
            }
        } else if (requestCode == Constants.REQUEST_CODE_LANDLORD_PROPERTY_ADD_TENANT) {
            System.out.println("please...");
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                try {
//                JSONObject qrScanJsonObject = new JSONObject();
                    System.out.println("result.getContents() = " + result.getContents());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("result == null");
            }
        }
    }

    private void refreshTenantListView() {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.landlordManagePropertyGetTenantListFailed("Please relogin.");
            return;
        }

//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("session_id", sessionId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LANDLORD_PROPERTY_TENANT_LIST, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//                try {
//                    landlordManagePropertyGetTenantListSuccess(response.getJSONArray("tenant_list"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    landlordManagePropertyGetTenantListFailed(e.getLocalizedMessage());
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                landlordManagePropertyGetTenantListFailed(error.getLocalizedMessage());
//                error.printStackTrace();
//            }
//        });
//        this.requestQueue.add(jsonObjectRequest);

        JSONArray tenantListJasonArray = new JSONArray();
        this.landlordManagePropertyGetTenantListSuccess(tenantListJasonArray);

    }

    private void landlordManagePropertyGetTenantListSuccess(JSONArray tenantListJasonArray) {
        this.refreshTenantList();
        this.stopLoadingSpinner();
        try {
            JSONObject propertyItemJsonObject;
            for (int i = 0; i < tenantListJasonArray.length(); i++) {
                propertyItemJsonObject = tenantListJasonArray.getJSONObject(i);
                this.addTenantItemIntoList(propertyItemJsonObject.getString("tenant_name"), propertyItemJsonObject.getString("tenure_end_date"),
                        (float) propertyItemJsonObject.getDouble("payment"), propertyItemJsonObject.getInt("age"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void landlordManagePropertyGetTenantListFailed(String landlordGetPropertyListFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder loginFailedDialog = new AlertDialog.Builder(this);
        loginFailedDialog.setCancelable(false);
        loginFailedDialog.setTitle("Tenant List Failed");
        loginFailedDialog.setMessage(landlordGetPropertyListFailed);
        loginFailedDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        loginFailedDialog.create().show();
    }

    private void refreshTenantList() {
        if (this.propertyTenantListAdapter == null) {
            return;
        }
        this.propertyTenantListAdapter.clear();
    }

    private void addTenantItemIntoList(String tenantName, String tenureEndDate, float payment, int age) {
        if (this.propertyTenantListAdapter == null) {
            return;
        }
        this.propertyTenantListAdapter.add(new LandlordPropertyTenantListItem(tenantName, tenureEndDate, payment, age));
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.landlordManagePropertyAddTenantButton.setEnabled(false);
        this.landlordManagePropertyDetailButton.setEnabled(false);
        this.landlordManagePropertyExpensesButton.setEnabled(false);
        this.landlordManagePropertyPaymentRecordsButton.setEnabled(false);
        this.landlordManagePropertyTenureContractButton.setEnabled(false);
        this.landlordManagePropertyRemovePropertyButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordManagePropertyAddTenantButton.setEnabled(true);
        this.landlordManagePropertyDetailButton.setEnabled(true);
        this.landlordManagePropertyExpensesButton.setEnabled(true);
        this.landlordManagePropertyPaymentRecordsButton.setEnabled(true);
        this.landlordManagePropertyTenureContractButton.setEnabled(true);
        this.landlordManagePropertyRemovePropertyButton.setEnabled(true);
    }
}
