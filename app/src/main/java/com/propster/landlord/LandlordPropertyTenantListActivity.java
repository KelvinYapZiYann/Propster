package com.propster.landlord;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.propster.R;
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

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_property_tenant_list);

        this.backgroundView = findViewById(R.id.landlordPropertyTenantListBackground);
        this.loadingSpinner = findViewById(R.id.landlordPropertyTenantListLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        ArrayList<LandlordPropertyTenantListItem> tenantListItemList = new ArrayList<>();
        this.propertyTenantListAdapter = new LandlordPropertyTenantListAdapter(this, tenantListItemList);
        ListView propertyTenantListView = findViewById(R.id.landlordPropertyTenantList);
        propertyTenantListView.setAdapter(this.propertyTenantListAdapter);

        this.landlordManagePropertyAddTenantButton = findViewById(R.id.landlordPropertyAddTenantButton);
        this.landlordManagePropertyAddTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent landlordPropertyAddTenantIntent = new Intent(LandlordPropertyTenantListActivity.this, LandlordPropertyAddTenantActivity.class);
                startActivity(landlordPropertyAddTenantIntent);
            }
        });

        this.refreshPropertyListView();

    }

    private void refreshPropertyListView() {
        this.startLoadingSpinner();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.landlordManagePropertyGetTenantListFailed("Please relogin.");
            return;
        }

        JSONObject postData = new JSONObject();
        try {
            postData.put("session_id", sessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.URL_LANDLORD_PROPERTY_TENANT_LIST, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                try {
                    landlordManagePropertyGetTenantListSuccess(response.getJSONArray("tenant_list"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    landlordManagePropertyGetTenantListFailed(e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                landlordManagePropertyGetTenantListFailed(error.getLocalizedMessage());
                error.printStackTrace();
            }
        });
        this.requestQueue.add(jsonObjectRequest);

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
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.landlordManagePropertyAddTenantButton.setEnabled(true);
    }
}
