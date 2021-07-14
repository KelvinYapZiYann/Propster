package com.propster.content;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.propster.R;
import com.propster.login.SplashActivity;
import com.propster.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContentActivity extends AppCompatActivity {

    private ViewPager2 contentTabPager;
    private ContentTabPageAdapter contentTabPageAdapter;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        boolean firstTime;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            firstTime = false;
        } else {
            firstTime = extras.getBoolean(Constants.INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN);
        }

        this.requestQueue = Volley.newRequestQueue(this);

        Toolbar mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        mainToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getItemId() == R.id.mainMenuUser) {
//                    Intent userProfileIntent = new Intent(ContentActivity.this, UserProfileActivity.class);
//                    startActivityForResult(userProfileIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
//                } else
                if (item.getItemId() == R.id.mainMenuNotification) {
                    Intent notificationIntent = new Intent(ContentActivity.this, NotificationActivity.class);
                    startActivityForResult(notificationIntent, Constants.REQUEST_CODE_SWITCH_ROLE);
                }
                return false;
            }
        });

        this.contentTabPager = findViewById(R.id.contentTabPager);
        this.contentTabPageAdapter = new ContentTabPageAdapter(this, firstTime);
        this.contentTabPager.setAdapter(this.contentTabPageAdapter);

        TabLayout tabLayout = findViewById(R.id.contentTabLayout);
        new TabLayoutMediator(tabLayout, this.contentTabPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.dashboard);
                        tab.setIcon(R.drawable.home_ic);
                        break;
                    case 1:
                        tab.setText(R.string.report);
                        tab.setIcon(R.drawable.report_ic);
                        break;
                    case 2:
                        tab.setText(R.string.manage);
                        tab.setIcon(R.drawable.manage_ic);
                        break;
                    case 3:
                        tab.setText(R.string.community);
                        tab.setIcon(R.drawable.message_ic);
                        break;
                    case 4:
                        tab.setText(R.string.more);
                        tab.setIcon(R.drawable.more_ic);
                        break;
                }
            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.home_selected_ic);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.report_selected_ic);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.manage_selected_ic);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.message_selected_ic);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.more_selected_ic);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (firstTime) {
            this.contentTabPager.setCurrentItem(2);
        }

        this.getInitializeParameters();
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
//        if (requestCode == Constants.REQUEST_CODE_SWITCH_ROLE) {
//            if (resultCode == Activity.RESULT_OK) {
//                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//                int role = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
//                if (role == Constants.ROLE_LANDLORD) {
//                    this.contentTabPageAdapter.setRole(Constants.ROLE_LANDLORD);
//                } else {
//                    this.contentTabPageAdapter.setRole(Constants.ROLE_TENANT);
//                }
//                this.contentTabPageAdapter.reloadContentFragments();
//                if (this.contentTabPager.getCurrentItem() == 0) {
//                    this.contentTabPager.setCurrentItem(1);
//                }
//                this.contentTabPager.setCurrentItem(0);
//            }
//        }
    }

    private void getInitializeParameters() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SESSION_ID, null);
        if (sessionId == null) {
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_USER, null, response -> {
            try {
                if (!response.has("data")) {
                    return;
                }
                JSONObject dataJsonObject = response.getJSONObject("data");
                if (!dataJsonObject.has("fields")) {
                    return;
                }
                JSONObject dataFieldsJsonObject = response.getJSONObject("fields");
                if (!dataFieldsJsonObject.has("selected_role")) {
                    return;
                }
                String selectedRole = dataFieldsJsonObject.getString("selected_role");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (selectedRole.equals("TENANT")) {
                    editor.putInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_TENANT);
                } else {
                    editor.putInt(Constants.SHARED_PREFERENCES_ROLE, Constants.ROLE_LANDLORD);
                }
                editor.apply();
            } catch (JSONException ignored) {}
        }, error -> {

        }) {
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
