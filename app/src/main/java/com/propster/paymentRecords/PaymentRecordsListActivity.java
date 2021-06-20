package com.propster.paymentRecords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.propster.R;
import com.propster.content.NotificationActivity;
import com.propster.login.SplashActivity;
import com.propster.propertyExpenses.PropertyExpensesListItem;
import com.propster.utils.Constants;
import com.propster.utils.CurrencyConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentRecordsListActivity extends AppCompatActivity {

    private PaymentRecordsListAdapter paymentRecordsListAdapter;

    private Button paymentRecordsListAddRecordsButton;

    private View backgroundView;
    private ProgressBar loadingSpinner;

    private RequestQueue requestQueue;

    private String listPaymentRecordsType = null;

    private int paginationLastItem = 0;
    private boolean paginationHasNextPage = false;
    private int paginationId = 1;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_records_list);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.listPaymentRecordsType = null;
        } else {
            this.listPaymentRecordsType = extras.getString(Constants.INTENT_EXTRA_LIST_PAYMENT_RECORDS, null);
        }

        this.backgroundView = findViewById(R.id.paymentRecordsListBackground);
        this.loadingSpinner = findViewById(R.id.paymentRecordsListLoadingSpinner);

        this.requestQueue = Volley.newRequestQueue(this);

        this.paginationLastItem = 0;
        this.paginationHasNextPage = false;
        this.paginationId = 1;

        ListView paymentRecordsList = findViewById(R.id.paymentRecordsList);
        ArrayList<PaymentRecordsListItem> paymentRecordsListItemArrayList = new ArrayList<>();
        this.paymentRecordsListAdapter = new PaymentRecordsListAdapter(this, paymentRecordsListItemArrayList);
        paymentRecordsList.setAdapter(this.paymentRecordsListAdapter);
        paymentRecordsList.setOnItemClickListener((parent, view, position, id) -> {
//            PaymentRecordsListItem paymentRecordsListItem = ((PaymentRecordsListAdapter) parent.getAdapter()).getItem(position);
//            Intent propertyTenantDetailIntent = new Intent(PaymentRecordsListActivity.this, PaymentRecordsDetailActivity.class);
//            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, paymentRecordsListItem.getPropertyId());
//            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, paymentRecordsListItem.getPropertyName());
//            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_ID, paymentRecordsListItem.getpaymentRecordsId());
//            propertyTenantDetailIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_EXPENSES_NAME, paymentRecordsListItem.getpaymentRecordsDescription());
//            startActivityForResult(propertyTenantDetailIntent, Constants.REQUEST_CODE_PROPERTY_EXPENSES_DETAIL);
        });
        paymentRecordsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if(lastItem == totalItemCount) {
                    if(paginationLastItem != lastItem) {
                        paginationLastItem = lastItem;
                        if (paginationHasNextPage) {
                            getPaymentRecords();
                        }
                    }
                }
            }
        });

        this.paymentRecordsListAddRecordsButton = findViewById(R.id.paymentRecordsListAddRecordsButton);
        this.paymentRecordsListAddRecordsButton.setOnClickListener(v -> {
//            Intent addPaymentRecordsIntent = new Intent(paymentRecordsListActivity.this, AddpaymentRecordsActivity.class);
//            addPaymentRecordsIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_ID, this.propertyId);
//            addPaymentRecordsIntent.putExtra(Constants.INTENT_EXTRA_PROPERTY_NAME, this.propertyName);
//            startActivityForResult(addPaymentRecordsIntent, Constants.REQUEST_CODE_ADD_PROPERTY_EXPENSES);
        });

        if (this.isShowingAllPaymentRecords()) {
            this.paymentRecordsListAddRecordsButton.setVisibility(View.GONE);
        } else {
            this.paymentRecordsListAddRecordsButton.setVisibility(View.VISIBLE);
        }

        Toolbar mainToolbar = findViewById(R.id.paymentRecordsListToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            if (this.isShowingAllPaymentRecords()) {
                getSupportActionBar().setTitle(R.string.app_name);
            } else {
//                getSupportActionBar().setTitle(this.propertyName);
            }
        }
        mainToolbar.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.mainMenuUser) {
//                Intent userProfileIntent = new Intent(paymentRecordsListActivity.this, UserProfileActivity.class);
//                startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//            } else
            if (item.getItemId() == R.id.mainMenuNotification) {
                Intent notificationIntent = new Intent(PaymentRecordsListActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
            }
            return false;
        });
        mainToolbar.setNavigationOnClickListener(v -> finish());

        this.refreshPaymentRecordsList();
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
        if (requestCode == Constants.REQUEST_CODE_PAYMENT_RECORDS_DETAIL) {
            if (resultCode == Activity.RESULT_OK) {
                this.refreshPaymentRecordsList();
            }
        } else if (requestCode == Constants.REQUEST_CODE_ADD_PAYMENT_RECORDS) {
            if (resultCode == Activity.RESULT_OK) {
                this.refreshPaymentRecordsList();
            }
        }
    }

    private void refreshPaymentRecordsList() {
        this.startLoadingSpinner();
        this.paginationId = 1;
        this.refreshPaymentRecordsAdapterList();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            this.getPaymentRecordsListFailed("Please relogin.");
            return;
        }
        this.paginationId = 1;
        this.getPaymentRecords();
    }

    private void getPaymentRecords() {
        this.startLoadingSpinner();
        this.paginationHasNextPage = false;
        if (this.isShowingAllPaymentRecords()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_LANDLORD_PAYMENT_RECORDS + "?" + Constants.PAGE + "=" + this.paginationId, null, response -> {
                try {
                    if (!response.has("data")) {
                        getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                        return;
                    }
                    JSONArray dataJsonArray = response.getJSONArray("data");
                    JSONObject dataJsonObject;
                    JSONObject dataFieldsJsonObject;
                    JSONObject dataFieldsSenderJsonObject;
                    JSONObject dataFieldsRecipientJsonObject;
                    JSONObject dataFieldsAssetJsonObject;
                    JSONObject dataFieldsPaymentDescriptionJsonObject;
                    List<PaymentRecordsListItem> paymentRecordsListItemList = new ArrayList<>();
                    for (int i = dataJsonArray.length() - 1; i >= 0; i--) {
                        dataJsonObject = dataJsonArray.getJSONObject(i);
                        if (!dataJsonObject.has("id")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataJsonObject.has("fields")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        dataFieldsJsonObject = dataJsonObject.getJSONObject("fields");
                        if (!dataFieldsJsonObject.has("sender")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("recipient")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("asset")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsJsonObject.has("payment_description")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        dataFieldsSenderJsonObject = dataFieldsJsonObject.getJSONObject("sender");
                        if (!dataFieldsSenderJsonObject.has("sender_name")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsSenderJsonObject.has("sender_type")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsSenderJsonObject.has("id")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (dataFieldsSenderJsonObject.isNull("sender_name")) {
                            continue;
                        }
                        if (dataFieldsSenderJsonObject.isNull("sender_type")) {
                            continue;
                        }
                        if (dataFieldsSenderJsonObject.isNull("id")) {
                            continue;
                        }
                        dataFieldsRecipientJsonObject = dataFieldsJsonObject.getJSONObject("recipient");
                        if (!dataFieldsRecipientJsonObject.has("recipient_name")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsRecipientJsonObject.has("recipient_type")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsRecipientJsonObject.has("id")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (dataFieldsRecipientJsonObject.isNull("recipient_name")) {
                            continue;
                        }
                        if (dataFieldsRecipientJsonObject.isNull("recipient_type")) {
                            continue;
                        }
                        if (dataFieldsRecipientJsonObject.isNull("id")) {
                            continue;
                        }
                        dataFieldsAssetJsonObject = dataFieldsJsonObject.getJSONObject("asset");
                        if (!dataFieldsAssetJsonObject.has("asset_nickname")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsAssetJsonObject.has("id")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (dataFieldsAssetJsonObject.isNull("asset_nickname")) {
                            continue;
                        }
                        if (dataFieldsAssetJsonObject.isNull("id")) {
                            continue;
                        }
                        dataFieldsPaymentDescriptionJsonObject = dataFieldsJsonObject.getJSONObject("payment_description");
                        if (!dataFieldsPaymentDescriptionJsonObject.has("payment_description")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsPaymentDescriptionJsonObject.has("amount")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsPaymentDescriptionJsonObject.has("created_at")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsPaymentDescriptionJsonObject.has("payment_type")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsPaymentDescriptionJsonObject.has("currency_iso")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        if (!dataFieldsPaymentDescriptionJsonObject.has("cash_flow_direction")) {
                            getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                            return;
                        }
                        paymentRecordsListItemList.add(new PaymentRecordsListItem(
                                dataJsonObject.getInt("id"),
                                dataFieldsPaymentDescriptionJsonObject.getString("payment_description"),
                                dataFieldsSenderJsonObject.getInt("id"),
                                dataFieldsSenderJsonObject.getString("sender_name"),
                                dataFieldsRecipientJsonObject.getInt("id"),
                                dataFieldsRecipientJsonObject.getString("recipient_name"),
                                true,
//                                dataFieldsPaymentDescriptionJsonObject.getBoolean("direction"),
                                dataFieldsPaymentDescriptionJsonObject.getString("payment_type"),
                                dataFieldsPaymentDescriptionJsonObject.getString("created_at"),
                                CurrencyConverter.convertCurrency(dataFieldsJsonObject.getString("currency_iso")) + dataFieldsPaymentDescriptionJsonObject.getString("amount")));
                    }
                    getPaymentRecordsListSuccess(paymentRecordsListItemList);
//                    updatePropertyNameToPropertyExpensesListItem(propertyExpensesListItemList);
                    if (!response.has("meta")) {
                        return;
                    }
                    JSONObject metaJsonObject = response.getJSONObject("meta");
                    if (!metaJsonObject.has("current_page")) {
                        return;
                    }
                    if (!metaJsonObject.has("last_page")) {
                        return;
                    }
                    if (metaJsonObject.getInt("last_page") > metaJsonObject.getInt("current_page")) {
                        this.paginationHasNextPage = true;
                        this.paginationId += 1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getPaymentRecordsListFailed(Constants.ERROR_COMMON);
                }
            }, error -> getPaymentRecordsListFailed(Constants.ERROR_COMMON)) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if (SplashActivity.SESSION_ID.isEmpty()) {
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        SplashActivity.SESSION_ID = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, "");
                    }
                    Map<String, String> headerParams = new HashMap<>();
                    headerParams.put("Accept", "application/json");
                    headerParams.put("Content-Type", "application/json");
                    headerParams.put("X-Requested-With", "XMLHttpRequest");
                    headerParams.put("Authorization", SplashActivity.SESSION_ID);
                    return headerParams;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.requestQueue.add(jsonObjectRequest);
        }
    }

    private void getPaymentRecordsListSuccess(List<PaymentRecordsListItem> paymentRecordsListItemList) {
        this.stopLoadingSpinner();
        for (int i = 0; i < paymentRecordsListItemList.size(); i++) {
            this.addPaymentRecordsItemIntoList(paymentRecordsListItemList.get(i));
        }
    }

    private void getPaymentRecordsListFailed(String paymentRecordsListFailed) {
        this.stopLoadingSpinner();
        AlertDialog.Builder paymentRecordsListFailedDialog = new AlertDialog.Builder(this);
        paymentRecordsListFailedDialog.setCancelable(false);
        paymentRecordsListFailedDialog.setTitle("Payment Records List Failed");
        paymentRecordsListFailedDialog.setMessage(paymentRecordsListFailed);
        paymentRecordsListFailedDialog.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        paymentRecordsListFailedDialog.create().show();
    }

    private boolean isShowingAllPaymentRecords() {
        if (this.listPaymentRecordsType == null) {
            return false;
        } else {
            return this.listPaymentRecordsType.equals(Constants.INTENT_EXTRA_LIST_ALL);
        }
    }

    private void refreshPaymentRecordsAdapterList() {
        if (this.paymentRecordsListAdapter == null) {
            return;
        }
        this.paymentRecordsListAdapter.clear();
    }

    private void addPaymentRecordsItemIntoList(PaymentRecordsListItem paymentRecordsListItem) {
        if (this.paymentRecordsListAdapter == null) {
            return;
        }
        this.paymentRecordsListAdapter.add(paymentRecordsListItem);
    }

    private void startLoadingSpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.VISIBLE);
        this.loadingSpinner.setVisibility(View.VISIBLE);
        this.paymentRecordsListAddRecordsButton.setEnabled(false);
    }

    private void stopLoadingSpinner() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        this.backgroundView.setVisibility(View.GONE);
        this.loadingSpinner.setVisibility(View.GONE);
        this.paymentRecordsListAddRecordsButton.setEnabled(true);
    }
}
